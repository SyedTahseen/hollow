package com.hollow.vpn.ui.screens

import android.net.Uri
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import com.hollow.vpn.data.AppLinks
import com.hollow.vpn.vpn.XrayVpnService
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hollow.vpn.vpn.HollowWidget
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.BuildConfig
import com.hollow.vpn.data.AppSettingsState
import com.hollow.vpn.data.AppUpdateState
import com.hollow.vpn.ui.components.HollowSwitch
import com.hollow.vpn.ui.components.HollowTopSquareButton
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowDisplay
import com.hollow.vpn.ui.theme.JetBrainsMonoFamily
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.NothingMotion
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingPillActiveBg
import com.hollow.vpn.ui.theme.NothingPillActiveIcon
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
import com.hollow.vpn.ui.theme.SpaceGroteskFamily
import com.hollow.vpn.ui.theme.nothingPressScale

/** Dialogs that still open on top of the settings list (log viewer, about sheet, update check). */
private enum class ActiveSettingDialog {
    XRAY_LOGS,
    ABOUT,
    CHECK_UPDATE
}

/** Every setting that has its own page where the value is changed. */
private enum class SettingsPage {
    NET_SHIELD,
    SPLIT_TUNNELING,
    SPLIT_IPS,
    USAGE_HISTORY,
    KILL_SWITCH,
    PRIVATE_DNS,
    PROTOCOL,
    ROUTING_MODE,
    SMART_SWITCH,
    STRICT_TLS,
    START_ON_OPEN,
    AUTO_UPDATE,
    LATENCY_TEST,
    ADVANCED,
    CUSTOM_DNS,
    MTU,
    APPEARANCE,
    HIDE_RECENTS
}

private val PROTOCOL_OPTIONS = listOf(
    SettingChoiceOption("Auto (V2Ray)", "Selects the fastest & stealthiest protocol automatically", "Auto (Recommended)"),
    SettingChoiceOption("VLESS (Reality)", "Modern TLS-fingerprint camouflage for strict networks", "VLESS (Reality)"),
    SettingChoiceOption("VLESS (WS / TCP)", "Low-overhead tunnel for everyday high-speed browsing", "VLESS (WS / TCP)"),
    SettingChoiceOption("VMess (WebSocket)", "Broadly compatible WebSocket fallback transport", "VMess (WebSocket)"),
    SettingChoiceOption("Trojan (TLS)", "Blends with standard HTTPS web traffic", "Trojan (TLS)"),
    SettingChoiceOption("Hysteria2", "QUIC-based high-throughput mode for streaming", "Hysteria2"),
    SettingChoiceOption("gRPC", "Multiplexed HTTP/2 tunnel for unstable mobile data", "gRPC"),
    SettingChoiceOption("Shadowsocks", "Lightweight AEAD cipher proxy", "Shadowsocks")
)

private val ROUTING_MODE_OPTIONS = listOf(
    SettingChoiceOption("Global", "Route all device internet traffic through the VPN", "Global (Recommended)"),
    SettingChoiceOption("Bypass LAN", "Keep local Wi‑Fi devices like printers & TVs reachable directly", "Bypass LAN"),
    SettingChoiceOption("Direct Only", "Diagnostic mode — sends traffic directly without VPN", "Direct Only (Testing)")
)

private val DNS_PROVIDER_OPTIONS = listOf(
    SettingChoiceOption("Auto (DoH)", "Encrypted private DNS lookups — safest default", "Auto (DoH)"),
    SettingChoiceOption("Cloudflare 1.1.1.1", "Fast privacy-focused DNS by Cloudflare", "Cloudflare 1.1.1.1"),
    SettingChoiceOption("Google 8.8.8.8", "Reliable global DNS by Google", "Google 8.8.8.8"),
    SettingChoiceOption("Quad9 9.9.9.9", "Blocks known malicious and phishing domains", "Quad9 9.9.9.9"),
    SettingChoiceOption("AdGuard 94.140.14.14", "Filters ads and trackers at the DNS level", "AdGuard DNS")
)

private fun formatRoutingSummary(mode: String): String = when {
    mode.equals("Global", ignoreCase = true) -> "Global"
    mode.equals("Bypass LAN", ignoreCase = true) -> "Bypass LAN"
    mode.contains("Direct", ignoreCase = true) -> "Direct Only"
    else -> "Global"
}

private fun formatDnsSummary(dns: String): String = when {
    dns.startsWith("Auto", ignoreCase = true) -> "Auto (DoH)"
    dns.contains("Cloudflare", ignoreCase = true) -> "Cloudflare 1.1.1.1"
    dns.contains("Google", ignoreCase = true) -> "Google 8.8.8.8"
    dns.contains("Quad9", ignoreCase = true) -> "Quad9 9.9.9.9"
    dns.contains("AdGuard", ignoreCase = true) -> "AdGuard"
    else -> dns
}

private fun onOff(value: Boolean) = if (value) "On" else "Off"

/** One-line summary under "Advanced settings": what is switched away from the defaults. */
private fun advancedSummary(settings: AppSettingsState): String {
    val parts = buildList {
        if (settings.customDnsEnabled && settings.customDnsList.isNotEmpty()) add("Custom DNS")
        if (lanConnectionsOn(settings)) add("LAN")
        if (settings.ipv6Enabled) add("IPv6")
        if (!settings.blockQuic) add("QUIC allowed")
        if (settings.mtu != AppSettingsState.DEFAULT_MTU) add("MTU ${settings.mtu}")
        if (settings.autoConnectBoot) add("Boot connect")
        if (settings.rootMode) add("Root")
    }
    return if (parts.isEmpty()) "DNS, IPv6, QUIC, MTU and more" else parts.joinToString(" · ")
}

/**
 * Settings: a clean sectioned list (icon, title, current value). Tapping a setting opens its own
 * page where the value is changed; actions (guide, logs, export, updates, about) run directly.
 */
@Composable
fun SettingsScreen(
    settings: AppSettingsState,
    xrayLogs: List<String> = emptyList(),
    serverCount: Int = 0,
    appUpdateState: AppUpdateState = AppUpdateState.Idle,
    bottomContentPadding: Dp = 0.dp,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onOpenAddSubscription: () -> Unit,
    onOpenGettingStarted: () -> Unit = {},
    onClearXrayLogs: () -> Unit = {},
    onExportServers: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    onShowMessage: (String) -> Unit = {},
    onBack: () -> Unit
) {
    var page by remember { mutableStateOf<SettingsPage?>(null) }
    BackHandler {
        when (page) {
            null -> onBack()
            SettingsPage.SPLIT_IPS -> page = SettingsPage.SPLIT_TUNNELING
            SettingsPage.CUSTOM_DNS, SettingsPage.MTU -> page = SettingsPage.ADVANCED
            else -> page = null
        }
    }

    val context = LocalContext.current
    var activeDialog by remember { mutableStateOf<ActiveSettingDialog?>(null) }
    var showPerAppPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HollowCream)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = HollowSizes.MaxContent)
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HollowScreenPadding)
        ) {
            val current = page
            if (current == null) {
                SettingsOverview(
                    settings = settings,
                    serverCount = serverCount,
                    xrayLogs = xrayLogs,
                    appUpdateState = appUpdateState,
                    onOpenPage = { page = it },
                    onOpenAddSubscription = onOpenAddSubscription,
                    onOpenGettingStarted = onOpenGettingStarted,
                    onOpenLogs = { activeDialog = ActiveSettingDialog.XRAY_LOGS },
                    onExportServers = onExportServers,
                    onCheckUpdates = {
                        activeDialog = ActiveSettingDialog.CHECK_UPDATE
                        onCheckForUpdates()
                    },
                    onOpenAbout = { activeDialog = ActiveSettingDialog.ABOUT },
                    onShowMessage = onShowMessage
                )
            } else {
                SettingsDetail(
                    page = current,
                    settings = settings,
                    onUpdateSettings = onUpdateSettings,
                    onBack = {
                        page = when (current) {
                            SettingsPage.SPLIT_IPS -> SettingsPage.SPLIT_TUNNELING
                            SettingsPage.CUSTOM_DNS, SettingsPage.MTU -> SettingsPage.ADVANCED
                            else -> null
                        }
                    },
                    onOpenPage = { page = it },
                    onShowMessage = onShowMessage,
                    onOpenSplitIps = { page = SettingsPage.SPLIT_IPS },
                    onOpenPerAppPicker = { showPerAppPicker = true },
                    onOpenSystemVpnSettings = {
                        try {
                            context.startActivity(
                                Intent(Settings.ACTION_VPN_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } catch (_: Exception) {
                            onShowMessage("Couldn't open Android VPN settings")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(HollowSpace.Xxl + bottomContentPadding))
            if (bottomContentPadding == 0.dp) {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }

    when (activeDialog) {
        ActiveSettingDialog.XRAY_LOGS -> {
            XrayDiagnosticLogsDialog(
                logs = xrayLogs,
                onCopyLogs = {
                    val joined = xrayLogs.joinToString("\n")
                    if (joined.isNotBlank()) {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        cm?.setPrimaryClip(ClipData.newPlainText("Hollow VPN Connection Logs", joined))
                        onShowMessage("Copied ${xrayLogs.size} log line(s)")
                    }
                },
                onClearLogs = onClearXrayLogs,
                onDismiss = { activeDialog = null }
            )
        }

        ActiveSettingDialog.ABOUT -> {
            AboutHollowSheet(
                onCopyBuildInfo = onShowMessage,
                onCheckUpdates = {
                    activeDialog = ActiveSettingDialog.CHECK_UPDATE
                    onCheckForUpdates()
                },
                onDismiss = { activeDialog = null }
            )
        }

        ActiveSettingDialog.CHECK_UPDATE -> {
            AppUpdateCheckDialog(
                updateState = appUpdateState,
                onRetry = onCheckForUpdates,
                onDismiss = { activeDialog = null }
            )
        }

        null -> Unit
    }

    if (showPerAppPicker) {
        val including = settings.perAppProxyEnabled && !settings.bypassSelectedApps
        PerAppProxyPickerDialog(
            selectedPackages = settings.activeSplitApps,
            including = including,
            onSave = { packages ->
                onShowMessage(
                    if (packages.isEmpty()) "No apps selected for split tunneling"
                    else "Split tunneling saved for ${packages.size} app(s)"
                )
                onUpdateSettings {
                    if (including) it.copy(splitIncludeApps = packages)
                    else it.copy(splitExcludeApps = packages)
                }
                showPerAppPicker = false
            },
            onDismiss = { showPerAppPicker = false }
        )
    }
}

// ─────────────────────────────── Overview list ───────────────────────────────

@Composable
private fun SettingsOverview(
    settings: AppSettingsState,
    serverCount: Int,
    xrayLogs: List<String>,
    appUpdateState: AppUpdateState,
    onOpenPage: (SettingsPage) -> Unit,
    onOpenAddSubscription: () -> Unit,
    onOpenGettingStarted: () -> Unit,
    onOpenLogs: () -> Unit,
    onExportServers: () -> Unit,
    onCheckUpdates: () -> Unit,
    onOpenAbout: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val ctx = LocalContext.current
    var notifOn by remember { mutableStateOf(NotificationManagerCompat.from(ctx).areNotificationsEnabled()) }
    var widgetCount by remember { mutableStateOf(HollowWidget.widgetCount(ctx)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notifOn = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
                widgetCount = HollowWidget.widgetCount(ctx)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val debugInfo = {
        "App ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n" +
            "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n" +
            "Device ${Build.MANUFACTURER} ${Build.MODEL}\n" +
            "Status ${if (XrayVpnService.isServiceRunning.value) "Connected" else "Disconnected"}\n" +
            "Servers $serverCount\n" +
            "Last error ${XrayVpnService.lastError.value ?: "none"}"
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        notifOn = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    }

    // Same title style and 64dp header row as Home ("HOLLOW VPN") and Servers ("SERVERS").
    Box(
        modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "SETTINGS",
            fontFamily = DotoDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = HollowType.Display,
            letterSpacing = 0.9.sp,
            maxLines = 1,
            color = NothingOnCanvas,
            modifier = Modifier.testTag("settings_title")
        )
    }

    SettingsSectionHeader("Features")
    SettingsRow(
        icon = Icons.Outlined.Security,
        title = "NetShield",
        subtitle = when {
            !settings.netShieldEnabled -> "Off"
            settings.blockAdultContent -> "Blocking ads, trackers, malware and adult content"
            else -> "Blocking ads, trackers and malware"
        },
        onClick = { onOpenPage(SettingsPage.NET_SHIELD) },
        testTag = "row_netshield"
    )
    SettingsRow(
        icon = Icons.Outlined.Apps,
        title = "Split tunneling",
        subtitle = if (settings.perAppProxyEnabled) "On" else "Off",
        onClick = { onOpenPage(SettingsPage.SPLIT_TUNNELING) },
        testTag = "row_per_app_selection"
    )
    SettingsRow(
        icon = Icons.Outlined.Lock,
        title = "Kill switch",
        subtitle = "Always-on VPN is managed by Android",
        onClick = { onOpenPage(SettingsPage.KILL_SWITCH) },
        testTag = "row_system_vpn_settings"
    )
    SettingsRow(
        icon = UsageHistoryIcon,
        title = "Usage & history",
        subtitle = "Data use, connection log and app usage",
        onClick = { onOpenPage(SettingsPage.USAGE_HISTORY) },
        testTag = "row_usage_history"
    )
    SettingsRow(
        icon = Icons.Outlined.Dns,
        title = "Private DNS",
        subtitle = if (settings.netShieldEnabled) "Overridden by NetShield" else formatDnsSummary(settings.dnsMode),
        onClick = { onOpenPage(SettingsPage.PRIVATE_DNS) },
        testTag = "row_dns"
    )

    SettingsSectionHeader("Connection")
    SettingsRow(
        icon = LatencyTestIcon,
        title = "Latency test",
        subtitle = AppSettingsState.testUrlOption(settings.testUrlChoice).name,
        onClick = { onOpenPage(SettingsPage.LATENCY_TEST) },
        testTag = "row_latency_test"
    )
    SettingsRow(
        icon = Icons.Outlined.CloudQueue,
        title = "Auto-update subscriptions",
        subtitle = if (settings.subAutoUpdate) "On · every ${settings.updateIntervalLabel}" else "Off",
        onClick = { onOpenPage(SettingsPage.AUTO_UPDATE) },
        testTag = "row_update_interval"
    )
    SettingsRow(
        icon = AdvancedSettingsIcon,
        title = "Advanced settings",
        subtitle = advancedSummary(settings),
        onClick = { onOpenPage(SettingsPage.ADVANCED) },
        testTag = "row_advanced_settings"
    )
    SettingsRow(
        icon = Icons.Rounded.AccountTree,
        title = "Protocol",
        subtitle = formatConsumerProtocol(settings.protocolPreference),
        onClick = { onOpenPage(SettingsPage.PROTOCOL) },
        testTag = "row_protocol"
    )
    SettingsRow(
        icon = Icons.Rounded.Route,
        title = "Routing mode",
        subtitle = formatRoutingSummary(settings.routingMode),
        onClick = { onOpenPage(SettingsPage.ROUTING_MODE) },
        testTag = "row_routing_mode"
    )
    SettingsRow(
        icon = Icons.Outlined.Refresh,
        title = "Auto-switch slow servers",
        subtitle = onOff(settings.smartSwitchEnabled),
        onClick = { onOpenPage(SettingsPage.SMART_SWITCH) },
        testTag = "row_smart_switch"
    )
    SettingsRow(
        icon = Icons.Outlined.Lock,
        title = "Strict TLS verification",
        subtitle = onOff(settings.strictTlsEnabled),
        onClick = { onOpenPage(SettingsPage.STRICT_TLS) },
        testTag = "row_strict_tls"
    )
    SettingsRow(
        icon = Icons.Outlined.PlayArrow,
        title = "Start on app open",
        subtitle = onOff(settings.startOnAppOpen),
        onClick = { onOpenPage(SettingsPage.START_ON_OPEN) },
        testTag = "row_start_on_open"
    )

    SettingsSectionHeader("Servers")
    SettingsRow(
        icon = Icons.Outlined.Link,
        title = "Servers & subscriptions",
        subtitle = if (serverCount > 0) "$serverCount saved" else "Add servers or a subscription",
        onClick = onOpenAddSubscription,
        testTag = "row_custom_subscription"
    )

    SettingsSectionHeader("General")
    SettingsRow(
        icon = Icons.Outlined.Notifications,
        title = "Notifications",
        subtitle = onOff(notifOn),
        onClick = {
            val prefs = ctx.getSharedPreferences("ui", Context.MODE_PRIVATE)
            val asked = prefs.getBoolean("notif_asked", false)
            if (!notifOn && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !asked) {
                prefs.edit().putBoolean("notif_asked", true).apply()
                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { ctx.startActivity(intent) }
                    .onFailure { onShowMessage("Couldn\u2019t open notification settings") }
            }
        },
        testTag = "row_notifications"
    )
    SettingsRow(
        icon = Icons.Outlined.SettingsBrightness,
        title = "Appearance",
        subtitle = settings.themeMode,
        onClick = { onOpenPage(SettingsPage.APPEARANCE) },
        testTag = "row_appearance"
    )
    SettingsRow(
        icon = Icons.Outlined.Widgets,
        title = "Widget",
        subtitle = if (widgetCount > 0) "Added to home screen" else "Add to home screen",
        onClick = {
            if (!HollowWidget.requestPin(ctx)) {
                onShowMessage("To add the widget, press and hold your home screen, then select Hollow VPN from Widgets.")
            }
            widgetCount = HollowWidget.widgetCount(ctx)
        },
        testTag = "row_widget"
    )
    SettingsRow(
        icon = Icons.Outlined.VisibilityOff,
        title = "Hide from recent apps",
        subtitle = onOff(settings.hideFromRecentApps),
        onClick = { onOpenPage(SettingsPage.HIDE_RECENTS) },
        testTag = "row_hide_recents"
    )

    SettingsSectionHeader("Support")
    SettingsRow(
        icon = Icons.Outlined.SupportAgent,
        title = "Support contact",
        subtitle = null,
        onClick = { ctx.openUrl(AppLinks.REPO_URL, onShowMessage) },
        testTag = "row_support_contact",
        external = true
    )
    SettingsRow(
        icon = Icons.Outlined.BugReport,
        title = "Report an issue",
        subtitle = null,
        onClick = { ctx.reportIssue(debugInfo(), onShowMessage) },
        testTag = "row_report_issue",
        external = true
    )
    SettingsRow(
        icon = Icons.Outlined.Terminal,
        title = "Debug logs",
        subtitle = if (xrayLogs.isEmpty()) "Empty" else "${xrayLogs.size} lines",
        onClick = onOpenLogs,
        testTag = "row_xray_logs"
    )
    SettingsRow(
        icon = Icons.Outlined.Info,
        title = "Setup & permissions guide",
        subtitle = null,
        onClick = onOpenGettingStarted,
        testTag = "row_getting_started"
    )
    SettingsRow(
        icon = Icons.Outlined.FileUpload,
        title = "Export server backup",
        subtitle = "$serverCount servers to clipboard",
        onClick = onExportServers,
        testTag = "row_export_servers"
    )
    val updateSubtitle = when (appUpdateState) {
        AppUpdateState.Idle -> "Tap to check"
        AppUpdateState.Checking -> "Checking…"
        is AppUpdateState.UpToDate -> "You're on the latest version"
        is AppUpdateState.UpdateAvailable -> "Update available · ${appUpdateState.latestVersion}"
        is AppUpdateState.Error -> "Couldn't check · tap to retry"
    }
    SettingsRow(
        icon = Icons.Outlined.SystemUpdate,
        title = "Check for updates",
        subtitle = updateSubtitle,
        onClick = onCheckUpdates,
        testTag = "row_check_updates"
    )

    Spacer(modifier = Modifier.height(HollowSpace.Xxl))
    Column(
        modifier = Modifier.fillMaxWidth().padding(HollowSpace.Lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App version: ${BuildConfig.VERSION_NAME}",
            fontFamily = SpaceGroteskFamily,
            fontSize = HollowType.Micro,
            color = HollowInkSecondary,
            modifier = Modifier
                .clip(RoundedCornerShape(HollowRadius.Sm))
                .clickable(role = Role.Button, onClick = onOpenAbout)
                .padding(HollowSpace.Sm)
                .testTag("card_about_hollow")
        )
        Text(
            text = "Third party licenses",
            fontFamily = SpaceGroteskFamily,
            fontSize = HollowType.Micro,
            color = HollowOrange,
            modifier = Modifier
                .clip(RoundedCornerShape(HollowRadius.Sm))
                .clickable(role = Role.Button) { ctx.openUrl(AppLinks.LICENSE_URL, onShowMessage) }
                .padding(HollowSpace.Sm)
                .testTag("row_licenses")
        )
    }
    Spacer(modifier = Modifier.height(HollowSpace.Xxl))
}

@Composable
internal fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontFamily = SpaceGroteskFamily,
        fontSize = HollowType.Micro,
        letterSpacing = 0.9.sp,
        color = HollowOrange,
        modifier = Modifier.padding(top = HollowSpace.Xxl, bottom = HollowSpace.Sm, start = HollowSpace.Xxs)
    )
}

@Composable
internal fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    testTag: String,
    external: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = HollowSpace.Md)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HollowInk,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(HollowSpace.Lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = HollowType.Small,
                color = HollowInk
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(HollowSpace.Xs))
                Text(
                    text = subtitle,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = HollowType.Micro,
                    lineHeight = 14.sp,
                    color = HollowInkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (external) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = HollowInkSecondary,
                modifier = Modifier.padding(start = HollowSpace.Sm).size(18.dp)
            )
        }
    }
}

// ─────────────────────────────── Detail pages ───────────────────────────────

@Composable
private fun SettingsDetail(
    page: SettingsPage,
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onBack: () -> Unit,
    onOpenPage: (SettingsPage) -> Unit,
    onShowMessage: (String) -> Unit,
    onOpenSplitIps: () -> Unit,
    onOpenPerAppPicker: () -> Unit,
    onOpenSystemVpnSettings: () -> Unit
) {
    val title = when (page) {
        SettingsPage.NET_SHIELD -> "NetShield"
        SettingsPage.SPLIT_TUNNELING -> "Split tunneling"
        SettingsPage.SPLIT_IPS ->
            if (settings.bypassSelectedApps) "Excluded IP addresses" else "Included IP addresses"
        SettingsPage.USAGE_HISTORY -> "Usage & history"
        SettingsPage.KILL_SWITCH -> "Kill switch"
        SettingsPage.PRIVATE_DNS -> "Private DNS"
        SettingsPage.PROTOCOL -> "Protocol"
        SettingsPage.ROUTING_MODE -> "Routing mode"
        SettingsPage.SMART_SWITCH -> "Auto-switch slow servers"
        SettingsPage.STRICT_TLS -> "Strict TLS verification"
        SettingsPage.START_ON_OPEN -> "Start on app open"
        SettingsPage.AUTO_UPDATE -> "Auto-update subscriptions"
        SettingsPage.LATENCY_TEST -> "Latency test"
        SettingsPage.ADVANCED -> "Advanced settings"
        SettingsPage.CUSTOM_DNS -> "Custom DNS"
        SettingsPage.MTU -> "MTU"
        SettingsPage.APPEARANCE -> "Appearance"
        SettingsPage.HIDE_RECENTS -> "Hide from recent apps"
    }

    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HollowTopSquareButton(
            containerColor = NothingTopBtnFill,
            contentColor = NothingOnCanvas,
            borderColor = NothingTopBtnBorder,
            contentDescription = "Back",
            onClick = onBack,
            modifier = Modifier.testTag("btn_navigate_back")
        ) { tint ->
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(modifier = Modifier.width(HollowSpace.Lg))
        Text(
            text = title.uppercase(),
            fontFamily = DotoDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = HollowType.Display,
            letterSpacing = 0.9.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = NothingOnCanvas
        )
    }
    Spacer(modifier = Modifier.height(HollowSpace.Lg))

    when (page) {
        SettingsPage.NET_SHIELD -> NetShieldPage(settings, onUpdateSettings)

        SettingsPage.SPLIT_TUNNELING -> SplitTunnelingPage(
            settings = settings,
            onUpdateSettings = onUpdateSettings,
            onOpenAppPicker = onOpenPerAppPicker,
            onOpenIps = onOpenSplitIps
        )

        SettingsPage.SPLIT_IPS -> SplitIpAddressesPage(settings, onUpdateSettings)

        SettingsPage.KILL_SWITCH -> KillSwitchPage(onOpenSystemVpnSettings)

        SettingsPage.USAGE_HISTORY -> UsageHistoryPage()

        SettingsPage.PRIVATE_DNS -> {
            DetailDescription(
                if (settings.netShieldEnabled) {
                    "The DNS provider used to look up websites while connected. NetShield is on, so it " +
                        "currently overrides this choice."
                } else "The DNS provider used to look up websites while connected."
            )
            DetailRadioList(DNS_PROVIDER_OPTIONS, settings.dnsMode) { chosen ->
                onUpdateSettings { it.copy(dnsMode = chosen) }
            }
        }

        SettingsPage.PROTOCOL -> {
            DetailDescription("The protocol used to build the tunnel. Auto picks the best one for your network.")
            DetailRadioList(PROTOCOL_OPTIONS, settings.protocolPreference) { chosen ->
                onUpdateSettings { it.copy(protocolPreference = chosen) }
            }
        }

        SettingsPage.ROUTING_MODE -> {
            DetailDescription("Decide which traffic goes through the VPN.")
            DetailRadioList(ROUTING_MODE_OPTIONS, formatRoutingSummary(settings.routingMode)) { chosen ->
                onUpdateSettings { it.copy(routingMode = chosen) }
            }
        }

        SettingsPage.SMART_SWITCH -> {
            DetailDescription("Automatically move to a faster server when the current one becomes slow or unresponsive.")
            DetailToggleRow("Auto-switch slow servers", settings.smartSwitchEnabled, { v -> onUpdateSettings { it.copy(smartSwitchEnabled = v) } }, "switch_smart_switch")
        }

        SettingsPage.STRICT_TLS -> {
            DetailDescription("Reject servers whose TLS certificate can't be verified. Safer, but some free servers may stop working.")
            DetailToggleRow("Strict TLS verification", settings.strictTlsEnabled, { v -> onUpdateSettings { it.copy(strictTlsEnabled = v) } }, "switch_strict_tls")
        }

        SettingsPage.START_ON_OPEN -> {
            DetailDescription("Connect automatically as soon as you open the app.")
            DetailToggleRow("Start on app open", settings.startOnAppOpen, { v -> onUpdateSettings { it.copy(startOnAppOpen = v) } }, "switch_start_on_open")
        }

        SettingsPage.AUTO_UPDATE -> {
            DetailDescription("Keep your server list fresh by refreshing subscriptions in the background.")
            DetailToggleRow("Auto-update subscriptions", settings.subAutoUpdate, { v -> onUpdateSettings { it.copy(subAutoUpdate = v) } }, "switch_auto_configure")
            if (settings.subAutoUpdate) {
                Spacer(modifier = Modifier.height(HollowSpace.Sm))
                SettingsSectionHeader("Update interval")
                DetailRadioList(UPDATE_INTERVAL_OPTIONS, settings.updateIntervalLabel) { chosen ->
                    onUpdateSettings { it.copy(updateIntervalLabel = chosen) }
                }
            }
        }

        SettingsPage.LATENCY_TEST -> LatencyTestPage(settings, onUpdateSettings)

        SettingsPage.ADVANCED -> AdvancedSettingsPage(
            settings = settings,
            onUpdateSettings = onUpdateSettings,
            onOpenCustomDns = { onOpenPage(SettingsPage.CUSTOM_DNS) },
            onOpenMtu = { onOpenPage(SettingsPage.MTU) },
            onShowMessage = onShowMessage
        )

        SettingsPage.CUSTOM_DNS -> CustomDnsPage(settings, onUpdateSettings)

        SettingsPage.MTU -> MtuPage(settings, onUpdateSettings, onChosen = { onOpenPage(SettingsPage.ADVANCED) })

        SettingsPage.APPEARANCE -> {
            DetailDescription("Choose how Hollow looks.")
            ThemeModeInlineSelector(
                selectedMode = settings.themeMode,
                onSelectMode = { mode -> onUpdateSettings { it.copy(themeMode = mode) } }
            )
            Spacer(modifier = Modifier.height(HollowSpace.Xl))
            DisplaySizeStepper(
                percent = settings.displayScalePercent,
                onChange = { p ->
                    onUpdateSettings {
                        it.copy(displayScalePercent = p.coerceIn(HollowDisplay.MIN, HollowDisplay.MAX))
                    }
                }
            )
            DetailDescription("Makes text and controls across the app smaller or larger.")
        }

        SettingsPage.HIDE_RECENTS -> {
            DetailDescription("Hide the app preview in Android's recent apps screen.")
            DetailToggleRow("Hide from recent apps", settings.hideFromRecentApps, { v -> onUpdateSettings { it.copy(hideFromRecentApps = v) } }, "switch_hide_recents")
        }
    }
}

@Composable
private fun DisplaySizeStepper(percent: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = HollowSizes.Control)
            .testTag("row_display_size"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Display size",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = HollowType.Small,
            color = HollowInk,
            modifier = Modifier.weight(1f)
        )
        HollowTopSquareButton(
            containerColor = NothingTopBtnFill,
            contentColor = NothingOnCanvas,
            borderColor = NothingTopBtnBorder,
            contentDescription = "Decrease display size",
            onClick = { if (percent > HollowDisplay.MIN) onChange(percent - HollowDisplay.STEP) },
            modifier = Modifier.testTag("btn_display_size_minus")
        ) { tint ->
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = null,
                tint = if (percent > HollowDisplay.MIN) tint else tint.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
        Box(modifier = Modifier.widthIn(min = 56.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "$percent%",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = HollowType.Small,
                color = HollowInk,
                maxLines = 1
            )
        }
        HollowTopSquareButton(
            containerColor = NothingTopBtnFill,
            contentColor = NothingOnCanvas,
            borderColor = NothingTopBtnBorder,
            contentDescription = "Increase display size",
            onClick = { if (percent < HollowDisplay.MAX) onChange(percent + HollowDisplay.STEP) },
            modifier = Modifier.testTag("btn_display_size_plus")
        ) { tint ->
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = if (percent < HollowDisplay.MAX) tint else tint.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
internal fun DetailDescription(text: String) {
    Text(
        text = text,
        fontFamily = SpaceGroteskFamily,
        fontSize = HollowType.Micro,
        lineHeight = 16.sp,
        color = HollowInkSecondary,
        modifier = Modifier.padding(start = HollowSpace.Xxs, bottom = HollowSpace.Sm)
    )
}

@Composable
internal fun DetailToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = HollowSpace.Lg)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = HollowType.Small,
            color = HollowInk,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(HollowSpace.Md))
        HollowSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
internal fun DetailRadioList(
    options: List<SettingChoiceOption>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            val selected = option.value.equals(selectedValue, ignoreCase = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable(role = Role.RadioButton) { onSelect(option.value) }
                    .padding(vertical = HollowSpace.Md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = HollowOrange,
                        unselectedColor = HollowInkSecondary
                    )
                )
                Spacer(modifier = Modifier.width(HollowSpace.Lg))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = HollowType.Small,
                        color = HollowInk
                    )
                    Spacer(modifier = Modifier.height(HollowSpace.Xs))
                    Text(
                        text = option.subtitle,
                        fontFamily = SpaceGroteskFamily,
                        fontSize = HollowType.Micro,
                        lineHeight = 14.sp,
                        color = HollowInkSecondary
                    )
                }
            }
        }
    }
}

// ─────────────────────────────── Segmented selectors ───────────────────────────────

@Composable
private fun ThemeModeInlineSelector(
    selectedMode: String,
    onSelectMode: (String) -> Unit
) {
    val activeMode = when {
        selectedMode.equals("Light", ignoreCase = true) -> "Light"
        selectedMode.equals("System", ignoreCase = true) -> "System"
        else -> "Dark"
    }
    val selectedIndex = when (activeMode) {
        "Dark" -> 0
        "Light" -> 1
        else -> 2
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HollowSpace.Sm),
        shape = RoundedCornerShape(HollowRadius.Sm),
        color = HollowCreamSurface,
        border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HollowSpace.Xs)
        ) {
            val gap = 4.dp
            val slotWidth = ((maxWidth - gap * 2) / 3f).coerceAtLeast(1.dp)
            val pillOffset by animateDpAsState(
                targetValue = (slotWidth + gap) * selectedIndex,
                animationSpec = NothingMotion.snappySpring(),
                label = "theme_pill_glide"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = pillOffset)
                    .width(slotWidth)
                    .height(HollowSizes.Chip)
                    .clip(RoundedCornerShape(HollowRadius.Xs))
                    .background(NothingPillActiveBg)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                ThemeModeSegment(
                    label = "Dark",
                    icon = Icons.Outlined.DarkMode,
                    selected = activeMode == "Dark",
                    onClick = { onSelectMode("Dark") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("theme_mode_dark")
                )
                ThemeModeSegment(
                    label = "Light",
                    icon = Icons.Outlined.LightMode,
                    selected = activeMode == "Light",
                    onClick = { onSelectMode("Light") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("theme_mode_light")
                )
                ThemeModeSegment(
                    label = "System",
                    icon = Icons.Outlined.SettingsBrightness,
                    selected = activeMode == "System",
                    onClick = { onSelectMode("System") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("theme_mode_system")
                )
            }
        }
    }
}

@Composable
private fun ThemeModeSegment(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentTint by animateColorAsState(
        targetValue = if (selected) NothingPillActiveIcon else HollowInkSecondary,
        animationSpec = tween(HollowMotion.Fast, easing = NothingMotion.FluidEase),
        label = "theme_seg_tint_$label"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .nothingPressScale(interactionSource, pressedScale = HollowMotion.PressControl)
            .clip(RoundedCornerShape(HollowRadius.Xs))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(HollowSizes.Chip)
                .padding(horizontal = HollowSpace.Sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentTint,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(HollowSpace.Xs))
            Text(
                text = label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = HollowType.Caption,
                maxLines = 1,
                color = contentTint
            )
        }
    }
}

@Composable
private fun InlineModeSegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentTint by animateColorAsState(
        targetValue = if (selected) NothingPillActiveIcon else HollowInkSecondary,
        animationSpec = tween(HollowMotion.Fast, easing = NothingMotion.FluidEase),
        label = "inline_seg_tint_$label"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .nothingPressScale(interactionSource, pressedScale = HollowMotion.PressControl)
            .clip(RoundedCornerShape(HollowRadius.Xs))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HollowSizes.Chip),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = HollowType.Caption,
                color = contentTint
            )
        }
    }
}

private fun Context.openUrl(url: String, onFail: (String) -> Unit) {
    runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { onFail("Couldn\u2019t open the link") }
}

private fun Context.reportIssue(info: String, onFail: (String) -> Unit) {
    val body = "**What happened?**\n\n\n\n**Environment**\n```\n$info\n```\n"
    openUrl("${AppLinks.REPO_URL}/issues/new?body=${Uri.encode(body)}", onFail)
}
