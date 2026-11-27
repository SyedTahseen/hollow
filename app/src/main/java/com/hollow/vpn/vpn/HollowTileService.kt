package com.hollow.vpn.vpn

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.VpnService
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hollow.vpn.MainActivity
import com.hollow.vpn.R
import com.hollow.vpn.data.VpnPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HollowTileService : TileService() {

    private var scope: CoroutineScope? = null
    private var collectorJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        syncTile(XrayVpnService.isServiceRunning.value)
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        collectorJob = scope?.launch {
            XrayVpnService.isServiceRunning.collect { running ->
                syncTile(running)
            }
        }
    }

    override fun onStopListening() {
        collectorJob?.cancel()
        collectorJob = null
        scope?.cancel()
        scope = null
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        val isRunning = XrayVpnService.isServiceRunning.value
        if (isRunning) {
            TunnelControl.disconnect(this)
            syncTile(false)
            return
        }

        val snapshot = VpnPreferencesStore(applicationContext).loadActiveTunnelSnapshot()
        if (snapshot == null || TunnelControl.needsApp(this, snapshot)) {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pending = android.app.PendingIntent.getActivity(
                    this,
                    0,
                    launchIntent,
                    android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
                )
                startActivityAndCollapse(pending)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(launchIntent)
            }
            return
        }

        TunnelControl.connect(this, snapshot)
        syncTile(true)
    }

    private fun syncTile(isRunning: Boolean) {
        val tile = qsTile ?: return
        val snapshot = VpnPreferencesStore(applicationContext).loadActiveTunnelSnapshot()
        tile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_vpn_shield)
        if (isRunning) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = snapshot?.country?.takeIf { it.isNotBlank() } ?: getString(R.string.app_name)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.app_name)
        }
        tile.updateTile()
    }

    companion object {
        fun requestTileUpdate(context: Context) {
            try {
                requestListeningState(
                    context.applicationContext,
                    ComponentName(context.applicationContext, HollowTileService::class.java)
                )
            } catch (_: Exception) {
            }
            HollowWidget.refresh(context)
        }
    }
}
