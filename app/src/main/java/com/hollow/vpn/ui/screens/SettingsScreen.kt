package com.hollow.vpn.ui.screens

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
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.AccountTree
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
    SPLIT_TUNNELING,
    KILL_SWITCH,
    PRIVATE_DNS,
    IPV6,
    PROTOCOL,
    ROUTING_MODE,
    SMART_SWITCH,
    STRICT_TLS,
    START_ON_OPEN,
    AUTO_UPDATE,
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
    BackHandler { if (page != null) page = null else onBack() }

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
                .fillMaxSize()
                .widthIn(max = 600.dp)
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
                    onOpenAbout = { activeDialog = ActiveSettingDialog.ABOUT }
                )
            } else {
                SettingsDetail(
                    page = current,
                    settings = settings,
                    onUpdateSettings = onUpdateSettings,
                    onBack = { page = null },
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

            Spacer(modifier = Modifier.height(24.dp + bottomContentPadding))
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
        PerAppProxyPickerDialog(
            selectedPackages = settings.selectedAppPackages,
            bypassSelectedApps = !settings.perAppProxyEnabled || settings.bypassSelectedApps,
            onSave = { packages, bypassMode ->
                onShowMessage(
                    if (packages.isEmpty()) "Split tunneling disabled (all apps use VPN)"
                    else "Split tunneling saved for ${packages.size} app(s)"
                )
                onUpdateSettings {
                    it.copy(
                        perAppProxyEnabled = packages.isNotEmpty(),
                        bypassSelectedApps = bypassMode,
                        selectedAppPackages = packages
                    )
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
    onOpenAbout: () -> Unit
) {
    Spacer(modifier = Modifier.height(12.dp))
    // Same title style as Home ("HOLLOW VPN") and Servers ("SERVERS").
    Text(
        text = "SETTINGS",
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.9.sp,
        maxLines = 1,
        color = NothingOnCanvas,
        modifier = Modifier.testTag("settings_title")
    )
    Spacer(modifier = Modifier.height(6.dp))

    SettingsSectionHeader("Features")
    SettingsRow(
        icon = Icons.Outlined.Apps,
        title = "Split tunneling",
        subtitle = if (settings.perAppProxyEnabled) {
            "On · ${settings.selectedAppPackages.size} apps"
        } else "Off",
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
        icon = Icons.Outlined.Dns,
        title = "Private DNS",
        subtitle = formatDnsSummary(settings.dnsMode),
        onClick = { onOpenPage(SettingsPage.PRIVATE_DNS) },
        testTag = "row_dns"
    )
    SettingsRow(
        icon = Icons.Outlined.Language,
        title = "IPv6 routing",
        subtitle = onOff(settings.ipv6Enabled),
        onClick = { onOpenPage(SettingsPage.IPV6) },
        testTag = "row_ipv6"
    )

    SettingsSectionHeader("Connection")
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
    SettingsRow(
        icon = Icons.Outlined.CloudQueue,
        title = "Auto-update subscriptions",
        subtitle = if (settings.autoConfigure) "On · every ${settings.updateIntervalLabel}" else "Off",
        onClick = { onOpenPage(SettingsPage.AUTO_UPDATE) },
        testTag = "row_update_interval"
    )

    SettingsSectionHeader("General")
    SettingsRow(
        icon = Icons.Outlined.SettingsBrightness,
        title = "Appearance",
        subtitle = settings.themeMode,
        onClick = { onOpenPage(SettingsPage.APPEARANCE) },
        testTag = "row_appearance"
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
        icon = Icons.Outlined.Info,
        title = "Setup & permissions guide",
        subtitle = null,
        onClick = onOpenGettingStarted,
        testTag = "row_getting_started"
    )
    SettingsRow(
        icon = Icons.Outlined.Terminal,
        title = "Diagnostic logs",
        subtitle = if (xrayLogs.isEmpty()) "Empty" else "${xrayLogs.size} lines",
        onClick = onOpenLogs,
        testTag = "row_xray_logs"
    )
    SettingsRow(
        icon = Icons.Outlined.FileUpload,
        title = "Export server backup",
        subtitle = "$serverCount servers to clipboard",
        onClick = onExportServers,
        testTag = "row_export_servers"
    )

    SettingsSectionHeader("About")
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
    SettingsRow(
        icon = Icons.Outlined.Info,
        title = "About Hollow VPN",
        subtitle = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        onClick = onOpenAbout,
        testTag = "card_about_hollow"
    )
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = HollowOrange,
        modifier = Modifier.padding(top = 26.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HollowInk,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = HollowInk
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 14.sp,
                    color = HollowInkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
    onOpenPerAppPicker: () -> Unit,
    onOpenSystemVpnSettings: () -> Unit
) {
    val title = when (page) {
        SettingsPage.SPLIT_TUNNELING -> "Split tunneling"
        SettingsPage.KILL_SWITCH -> "Kill switch"
        SettingsPage.PRIVATE_DNS -> "Private DNS"
        SettingsPage.IPV6 -> "IPv6 routing"
        SettingsPage.PROTOCOL -> "Protocol"
        SettingsPage.ROUTING_MODE -> "Routing mode"
        SettingsPage.SMART_SWITCH -> "Auto-switch slow servers"
        SettingsPage.STRICT_TLS -> "Strict TLS verification"
        SettingsPage.START_ON_OPEN -> "Start on app open"
        SettingsPage.AUTO_UPDATE -> "Auto-update subscriptions"
        SettingsPage.APPEARANCE -> "Appearance"
        SettingsPage.HIDE_RECENTS -> "Hide from recent apps"
    }

    Spacer(modifier = Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
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
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title.uppercase(),
            fontFamily = DotoDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = 0.9.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = NothingOnCanvas
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    when (page) {
        SettingsPage.SPLIT_TUNNELING -> {
            DetailDescription("Choose which apps skip the VPN, or which apps are the only ones to use it.")
            DetailToggleRow(
                title = "Split tunneling",
                checked = settings.perAppProxyEnabled,
                onCheckedChange = { enabled ->
                    onUpdateSettings {
                        it.copy(
                            perAppProxyEnabled = enabled,
                            bypassSelectedApps = if (enabled && !it.perAppProxyEnabled) true else it.bypassSelectedApps
                        )
                    }
                },
                testTag = "switch_per_app_proxy"
            )
            if (settings.perAppProxyEnabled) {
                PerAppInlineModeRow(
                    bypassSelectedApps = settings.bypassSelectedApps,
                    onSelectBypass = { bypass -> onUpdateSettings { it.copy(bypassSelectedApps = bypass) } }
                )
            }
            SettingsRow(
                icon = Icons.Outlined.Apps,
                title = if (!settings.perAppProxyEnabled || settings.bypassSelectedApps) "Apps skipping VPN" else "Apps using VPN",
                subtitle = if (settings.selectedAppPackages.isEmpty()) "None selected"
                else "${settings.selectedAppPackages.size} apps",
                onClick = onOpenPerAppPicker,
                testTag = "row_per_app_selection_picker"
            )
        }

        SettingsPage.KILL_SWITCH -> {
            DetailDescription(
                "Android's Always-on VPN with “Block connections without VPN” stops all traffic if the " +
                    "tunnel drops, so your real IP is never exposed. It is a system setting, so you turn it on in Android."
            )
            SettingsRow(
                icon = Icons.Outlined.Lock,
                title = "Open Android VPN settings",
                subtitle = "Enable Always-on VPN for Hollow",
                onClick = onOpenSystemVpnSettings,
                testTag = "row_open_system_vpn"
            )
        }

        SettingsPage.PRIVATE_DNS -> {
            DetailDescription("The DNS provider used to look up websites while connected.")
            DetailRadioList(DNS_PROVIDER_OPTIONS, settings.dnsMode) { chosen ->
                onUpdateSettings { it.copy(dnsMode = chosen) }
            }
        }

        SettingsPage.IPV6 -> {
            DetailDescription("Send IPv6 traffic through the VPN as well. Turn off if some sites fail to load.")
            DetailToggleRow("IPv6 routing", settings.ipv6Enabled, { v -> onUpdateSettings { it.copy(ipv6Enabled = v) } }, "switch_ipv6")
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
            DetailToggleRow("Auto-update subscriptions", settings.autoConfigure, { v -> onUpdateSettings { it.copy(autoConfigure = v) } }, "switch_auto_configure")
            if (settings.autoConfigure) {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader("Update interval")
                DetailRadioList(UPDATE_INTERVAL_OPTIONS, settings.updateIntervalLabel) { chosen ->
                    onUpdateSettings { it.copy(updateIntervalLabel = chosen, subUpdateInterval = chosen) }
                }
            }
        }

        SettingsPage.APPEARANCE -> {
            DetailDescription("Choose how Hollow looks.")
            ThemeModeInlineSelector(
                selectedMode = settings.themeMode,
                onSelectMode = { mode -> onUpdateSettings { it.copy(themeMode = mode) } }
            )
        }

        SettingsPage.HIDE_RECENTS -> {
            DetailDescription("Hide the app preview in Android's recent apps screen.")
            DetailToggleRow("Hide from recent apps", settings.hideFromRecentApps, { v -> onUpdateSettings { it.copy(hideFromRecentApps = v) } }, "switch_hide_recents")
        }
    }
}

@Composable
private fun DetailDescription(text: String) {
    Text(
        text = text,
        fontFamily = SpaceGroteskFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = HollowInkSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun DetailToggleRow(
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
            .padding(vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = HollowInk,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        HollowSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DetailRadioList(
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
                    .padding(vertical = 10.dp),
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
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = HollowInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = option.subtitle,
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 13.sp,
                        color = HollowInkSecondary
                    )
                }
            }
        }
    }
}

// ─────────────────────────────── Segmented selectors ───────────────────────────────

@Composable
private fun PerAppInlineModeRow(
    bypassSelectedApps: Boolean,
    onSelectBypass: (Boolean) -> Unit
) {
    val selectedIndex = if (bypassSelectedApps) 0 else 1
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("switch_bypass_selected_apps"),
        shape = RoundedCornerShape(12.dp),
        color = HollowCreamSurface,
        border = BorderStroke(1.dp, HollowCreamDivider)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val gap = 4.dp
            val slotWidth = ((maxWidth - gap) / 2f).coerceAtLeast(1.dp)
            val pillOffset by animateDpAsState(
                targetValue = (slotWidth + gap) * selectedIndex,
                animationSpec = NothingMotion.snappySpring(),
                label = "per_app_pill_glide"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = pillOffset)
                    .width(slotWidth)
                    .height(36.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(NothingPillActiveBg)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                InlineModeSegment(
                    label = "Skip VPN",
                    selected = bypassSelectedApps,
                    onClick = { onSelectBypass(true) },
                    modifier = Modifier.weight(1f)
                )
                InlineModeSegment(
                    label = "VPN only",
                    selected = !bypassSelectedApps,
                    onClick = { onSelectBypass(false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

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
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(13.dp),
        color = HollowCreamSurface,
        border = BorderStroke(1.dp, HollowCreamDivider)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
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
                    .height(36.dp)
                    .clip(RoundedCornerShape(9.dp))
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
        animationSpec = tween(200, easing = NothingMotion.FluidEase),
        label = "theme_seg_tint_$label"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .nothingPressScale(interactionSource, pressedScale = 0.94f)
            .clip(RoundedCornerShape(9.dp))
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
                .height(36.dp)
                .padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentTint,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
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
        animationSpec = tween(200, easing = NothingMotion.FluidEase),
        label = "inline_seg_tint_$label"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .nothingPressScale(interactionSource, pressedScale = 0.94f)
            .clip(RoundedCornerShape(9.dp))
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
                .height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = contentTint
            )
        }
    }
}
