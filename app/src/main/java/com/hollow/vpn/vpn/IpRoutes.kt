package com.hollow.vpn.vpn

import java.math.BigInteger
import java.net.Inet6Address
import java.net.InetAddress

object IpRoutes {

    private val ipv4 = Regex("^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$")
    private val literal = Regex("^[0-9a-fA-F:.]+$")
    private val loopback = listOf("127.0.0.0/8", "::1/128")

    fun parse(text: String): String? {
        val t = text.trim()
        if (t.isEmpty()) return null
        val addr = t.substringBefore('/')
        val v6 = ':' in addr
        val max = if (v6) 128 else 32
        val prefix = if ('/' in t) {
            t.substringAfter('/').toIntOrNull()?.takeIf { it in 0..max } ?: return null
        } else max
        val ok = if (v6) {
            literal.matches(addr) && runCatching { InetAddress.getByName(addr) is Inet6Address }.getOrDefault(false)
        } else ipv4.matches(addr)
        if (!ok) return null
        return if (prefix == max) addr else "$addr/$prefix"
    }

    fun isIpv6(entry: String): Boolean = ':' in entry.substringBefore('/')

    fun prefixLength(entry: String): Int =
        entry.substringAfter('/', if (isIpv6(entry)) "128" else "32").toInt()

    private class Block(val v6: Boolean, val start: BigInteger, val end: BigInteger)

    private fun bits(v6: Boolean) = if (v6) 128 else 32
    private fun topOf(v6: Boolean) = BigInteger.ONE.shiftLeft(bits(v6)) - BigInteger.ONE

    private fun parseBlock(cidr: String): Block? {
        val addr = cidr.substringBefore('/').trim().substringBefore('%')
        if (addr.isEmpty() || !literal.matches(addr)) return null
        val ip = runCatching { InetAddress.getByName(addr) }.getOrNull() ?: return null
        val raw = ip.address
        val v6 = raw.size == 16
        val len = cidr.substringAfter('/', "").trim().toIntOrNull() ?: bits(v6)
        if (len !in 0..bits(v6)) return null
        val value = BigInteger(1, raw)
        val size = BigInteger.ONE.shiftLeft(bits(v6) - len)
        val start = value.andNot(size - BigInteger.ONE)
        return Block(v6, start, start + size - BigInteger.ONE)
    }

    private fun format(v6: Boolean, value: BigInteger): String {
        val n = bits(v6) / 8
        val src = value.toByteArray()
        val raw = ByteArray(n)
        for (i in 0 until minOf(n, src.size)) raw[n - 1 - i] = src[src.size - 1 - i]
        return InetAddress.getByAddress(raw).hostAddress.orEmpty().substringBefore('%')
    }

    fun routes(v6: Boolean, exclusions: List<String>): List<Pair<String, Int>> {
        val plain = complement(v6, exclusions)
        val loop = loopback.mapNotNull { parseBlock(it) }.first { it.v6 == v6 }
        val hitsLoopback = plain.any { (addr, len) ->
            val a = parseBlock("$addr/$len") ?: return@any false
            a.start <= loop.end && a.end >= loop.start
        }
        return if (hitsLoopback) complement(v6, exclusions + loopback) else plain
    }

    private fun complement(v6: Boolean, exclusions: List<String>): List<Pair<String, Int>> {
        val ex = exclusions.mapNotNull { parseBlock(it) }.filter { it.v6 == v6 }.sortedBy { it.start }
        val merged = ArrayList<Pair<BigInteger, BigInteger>>()
        for (b in ex) {
            val last = merged.lastOrNull()
            if (last != null && b.start <= last.second + BigInteger.ONE) {
                if (b.end > last.second) merged[merged.lastIndex] = last.first to b.end
            } else merged += b.start to b.end
        }
        val out = ArrayList<Pair<String, Int>>()
        val top = topOf(v6)
        fun emit(from: BigInteger, to: BigInteger) {
            var lo = from
            while (lo <= to) {
                var size = if (lo.signum() == 0) bits(v6) else lo.lowestSetBit
                while (size > 0 && lo + BigInteger.ONE.shiftLeft(size) - BigInteger.ONE > to) size--
                out += format(v6, lo) to (bits(v6) - size)
                lo += BigInteger.ONE.shiftLeft(size)
            }
        }
        var cur = BigInteger.ZERO
        for ((s, e) in merged) {
            if (s > cur) emit(cur, s - BigInteger.ONE)
            cur = e + BigInteger.ONE
        }
        if (cur <= top) emit(cur, top)
        return out
    }
}
