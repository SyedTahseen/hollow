package com.hollow.vpn.data

import android.util.Base64
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Field-by-field editor for a server's share link (vless://, vmess://, trojan://, ss://, hysteria2://,
 * wireguard://, socks5://). Ported from Outrun so the "Edit server" window behaves the same.
 */
data class EditModel(
    val protocol: String,
    /** The scheme exactly as written in the link (hy2, socks5, ...), so a saved link keeps it. */
    val scheme: String,
    val name: String,
    val host: String,
    val port: String,
    val user: String,
    val pass: String,
    val params: List<Pair<String, String>>,
    /** VMess only: JSON values that were numbers (n) or booleans (b), so they are saved with the same type. */
    val kinds: Map<String, Char> = emptyMap()
)

class EditLabels(val user: String, val pass: String?)

object LinkEditor {
    private val vmessFixed = setOf("ps", "add", "port", "id")

    fun protocolOf(link: String): String? {
        val l = link.trim().lowercase()
        return when {
            l.startsWith("vless://") -> "vless"
            l.startsWith("trojan://") -> "trojan"
            l.startsWith("ss://") -> "ss"
            l.startsWith("vmess://") -> "vmess"
            l.startsWith("hysteria2://") || l.startsWith("hy2://") -> "hysteria2"
            l.startsWith("wireguard://") || l.startsWith("wg://") -> "wireguard"
            l.startsWith("socks5://") || l.startsWith("socks5h://") || l.startsWith("socks://") -> "socks"
            else -> null
        }
    }

    fun labels(protocol: String): EditLabels = when (protocol) {
        "vless", "vmess" -> EditLabels("UUID", null)
        "trojan" -> EditLabels("Password", null)
        "ss" -> EditLabels("Method (for example aes-256-gcm)", "Password")
        "hysteria2" -> EditLabels("Password, or username", "Password (only if the first field is a username)")
        "wireguard" -> EditLabels("Private key", null)
        else -> EditLabels("Username (optional)", "Password (optional)")
    }

    fun describeProtocol(protocol: String) = when (protocol) {
        "vless" -> "VLESS"
        "vmess" -> "VMess"
        "trojan" -> "Trojan"
        "ss" -> "Shadowsocks"
        "hysteria2" -> "Hysteria2"
        "wireguard" -> "WireGuard"
        "socks" -> "SOCKS5 proxy"
        else -> "Proxy"
    }

    fun paramsTitle(protocol: String) = if (protocol == "vmess") "Settings" else "Parameters"

    fun parse(link: String): EditModel? = runCatching {
        val clean = link.trim()
        when (protocolOf(clean)) {
            null -> null
            "ss" -> parseSs(clean)
            "vmess" -> parseVmess(clean)
            else -> parseUri(clean)
        }
    }.getOrNull()

    /** Builds the new link from the edited model. Returns (link, null) or (null, error message). */
    fun finish(m: EditModel): Pair<String?, String?> {
        if (m.host.isBlank()) return null to "Enter the server address"
        val portNumber = m.port.trim().takeWhile { it.isDigit() }.toIntOrNull()
        if (portNumber == null || portNumber !in 1..65535) return null to "Enter a port between 1 and 65535"
        val link = runCatching { build(m) }.getOrNull()
            ?: return null to "These details don't make a valid link"
        if (protocolOf(link) != m.protocol) return null to "These details don't make a valid link"
        return link to null
    }

    fun build(m: EditModel): String = when (m.protocol) {
        "ss" -> buildSs(m)
        "vmess" -> buildVmess(m)
        else -> buildUri(m)
    }

    private fun dec(s: String) = URLDecoder.decode(s.replace("+", "%2B"), "UTF-8")

    private fun decodeB64(s: String): String? = try {
        var t = s.filter { !it.isWhitespace() }.replace('-', '+').replace('_', '/').trimEnd('=')
        t += "=".repeat((4 - t.length % 4) % 4)
        String(Base64.decode(t, Base64.DEFAULT), Charsets.UTF_8)
    } catch (_: Exception) {
        null
    }

    private fun requireB64(s: String): String = decodeB64(s) ?: error("Invalid base64 data in link")

    private fun fragment(link: String) =
        link.substringAfter('#', "").takeIf { it.isNotEmpty() }?.let { dec(it) } ?: ""

    private val authorityRe =
        Regex("^[A-Za-z0-9]+://(?:([^@/?#]*)@)?(\\[[^\\]]+\\]|[^:/?#,]+)(?::([^/?#]*))?")

    private fun parseUri(link: String): EditModel {
        val protocol = protocolOf(link) ?: error("Unsupported link")
        val m = authorityRe.find(link) ?: error("Invalid link")
        val rawUser = m.groupValues[1]
        val user = dec(rawUser.substringBefore(':'))
        val pass = if (':' in rawUser) dec(rawUser.substringAfter(':')) else ""
        val host = m.groupValues[2].trim('[', ']')
        var spec = m.groupValues[3]
        if (spec.isEmpty()) {
            spec = when (protocol) {
                "socks" -> "1080"
                "hysteria2" -> "443"
                else -> ""
            }
        }
        val query = link.substringAfter('?', "").substringBefore('#').split('&')
            .filter { it.contains('=') }
            .map { it.substringBefore('=') to dec(it.substringAfter('=')) }
        return EditModel(protocol, link.substringBefore("://"), fragment(link), host, spec, user, pass, query)
    }

    private fun parseSs(link: String): EditModel {
        var rest = link.removePrefix("ss://")
        if (rest.contains("#")) rest = rest.substringBefore("#")
        val queryPart = rest.substringAfter('?', "")
        rest = rest.substringBefore("?")
        val userInfo: String
        val hostPort: String
        if (rest.contains("@")) {
            val ui = rest.substringBeforeLast("@")
            userInfo = if (ui.contains(":")) URLDecoder.decode(ui, "UTF-8") else requireB64(ui)
            hostPort = rest.substringAfterLast("@")
        } else {
            val all = requireB64(rest)
            userInfo = all.substringBeforeLast("@")
            hostPort = all.substringAfterLast("@")
        }
        val host = hostPort.substringBeforeLast(":").trim('[', ']')
        val port = hostPort.substringAfterLast(":").toInt()
        val query = queryPart.split('&').filter { '=' in it }
            .map { it.substringBefore('=') to dec(it.substringAfter('=')) }
        return EditModel(
            "ss", "ss", fragment(link), host, port.toString(),
            userInfo.substringBefore(":"), userInfo.substringAfter(":", ""), query
        )
    }

    private fun parseVmess(link: String): EditModel {
        val j = JSONObject(requireB64(link.removePrefix("vmess://")))
        val kinds = HashMap<String, Char>()
        val params = ArrayList<Pair<String, String>>()
        for (k in j.keys()) {
            val v = j.get(k)
            if (v is Number) kinds[k] = 'n' else if (v is Boolean) kinds[k] = 'b'
            if (k !in vmessFixed) params += k to (if (v == JSONObject.NULL) "" else v.toString())
        }
        return EditModel(
            "vmess", "vmess", j.optString("ps"), j.getString("add"), j.get("port").toString(),
            j.optString("id"), "", params, kinds
        )
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8").replace("+", "%20")

    private fun hostPart(h: String) = h.trim().let { if (':' in it && !it.startsWith("[")) "[$it]" else it }

    private fun query(params: List<Pair<String, String>>): String {
        val ps = params.map { it.first.trim() to it.second }.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
        return if (ps.isEmpty()) "" else "?" + ps.joinToString("&") { "${enc(it.first)}=${enc(it.second)}" }
    }

    private fun buildUri(m: EditModel): String {
        val sb = StringBuilder(m.scheme).append("://")
        if (m.user.isNotEmpty() || m.pass.isNotEmpty()) {
            sb.append(enc(m.user))
            if (m.pass.isNotEmpty()) sb.append(':').append(enc(m.pass))
            sb.append('@')
        }
        sb.append(hostPart(m.host))
        if (m.port.isNotBlank()) sb.append(':').append(m.port.trim())
        sb.append(query(m.params))
        if (m.name.isNotBlank()) sb.append('#').append(enc(m.name.trim()))
        return sb.toString()
    }

    private fun buildSs(m: EditModel): String {
        val cred = Base64.encodeToString(
            "${m.user}:${m.pass}".toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
        )
        val sb = StringBuilder("ss://").append(cred).append('@').append(hostPart(m.host)).append(':').append(m.port.trim())
        sb.append(query(m.params))
        if (m.name.isNotBlank()) sb.append('#').append(enc(m.name.trim()))
        return sb.toString()
    }

    private fun buildVmess(m: EditModel): String {
        val j = JSONObject()
        j.put("ps", m.name.trim())
        j.put("add", m.host.trim())
        j.put("port", typed(m, "port", m.port.trim()))
        j.put("id", m.user.trim())
        for ((k, v) in m.params) {
            val key = k.trim()
            if (key.isNotEmpty() && key !in vmessFixed) j.put(key, typed(m, key, v))
        }
        return "vmess://" + Base64.encodeToString(j.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun typed(m: EditModel, key: String, v: String): Any = when (m.kinds[key]) {
        'n' -> {
            val l = v.toLongOrNull()
            if (l != null) (if (l >= Int.MIN_VALUE && l <= Int.MAX_VALUE) l.toInt() else l) else (v.toDoubleOrNull() ?: v)
        }
        'b' -> if (v.equals("true", ignoreCase = true)) true else if (v.equals("false", ignoreCase = true)) false else v
        else -> v
    }
}
