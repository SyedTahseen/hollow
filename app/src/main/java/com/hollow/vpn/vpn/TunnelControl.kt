package com.hollow.vpn.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import com.hollow.vpn.data.ActiveTunnelSnapshot
import com.hollow.vpn.data.VpnPreferencesStore

/** Connect / disconnect entry points shared by the Quick Settings tile and the home-screen widget. */
internal object TunnelControl {

    fun isRunning(): Boolean = XrayVpnService.isServiceRunning.value

    /** True when there is no saved server yet, or Android still needs to ask for VPN permission. */
    fun needsApp(context: Context, snapshot: ActiveTunnelSnapshot?): Boolean {
        val store = VpnPreferencesStore(context.applicationContext)
        val needsConsent = (snapshot?.tunMode ?: true) &&
            !store.loadSettings().rootMode &&
            VpnService.prepare(context) != null
        return snapshot == null || needsConsent
    }

    fun disconnect(context: Context) {
        val stopIntent = Intent(context, XrayVpnService::class.java).apply {
            action = XrayVpnService.ACTION_DISCONNECT
        }
        context.startService(stopIntent)
    }

    fun connect(context: Context, snapshot: ActiveTunnelSnapshot) {
        val startIntent = Intent(context, XrayVpnService::class.java).apply {
            action = XrayVpnService.ACTION_CONNECT
            putExtra(XrayVpnService.EXTRA_SERVER_COUNTRY, snapshot.country)
            putExtra(XrayVpnService.EXTRA_SERVER_HOST, snapshot.host)
            putExtra(XrayVpnService.EXTRA_SERVER_PORT, snapshot.port)
            putExtra(XrayVpnService.EXTRA_SERVER_PROTOCOL, snapshot.protocol)
            putExtra(XrayVpnService.EXTRA_XRAY_JSON, snapshot.xrayJson)
            putExtra(XrayVpnService.EXTRA_TUN_MODE, snapshot.tunMode)
            putExtra(XrayVpnService.EXTRA_ENABLE_IPV6, snapshot.enableIpv6)
            putExtra(XrayVpnService.EXTRA_DNS_MODE, snapshot.dnsMode)
            putExtra(XrayVpnService.EXTRA_ROUTING_MODE, snapshot.routingMode)
            putExtra(XrayVpnService.EXTRA_PER_APP_PROXY_ENABLED, snapshot.perAppProxyEnabled)
            putExtra(XrayVpnService.EXTRA_BYPASS_SELECTED_APPS, snapshot.bypassSelectedApps)
            putStringArrayListExtra(
                XrayVpnService.EXTRA_SELECTED_APP_PACKAGES,
                ArrayList(snapshot.selectedAppPackages)
            )
            putExtra(XrayVpnService.EXTRA_NETSHIELD_ENABLED, snapshot.netShieldEnabled)
            putExtra(XrayVpnService.EXTRA_BLOCK_ADULT_CONTENT, snapshot.blockAdultContent)
            putStringArrayListExtra(XrayVpnService.EXTRA_SPLIT_EXCLUDE_IPS, ArrayList(snapshot.splitExcludeIps))
            putStringArrayListExtra(XrayVpnService.EXTRA_SPLIT_INCLUDE_IPS, ArrayList(snapshot.splitIncludeIps))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startIntent)
        } else {
            context.startService(startIntent)
        }
    }
}
