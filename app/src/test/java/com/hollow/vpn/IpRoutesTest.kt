package com.hollow.vpn

import com.hollow.vpn.vpn.IpRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IpRoutesTest {

    @Test
    fun parse_acceptsIpv4Ipv6AndPrefixes() {
        assertEquals("192.0.2.1", IpRoutes.parse(" 192.0.2.1 "))
        assertEquals("10.0.0.0/8", IpRoutes.parse("10.0.0.0/8"))
        assertEquals("192.0.2.1", IpRoutes.parse("192.0.2.1/32"))
        assertEquals("2001:db8::/32", IpRoutes.parse("2001:db8::/32"))
    }

    @Test
    fun parse_rejectsGarbage() {
        assertNull(IpRoutes.parse(""))
        assertNull(IpRoutes.parse("256.1.1.1"))
        assertNull(IpRoutes.parse("1.2.3"))
        assertNull(IpRoutes.parse("10.0.0.0/33"))
        assertNull(IpRoutes.parse("example.com"))
        assertNull(IpRoutes.parse("2001:zz::1"))
    }

    @Test
    fun routes_coverEverythingExceptTheExcludedRange() {
        val routes = IpRoutes.routes(v6 = false, exclusions = listOf("192.168.0.0/16"))
        assertFalse(routes.isEmpty())
        routes.forEach { (addr, len) ->
            val start = addr.split('.').fold(0L) { acc, p -> acc * 256 + p.toLong() }
            val size = 1L shl (32 - len)
            val end = start + size - 1
            val exStart = (192L shl 24) + (168L shl 16)
            val exEnd = exStart + 65535
            assertTrue("$addr/$len overlaps 192.168.0.0/16", end < exStart || start > exEnd)
        }
    }

    @Test
    fun routes_neverIncludeLoopback() {
        val routes = IpRoutes.routes(v6 = false, exclusions = listOf("10.0.0.0/8"))
        routes.forEach { (addr, len) ->
            val first = addr.substringBefore('.').toInt()
            val size = 1L shl (32 - len)
            val start = addr.split('.').fold(0L) { acc, p -> acc * 256 + p.toLong() }
            val end = start + size - 1
            val loopStart = 127L shl 24
            val loopEnd = loopStart + (1L shl 24) - 1
            assertTrue("$addr/$len covers loopback", end < loopStart || start > loopEnd)
            assertTrue(first in 0..255)
        }
    }

    @Test
    fun prefixLength_defaultsToHostRoute() {
        assertEquals(32, IpRoutes.prefixLength("192.0.2.1"))
        assertEquals(128, IpRoutes.prefixLength("2001:db8::1"))
        assertEquals(24, IpRoutes.prefixLength("10.1.2.0/24"))
    }
}
