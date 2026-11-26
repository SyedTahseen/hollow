package com.hollow.vpn.vpn

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.hollow.vpn.MainActivity
import com.hollow.vpn.R
import com.hollow.vpn.data.VpnPreferencesStore

/** Home-screen widget: shows connection state and toggles the VPN (ported from Outrun's widget). */
class HollowWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { manager.updateAppWidget(it, build(context, manager, it)) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, manager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle
    ) {
        manager.updateAppWidget(appWidgetId, build(context, manager, appWidgetId))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) toggle(context.applicationContext)
    }

    companion object {
        const val ACTION_TOGGLE = "com.hollow.vpn.widget.TOGGLE"

        private val GREEN = 0xFF2EE59D.toInt()
        private val WHITE = Color.WHITE

        fun widgetCount(context: Context): Int = runCatching {
            val app = context.applicationContext
            AppWidgetManager.getInstance(app)
                .getAppWidgetIds(ComponentName(app, HollowWidget::class.java)).size
        }.getOrDefault(0)

        /** Asks the launcher to pin the widget. Returns false when pinning isn't supported. */
        fun requestPin(context: Context): Boolean {
            val manager = AppWidgetManager.getInstance(context)
            return manager.isRequestPinAppWidgetSupported && runCatching {
                manager.requestPinAppWidget(ComponentName(context, HollowWidget::class.java), null, null)
            }.getOrDefault(false)
        }

        fun refresh(context: Context) {
            val app = context.applicationContext
            val manager = AppWidgetManager.getInstance(app)
            val ids = manager.getAppWidgetIds(ComponentName(app, HollowWidget::class.java))
            if (ids.isEmpty()) return
            ids.forEach { manager.updateAppWidget(it, build(app, manager, it)) }
        }

        private fun build(ctx: Context, manager: AppWidgetManager, widgetId: Int): RemoteViews {
            val heightDp = manager.getAppWidgetOptions(widgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
            val compact = heightDp in 1..64
            val running = TunnelControl.isRunning()
            val snapshot = VpnPreferencesStore(ctx).loadActiveTunnelSnapshot()

            val title = ctx.getString(if (running) R.string.widget_title_on else R.string.widget_title_off)
            val sub = snapshot?.country?.takeIf { it.isNotBlank() }
                ?: if (snapshot != null) snapshot.host else ctx.getString(R.string.widget_sub_choose_server)

            val views = RemoteViews(ctx.packageName, R.layout.widget_hollow)
            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_sub, sub)
            views.setViewVisibility(R.id.widget_sub, if (compact) View.GONE else View.VISIBLE)
            views.setInt(R.id.widget_power, "setColorFilter", if (running) GREEN else WHITE)

            views.setOnClickPendingIntent(R.id.widget_root, openApp(ctx, 0))
            val needsApp = !running && TunnelControl.needsApp(ctx, snapshot)
            views.setOnClickPendingIntent(
                R.id.widget_power,
                if (needsApp) openApp(ctx, 1) else PendingIntent.getBroadcast(
                    ctx, 2,
                    Intent(ctx, HollowWidget::class.java).setAction(ACTION_TOGGLE),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            return views
        }

        private fun openApp(ctx: Context, req: Int) = PendingIntent.getActivity(
            ctx, req,
            Intent(ctx, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        private fun toggle(ctx: Context) {
            runCatching {
                if (TunnelControl.isRunning()) {
                    TunnelControl.disconnect(ctx)
                } else {
                    val snapshot = VpnPreferencesStore(ctx).loadActiveTunnelSnapshot()
                    if (snapshot == null || TunnelControl.needsApp(ctx, snapshot)) {
                        ctx.startActivity(
                            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    } else {
                        TunnelControl.connect(ctx, snapshot)
                    }
                }
            }
            refresh(ctx)
        }
    }
}
