package com.hollow.vpn.vpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import com.hollow.vpn.data.VpnDatabase
import com.hollow.vpn.data.VpnPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext
        val settings = VpnPreferencesStore(app).loadSettings()
        if (!settings.autoConnectBoot) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (settings.tunMode && !settings.rootMode && VpnService.prepare(app) != null) {
                    XrayVpnService.appendLog("Boot auto-connect skipped: VPN permission not granted yet (connect once from the app)")
                    return@launch
                }
                val server = VpnDatabase.getInstance(app).vpnDao().getAllServersOnce().firstOrNull { it.isSelected }
                if (server == null) {
                    XrayVpnService.appendLog("Boot auto-connect skipped: no server selected")
                    return@launch
                }
                val fetcher = V2RayParserAndFetcher()
                val json = fetcher.applyRuntimeSettingsToXrayJson(fetcher.resolveConfigJson(server), settings)
                XrayVpnService.appendLog("Auto-connecting after boot -> ${server.countryName}")
                val start = XrayVpnService.connectIntent(app, server, settings, json)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) app.startForegroundService(start)
                else app.startService(start)
            } catch (t: Throwable) {
                XrayVpnService.appendLog("Boot auto-connect failed: ${t.javaClass.simpleName}: ${t.message}")
            } finally {
                pending.finish()
            }
        }
    }
}
