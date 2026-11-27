package com.hollow.vpn.data

import android.util.Base64
import java.net.URLDecoder

data class SubscriptionMeta(
    val title: String = "",
    val upload: Long = 0L,
    val download: Long = 0L,
    val total: Long = 0L,
    val expire: Long = 0L,
    val intervalHours: Int = 0,
    val announce: String = ""
) {
    val used: Long get() = upload + download
    val hasUsage: Boolean get() = total > 0L || used > 0L
    val isEmpty: Boolean
        get() = title.isBlank() && total == 0L && used == 0L && expire == 0L

    companion object {
        private val COMMENT_HEADER = Regex("^#\\s*([A-Za-z-]+)\\s*[:=]\\s*(.*)$")
        private val FILENAME = Regex("filename\\*?=(?:UTF-8'')?\"?([^\";]+)")

        fun parse(headers: Map<String, String>, body: String): SubscriptionMeta {
            val h = HashMap(headers)
            body.lineSequence().take(40).forEach { line ->
                val t = line.trim()
                if (t.startsWith("#")) {
                    COMMENT_HEADER.find(t)?.let { m -> h.putIfAbsent(m.groupValues[1].lowercase(), m.groupValues[2]) }
                }
            }

            var up = 0L
            var down = 0L
            var total = 0L
            var expire = 0L
            (h["subscription-userinfo"] ?: "").split(";").forEach { kv ->
                val key = kv.substringBefore("=").trim().lowercase()
                val value = kv.substringAfter("=", "").trim().toDoubleOrNull()?.toLong() ?: return@forEach
                when (key) {
                    "upload" -> up = value
                    "download" -> down = value
                    "total" -> total = value
                    "expire" -> expire = value
                }
            }

            val title = h["profile-title"]?.let { decodeValue(it) }
                ?: FILENAME.find(h["content-disposition"] ?: "")?.groupValues?.get(1)
                    ?.let { runCatching { URLDecoder.decode(it, "UTF-8") }.getOrDefault(it) }
                    ?.substringBeforeLast('.')
                ?: ""

            return SubscriptionMeta(
                title = title.trim(),
                upload = up,
                download = down,
                total = total,
                expire = expire,
                intervalHours = h["profile-update-interval"]?.trim()?.toDoubleOrNull()?.toInt() ?: 0,
                announce = h["announce"]?.let { decodeValue(it) }.orEmpty()
            )
        }

        private fun decodeValue(raw: String): String {
            val t = raw.trim()
            if (t.startsWith("base64:", ignoreCase = true)) {
                val decoded = runCatching {
                    String(Base64.decode(t.substring(7).trim(), Base64.DEFAULT), Charsets.UTF_8)
                }.getOrNull()
                return decoded?.takeIf { it.isNotBlank() } ?: t
            }
            return if (t.contains('%')) runCatching { URLDecoder.decode(t, "UTF-8") }.getOrDefault(t) else t
        }
    }
}
