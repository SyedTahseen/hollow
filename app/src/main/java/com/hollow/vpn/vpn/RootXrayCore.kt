package com.hollow.vpn.vpn

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RootXrayCore(private val context: Context) {

    companion object {
        const val IFACE = "hollow0"
        const val MARK = 0x7b000000
        private const val MARK_MASK = "0x7b000000/0xff000000"
        private const val TABLE = 2022
        private const val RULE_PREF = 9000
        private const val DIR = "xray-root"

        private fun q(s: String) = "'" + s.replace("'", "'\\''") + "'"

        fun binary(context: Context) = File(context.applicationInfo.nativeLibraryDir, "libxray.so")

        fun isAvailable(context: Context) = binary(context).exists()

        fun isRunning(context: Context): Boolean {
            val pid = File(File(context.filesDir, DIR), "core.pid")
            if (!pid.exists()) return false
            val r = RootShell.run(
                "P=\$(cat ${q(pid.absolutePath)} 2>/dev/null); grep -q libxray /proc/\$P/cmdline 2>/dev/null && echo alive",
                15_000
            )
            return "alive" in r.out
        }

        fun activeInterface(context: Context): String? {
            val cm = context.getSystemService(ConnectivityManager::class.java) ?: return null
            val network = cm.activeNetwork ?: return null
            val caps = cm.getNetworkCapabilities(network)
            if (caps == null || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) return null
            return cm.getLinkProperties(network)?.interfaceName
        }

        fun buildConfig(runtimeJson: String, iface: String, mtu: Int): String {
            val root = JSONObject(runtimeJson)
            root.optJSONArray("inbounds")?.let { inbounds ->
                for (i in 0 until inbounds.length()) {
                    val ib = inbounds.optJSONObject(i) ?: continue
                    if (ib.optString("protocol").equals("tun", true)) {
                        val st = ib.optJSONObject("settings") ?: JSONObject().also { ib.put("settings", it) }
                        st.put("name", IFACE).put("MTU", mtu)
                    }
                }
            }
            val outbounds = root.optJSONArray("outbounds") ?: JSONArray()
            for (i in 0 until outbounds.length()) {
                val ob = outbounds.optJSONObject(i) ?: continue
                val proto = ob.optString("protocol").lowercase()
                if (proto == "blackhole" || proto == "dns") continue
                val stream = ob.optJSONObject("streamSettings") ?: JSONObject().also { ob.put("streamSettings", it) }
                val sockopt = stream.optJSONObject("sockopt") ?: JSONObject().also { stream.put("sockopt", it) }
                sockopt.put("mark", MARK).put("interface", iface)
            }
            return root.toString()
        }

        private fun stopScript(dir: File): String {
            val pid = q(File(dir, "core.pid").absolutePath)
            return listOf(
                "if [ -s $pid ]; then",
                "  P=\$(cat $pid)",
                "  if grep -q libxray /proc/\$P/cmdline 2>/dev/null; then",
                "    kill -TERM \$P 2>/dev/null",
                "    i=0",
                "    while kill -0 \$P 2>/dev/null && [ \$i -lt 25 ]; do sleep 0.2; i=\$((i+1)); done",
                "    kill -0 \$P 2>/dev/null && kill -KILL \$P 2>/dev/null",
                "  fi",
                "  rm -f $pid",
                "fi",
                "for fam in -4 -6; do",
                "  ip \$fam rule show 2>/dev/null | grep 'lookup $TABLE' | cut -d: -f1 | while read n; do ip \$fam rule del pref \$n; done",
                "  ip \$fam route flush table $TABLE 2>/dev/null",
                "done",
                "ip link del $IFACE 2>/dev/null",
                "true"
            ).joinToString("\n")
        }
    }

    private val dir = File(context.filesDir, DIR)

    @Volatile private var running = false

    private fun lastLog(): String {
        val r = RootShell.run("tail -n 5 ${q(File(dir, "core.log").absolutePath)} 2>/dev/null", 5_000)
        return r.out.lines().filter { it.isNotBlank() }.takeLast(4).joinToString(" | ").ifEmpty { "no output" }
    }

    fun start(configJson: String, iface: String, mtu: Int, ipv6: Boolean) {
        val bin = binary(context)
        if (!bin.exists()) error("Xray binary (libxray.so) is not bundled in this build")
        dir.mkdirs()
        val cfg = File(dir, "config.json").apply { writeText(configJson) }
        RootShell.run(stopScript(dir), 20_000)

        val assets = (context.getExternalFilesDir("assets") ?: context.getDir("assets", 0)).absolutePath
        val launch = listOf(
            "cd ${q(dir.absolutePath)} || exit 1",
            "rm -f core.log core.pid",
            "XRAY_LOCATION_ASSET=${q(assets)} nohup ${q(bin.absolutePath)} run -c ${q(cfg.absolutePath)} > core.log 2>&1 &",
            "echo \$! > core.pid"
        ).joinToString("\n")
        val r = RootShell.run(launch)
        if (!r.ok) error("Root shell: ${r.out.ifEmpty { "failed to start Xray" }}")

        val pid = q(File(dir, "core.pid").absolutePath)
        val probe = "P=\$(cat $pid 2>/dev/null); kill -0 \$P 2>/dev/null && echo alive; [ -d /sys/class/net/$IFACE ] && echo iface"
        val deadline = System.currentTimeMillis() + 15_000
        var up = false
        while (System.currentTimeMillis() < deadline) {
            val out = RootShell.run(probe, 5_000).out
            if ("alive" !in out) {
                Thread.sleep(150)
                error("Xray exited: " + lastLog())
            }
            if ("iface" in out) { up = true; break }
            Thread.sleep(250)
        }
        if (!up) {
            stop()
            error("TUN interface did not come up: " + lastLog())
        }

        val setup = buildList {
            add("ip addr add 172.19.0.1/30 dev $IFACE 2>/dev/null")
            add("ip link set $IFACE up mtu $mtu")
            add("ip -4 route replace default dev $IFACE table $TABLE")
            add("ip -4 rule add pref $RULE_PREF not fwmark $MARK_MASK lookup $TABLE")
            if (ipv6) {
                add("ip -6 addr add fdfe:dcba:9876::1/126 dev $IFACE 2>/dev/null")
                add("ip -6 route replace default dev $IFACE table $TABLE")
                add("ip -6 rule add pref $RULE_PREF not fwmark $MARK_MASK lookup $TABLE")
            }
        }.joinToString("\n") + "\ntrue"
        val s = RootShell.run(setup, 15_000)
        if (!s.ok) {
            stop()
            error("Could not set up routing: ${s.out.ifEmpty { "ip command failed" }}")
        }
        flushConnections()
        running = true
    }

    fun totals(): Pair<Long, Long> {
        val r = RootShell.run(
            "cat /sys/class/net/$IFACE/statistics/tx_bytes /sys/class/net/$IFACE/statistics/rx_bytes 2>/dev/null",
            3_000
        )
        val n = r.out.lines().mapNotNull { it.trim().toLongOrNull() }
        return if (n.size >= 2) n[0] to n[1] else 0L to 0L
    }

    fun stop() {
        running = false
        RootShell.run(stopScript(dir), 20_000)
        flushConnections()
    }

    private fun flushConnections() {
        RootShell.run(
            listOf(
                "conntrack -F >/dev/null 2>&1",
                "ss -t -K state established >/dev/null 2>&1",
                "ndc resolver flushdefaultif >/dev/null 2>&1",
                "true"
            ).joinToString("\n"),
            10_000
        )
    }
}
