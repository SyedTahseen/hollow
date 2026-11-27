package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.NothingOnInvertedSurface
import com.hollow.vpn.ui.components.HollowIcons
import com.hollow.vpn.ui.theme.NothingInvertedSurface
import com.hollow.vpn.ui.theme.HollowError
import com.hollow.vpn.ui.theme.HollowDisplayInk
import com.hollow.vpn.ui.theme.HollowLine2
import com.hollow.vpn.ui.theme.HollowGlass
import com.hollow.vpn.ui.theme.HollowMonoFamily
import com.hollow.vpn.ui.theme.HollowText
import android.content.Context
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hollow.vpn.data.AppSettingsState
import com.hollow.vpn.data.AppUsage
import com.hollow.vpn.data.UsageSession
import com.hollow.vpn.data.UsageStore
import com.hollow.vpn.ui.components.HollowSwitch
import com.hollow.vpn.ui.components.NothingDotMatrixBackdrop
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowUploadAccent
import com.hollow.vpn.ui.theme.NothingMatrixGreen
import com.hollow.vpn.ui.theme.nothingPressScale
import com.hollow.vpn.vpn.IpRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val KILL_SWITCH_HELP_URL = "https://developer.android.com/develop/connectivity/vpn"

@Composable
internal fun FeatureHero(icon: ImageVector, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(HollowRadius.Lg)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HollowSizes.TopBarExpanded)
            .clip(shape)
            .background(HollowCreamSurface)
            .border(HollowStroke.Hairline, HollowCreamDivider, shape),
        contentAlignment = Alignment.Center
    ) {
        NothingDotMatrixBackdrop(modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .size(HollowSizes.Tile)
                .clip(CircleShape)
                .background(HollowCreamSurface)
                .border(HollowStroke.Hairline, HollowLine2, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = HollowInk, modifier = Modifier.size(HollowSizes.IconLg))
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = HollowSpace.Lg,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HollowRadius.Lg),
        color = HollowGlass,
        border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) { content() }
    }
}

@Composable
private fun FeatureToggleCard(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f),
        shape = RoundedCornerShape(HollowRadius.Lg),
        color = HollowGlass,
        border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
    ) {
        Row(
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .then(if (enabled) Modifier.clickable(role = Role.Switch) { onCheckedChange(!checked) } else Modifier)
                .padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = HollowText.Body,
                color = HollowInk,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(HollowSpace.Md))
            if (enabled) {
                HollowSwitch(checked = checked, onCheckedChange = onCheckedChange)
            } else {
                HollowSwitch(checked = false, onCheckedChange = {}, modifier = Modifier.alpha(0.6f))
            }
        }
    }
}

@Composable
internal fun FeatureButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    trailingIcon: ImageVector? = null,
    testTag: String = ""
) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val container = if (primary) NothingInvertedSurface else Color.Transparent
    val content = if (primary) NothingOnInvertedSurface else HollowInk
    val shape = RoundedCornerShape(HollowRadius.Md)
    Box(
        modifier = modifier
            .heightIn(min = HollowSizes.Button)
            .nothingPressScale(interaction, pressedScale = HollowMotion.PressCard)
            .clip(shape)
            .background(container)
            .border(HollowStroke.Hairline, if (primary) NothingInvertedSurface else HollowLine2, shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = HollowSpace.Lg)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(
                text = label.uppercase(),
                style = HollowText.Button,
                color = content,
                maxLines = 1
            )
            if (trailingIcon != null) {
                Spacer(Modifier.width(HollowSpace.Sm))
                Icon(trailingIcon, contentDescription = null, tint = content, modifier = Modifier.size(HollowSizes.IconSm))
            }
        }
    }
}

@Composable
internal fun FeatureCaption(text: String, modifier: Modifier = Modifier, color: Color = HollowInkSecondary) {
    Text(
        text = text,
        fontFamily = HollowMonoFamily,
        fontSize = HollowType.Micro,
        letterSpacing = HollowTracking.Micro,
        lineHeight = HollowLineHeight.Micro,
        color = color,
        modifier = modifier.padding(horizontal = HollowSpace.Xxs, vertical = HollowSpace.Sm)
    )
}

private fun formatItems(items: List<String>): String = when (items.size) {
    0 -> "None"
    1 -> items[0]
    2 -> "${items[0]}, ${items[1]}"
    else -> "${items[0]}, ${items[1]} and ${items.size - 2} more"
}

private fun isStrictPrivateDns(lp: LinkProperties?): Boolean =
    Build.VERSION.SDK_INT >= 28 && lp != null && lp.isPrivateDnsActive && lp.privateDnsServerName != null

private fun Context.strictPrivateDnsActive(): Boolean {
    if (Build.VERSION.SDK_INT < 28) return false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    return runCatching { cm.getLinkProperties(cm.activeNetwork) }.getOrNull().let(::isStrictPrivateDns)
}

@Composable
private fun rememberPrivateDnsActive(): Boolean {
    val ctx = LocalContext.current
    var active by remember { mutableStateOf(ctx.strictPrivateDnsActive()) }
    DisposableEffect(ctx) {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT < 28 || cm == null) return@DisposableEffect onDispose { }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                active = isStrictPrivateDns(linkProperties)
            }

            override fun onLost(network: Network) {
                active = ctx.strictPrivateDnsActive()
            }
        }
        runCatching { cm.registerDefaultNetworkCallback(callback) }
        onDispose { runCatching { cm.unregisterNetworkCallback(callback) } }
    }
    return active
}

@Composable
internal fun NetShieldPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit
) {
    val ctx = LocalContext.current
    val privateDns = rememberPrivateDnsActive()

    FeatureHero(HollowIcons.Security)
    Spacer(Modifier.height(HollowSpace.Md))
    DetailDescription(
        "Blocks ads, trackers and malicious websites while you are connected, by sending DNS " +
            "lookups to a filtering resolver (AdGuard DNS). Applies the next time you connect."
    )

    if (privateDns) {
        FeatureCard(modifier = Modifier.padding(vertical = HollowSpace.Sm)) {
            Text(
                text = "NetShield is unavailable",
                style = HollowText.Body,
                color = HollowInk
            )
            Spacer(Modifier.height(HollowSpace.Xs))
            Text(
                text = "Your device is using a private DNS provider, which overrides the DNS this VPN sets. " +
                    "Turn Private DNS off, or set it to Automatic, to use NetShield.",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                letterSpacing = HollowTracking.Micro,
                lineHeight = HollowLineHeight.Micro,
                color = HollowInkSecondary
            )
            Spacer(Modifier.height(HollowSpace.Md))
            FeatureButton(
                label = "Network settings",
                onClick = {
                    runCatching {
                        ctx.startActivity(
                            Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                primary = false,
                testTag = "btn_network_settings"
            )
        }
    }

    FeatureToggleCard(
        title = "NetShield",
        checked = settings.netShieldEnabled && !privateDns,
        enabled = !privateDns,
        onCheckedChange = { v -> onUpdateSettings { it.copy(netShieldEnabled = v) } },
        testTag = "switch_netshield",
        modifier = Modifier.padding(vertical = HollowSpace.Sm)
    )

    if (settings.netShieldEnabled && !privateDns) {
        FeatureToggleCard(
            title = "Block adult content",
            checked = settings.blockAdultContent,
            onCheckedChange = { v -> onUpdateSettings { it.copy(blockAdultContent = v) } },
            testTag = "switch_block_adult",
            modifier = Modifier.padding(vertical = HollowSpace.Sm)
        )
        FeatureCaption(
            "NetShield works on domain names, so it can't block ads served from the same domain as the " +
                "content, and apps that use their own encrypted DNS bypass it. While on, it replaces the " +
                "default and custom DNS."
        )
    }
}

@Composable
internal fun SplitTunnelingPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onOpenAppPicker: () -> Unit,
    onOpenIps: () -> Unit
) {
    val ctx = LocalContext.current
    val including = !settings.bypassSelectedApps
    val word = if (including) "Included" else "Excluded"
    val chosenApps = settings.activeSplitApps
    val appNames = remember(chosenApps) {
        val pm = ctx.packageManager
        chosenApps
            .mapNotNull { runCatching { pm.getApplicationInfo(it, 0).loadLabel(pm).toString() }.getOrNull() }
            .sortedBy { it.lowercase() }
    }
    val ips = if (including) settings.splitIncludeIps else settings.splitExcludeIps
    var modeDialog by remember { mutableStateOf(false) }

    FeatureHero(HollowIcons.CallSplit)
    Spacer(Modifier.height(HollowSpace.Md))
    DetailDescription("Customize your connection by deciding which apps and IP addresses are protected by VPN.")

    FeatureToggleCard(
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
        testTag = "switch_per_app_proxy",
        modifier = Modifier.padding(vertical = HollowSpace.Sm)
    )

    if (settings.perAppProxyEnabled) {
        SettingsRow(
            icon = HollowIcons.CallSplit,
            title = "Mode",
            subtitle = if (including) "Include" else "Exclude",
            onClick = { modeDialog = true },
            testTag = "row_split_mode"
        )
        SettingsRow(
            icon = HollowIcons.Apps,
            title = "$word apps",
            subtitle = formatItems(appNames),
            onClick = onOpenAppPicker,
            testTag = "row_per_app_selection_picker"
        )
        SettingsRow(
            icon = HollowIcons.Terminal,
            title = "$word IP addresses",
            subtitle = formatItems(ips),
            onClick = onOpenIps,
            testTag = "row_split_ips"
        )
        FeatureCaption(
            "Applies the next time you connect." +
                if (!including) " Excluding IP addresses needs Android 13 or newer." else ""
        )
    }

    if (modeDialog) {
        SettingOptionPickerDialog(
            title = "Mode",
            options = listOf(
                SettingChoiceOption("Exclude", "Selected apps and IP addresses are excluded from VPN connection."),
                SettingChoiceOption("Include", "Only selected apps and IP addresses connect through VPN, all other traffic is unprotected.")
            ),
            selectedValue = if (including) "Include" else "Exclude",
            onSelect = { chosen ->
                onUpdateSettings { it.copy(bypassSelectedApps = chosen == "Exclude") }
                modeDialog = false
            },
            onDismiss = { modeDialog = false }
        )
    }
}

@Composable
internal fun KillSwitchPage(onOpenSystemVpnSettings: () -> Unit) {
    val ctx = LocalContext.current
    val bold = SpanStyle(fontWeight = FontWeight.Bold, color = HollowInk)
    val cog = "cogwheel"

    FeatureHero(HollowIcons.Lock)
    Spacer(Modifier.height(HollowSpace.Lg))
    Text(
        text = "How to enable kill switch",
        style = HollowText.Heading,
        color = HollowInk
    )
    Spacer(Modifier.height(HollowSpace.Md))

    val stepStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = HollowSansFamily,
        fontSize = HollowType.BodyStrong,
        letterSpacing = HollowTracking.BodyStrong,
        lineHeight = HollowLineHeight.BodyStrong,
        color = HollowInkSecondary
    )
    Text("1. Open your device settings", style = stepStyle)
    Text(
        text = buildAnnotatedString {
            append("2. Find Hollow and tap ")
            appendInlineContent(cog, "settings")
        },
        inlineContent = mapOf(
            cog to InlineTextContent(Placeholder(1.25.em, 1.25.em, PlaceholderVerticalAlign.Center)) {
                Icon(HollowIcons.Gear, contentDescription = null, tint = HollowInk, modifier = Modifier.fillMaxSize())
            }
        ),
        style = stepStyle
    )
    Text(
        text = buildAnnotatedString {
            append("3. Enable “")
            withStyle(bold) { append("Always-on VPN") }
            append("” and “")
            withStyle(bold) { append("Block connections without VPN") }
            append("”")
        },
        style = stepStyle
    )

    Spacer(Modifier.height(HollowSpace.Lg))
    FeatureCard {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = HollowIcons.Info,
                contentDescription = null,
                tint = HollowInkSecondary,
                modifier = Modifier.size(HollowSizes.Icon)
            )
            Spacer(Modifier.width(HollowSpace.Md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "When kill switch is on, you won’t be able to connect to:",
                    fontFamily = HollowMonoFamily,
                    fontSize = HollowType.Micro,
                    letterSpacing = HollowTracking.Micro,
                    lineHeight = HollowLineHeight.Micro,
                    color = HollowInkSecondary
                )
                Spacer(Modifier.height(HollowSpace.Sm))
                KillSwitchBullet("Websites and apps skipped by split tunneling")
                KillSwitchBullet("Devices on your local area network (LAN)")
            }
        }
    }

    Spacer(Modifier.height(HollowSpace.Xl))
    FeatureButton(
        label = "Settings",
        onClick = onOpenSystemVpnSettings,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = HollowIcons.OpenInNew,
        testTag = "row_open_system_vpn"
    )
    Spacer(Modifier.height(HollowSpace.Md))
    FeatureButton(
        label = "What is kill switch?",
        onClick = {
            runCatching {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(KILL_SWITCH_HELP_URL)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        primary = false,
        trailingIcon = HollowIcons.OpenInNew,
        testTag = "btn_kill_switch_learn_more"
    )
}

@Composable
private fun KillSwitchBullet(text: String) {
    Row {
        Text(
            text = "•",
            fontFamily = HollowMonoFamily,
            fontSize = HollowType.Micro,
            lineHeight = HollowLineHeight.Micro,
            letterSpacing = HollowTracking.Micro,
            color = HollowInkSecondary,
            modifier = Modifier.padding(end = HollowSpace.Sm)
        )
        Text(
            text = text,
            fontFamily = HollowMonoFamily,
            fontSize = HollowType.Micro,
            letterSpacing = HollowTracking.Micro,
            lineHeight = HollowLineHeight.Micro,
            color = HollowInkSecondary
        )
    }
}

internal fun fmtSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var v = bytes.toDouble()
    var i = 0
    while (v >= 1000 && i < units.lastIndex) { v /= 1000; i++ }
    return if (i == 0) "$bytes B" else String.format(Locale.US, if (v >= 100) "%.0f %s" else "%.1f %s", v, units[i])
}

internal fun fmtDuration(ms: Long): String {
    val s = (ms / 1000).coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return when {
        h > 0 -> String.format(Locale.US, "%d h %02d min", h, m)
        m > 0 -> "$m min $sec sec"
        else -> "$sec sec"
    }
}

@Composable
internal fun UsageHistoryPage() {
    val ctx = LocalContext.current
    var version by remember { mutableIntStateOf(0) }
    var accessVersion by remember { mutableIntStateOf(0) }

    val activity = ctx as? ComponentActivity
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) { version++; accessVersion++ }
        }
        activity?.lifecycle?.addObserver(observer)
        onDispose { activity?.lifecycle?.removeObserver(observer) }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5_000)
            version++
        }
    }

    val days = remember(version) { UsageStore.lastDays(ctx, 14) }
    val sessions = remember(version) { UsageStore.sessions(ctx) }
    val today = remember(version) { UsageStore.total(ctx, 1) }
    val week = remember(version) { UsageStore.total(ctx, 7) }
    val month = remember(version) { UsageStore.total(ctx, 30) }
    val access = remember(accessVersion) { UsageStore.hasUsageAccess(ctx) }

    val apps by produceState<List<AppUsage>?>(null, accessVersion, access) {
        value = if (!access) emptyList() else withContext(Dispatchers.IO) {
            val since = System.currentTimeMillis() - 7L * 86_400_000L
            UsageStore.perApp(ctx, UsageStore.sessions(ctx).filter { it.end >= since })
        }
    }

    DetailDescription("Data used while connected, your recent connections, and which apps used data.")

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = HollowSpace.Sm),
        horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
    ) {
        UsageSummaryCard("Today", today, Modifier.weight(1f))
        UsageSummaryCard("7 days", week, Modifier.weight(1f))
        UsageSummaryCard("30 days", month, Modifier.weight(1f))
    }

    SettingsSectionHeader("Last 14 days")
    FeatureCard {
        val peak = days.maxOf { it.second + it.third }
        val barColor = HollowDisplayInk
        val barDim = HollowDisplayInk.copy(alpha = 0.3f)
        Canvas(Modifier.fillMaxWidth().height(110.dp)) {
            val n = days.size
            val gap = 4.dp.toPx()
            val bw = (size.width - gap * (n - 1)) / n
            days.forEachIndexed { i, d ->
                val total = d.second + d.third
                val h = if (peak == 0L) 2.dp.toPx()
                else (total.toFloat() / peak * size.height).coerceAtLeast(2.dp.toPx())
                drawRect(
                    color = if (i == n - 1) barColor else barDim,
                    topLeft = Offset(i * (bw + gap), size.height - h),
                    size = Size(bw, h)
                )
            }
        }
        Spacer(Modifier.height(HollowSpace.Sm))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = days.first().first.substring(5),
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                lineHeight = HollowLineHeight.Micro,
                letterSpacing = HollowTracking.Micro,
                color = HollowInkSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (peak > 0) "peak ${fmtSize(peak)}" else "no data yet",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                lineHeight = HollowLineHeight.Micro,
                letterSpacing = HollowTracking.Micro,
                color = HollowInkSecondary
            )
        }
    }

    SettingsSectionHeader("Apps, last 7 days")
    FeatureCard(contentPadding = 0.dp) {
        if (!access) {
            Text(
                text = "Android only shares per-app data with apps that have Usage access. Turn it on for " +
                    "Hollow to see which apps used data while the VPN was connected.",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                letterSpacing = HollowTracking.Micro,
                lineHeight = HollowLineHeight.Micro,
                color = HollowInkSecondary,
                modifier = Modifier.padding(HollowSpace.Lg)
            )
            UsageDivider()
            Text(
                text = "OPEN USAGE ACCESS SETTINGS",
                style = HollowText.ButtonSmall,
                color = HollowInk,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) {
                        runCatching {
                            ctx.startActivity(
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                    .padding(HollowSpace.Lg)
                    .testTag("row_usage_access")
            )
        } else {
            val list = apps
            when {
                list == null -> UsageNote("[LOADING…]")
                list.isEmpty() -> UsageNote("No app data for recent sessions.")
                else -> list.take(15).forEachIndexed { i, a ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = a.label,
                            style = HollowText.Body,
                            color = HollowInk,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = fmtSize(a.bytes),
                            fontFamily = HollowMonoFamily,
                            fontSize = HollowType.Caption,
                            lineHeight = HollowLineHeight.Caption,
                            letterSpacing = HollowTracking.Caption,
                            color = HollowInkSecondary
                        )
                    }
                    if (i < minOf(list.size, 15) - 1) UsageDivider()
                }
            }
            Text(
                text = "Approximate. Android groups app data in hourly blocks, and this includes apps skipped by the VPN.",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                letterSpacing = HollowTracking.Micro,
                lineHeight = HollowLineHeight.Micro,
                color = HollowInkSecondary,
                modifier = Modifier.padding(start = HollowSpace.Lg, end = HollowSpace.Lg, bottom = HollowSpace.Md)
            )
        }
    }

    SettingsSectionHeader("Connection history")
    FeatureCard(contentPadding = 0.dp) {
        if (sessions.isEmpty()) UsageNote("No connections recorded yet.")
        sessions.take(30).forEachIndexed { i, s ->
            UsageSessionRow(s)
            if (i < minOf(sessions.size, 30) - 1) UsageDivider()
        }
    }

    if (sessions.isNotEmpty() || today.first + today.second + week.first + week.second > 0) {
        Text(
            text = "CLEAR HISTORY",
            style = HollowText.ButtonSmall,
            color = HollowError,
            modifier = Modifier
                .padding(top = HollowSpace.Md)
                .clip(RoundedCornerShape(HollowRadius.Sm))
                .clickable(role = Role.Button) { UsageStore.clear(ctx); version++; accessVersion++ }
                .padding(horizontal = HollowSpace.Md, vertical = HollowSpace.Md)
                .testTag("btn_clear_usage_history")
        )
    }
}

@Composable
private fun UsageDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(HollowStroke.Hairline)
            .background(HollowCreamDivider)
    )
}

@Composable
private fun UsageNote(text: String) {
    Text(
        text = text,
        fontFamily = HollowMonoFamily,
        fontSize = HollowType.Micro,
        lineHeight = HollowLineHeight.Micro,
        letterSpacing = HollowTracking.Micro,
        color = HollowInkSecondary,
        modifier = Modifier.padding(HollowSpace.Lg)
    )
}

@Composable
private fun UsageSummaryCard(label: String, value: Pair<Long, Long>, modifier: Modifier) {
    FeatureCard(modifier = modifier, contentPadding = HollowSpace.Md) {
        Text(
            text = label.uppercase(),
            fontFamily = HollowMonoFamily,
            fontSize = HollowType.Micro,
            lineHeight = HollowLineHeight.Micro,
            letterSpacing = HollowTracking.Micro,
            color = HollowInkSecondary
        )
        Spacer(Modifier.height(HollowSpace.Sm))
        MetricLine("↓", fmtSize(value.second), NothingMatrixGreen)
        Spacer(Modifier.height(HollowSpace.Xs))
        MetricLine("↑", fmtSize(value.first), HollowUploadAccent)
    }
}

@Composable
private fun UsageSessionRow(s: UsageSession) {
    val fmt = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Md)
    ) {
        Text(
            text = s.server.ifBlank { "Server" },
            style = HollowText.Body,
            color = HollowInk,
            maxLines = 1
        )
        Spacer(Modifier.height(HollowSpace.Xxs))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "${fmt.format(Date(s.start))} · ${fmtDuration(s.end - s.start)}",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                lineHeight = HollowLineHeight.Micro,
                letterSpacing = HollowTracking.Micro,
                color = HollowInkSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "↓ ${fmtSize(s.down)}  ↑ ${fmtSize(s.up)}",
                fontFamily = HollowMonoFamily,
                fontSize = HollowType.Micro,
                lineHeight = HollowLineHeight.Micro,
                letterSpacing = HollowTracking.Micro,
                color = HollowInkSecondary
            )
        }
    }
}

internal val UsageHistoryIcon: ImageVector get() = HollowIcons.Stats

/** `.metric` value: Doto numeral + small mono unit, coloured delta arrow on the value only. */
@Composable
private fun MetricLine(arrow: String, size: String, tone: Color) {
    val num = size.substringBefore(' ')
    val unit = size.substringAfter(' ', "")
    Row(verticalAlignment = Alignment.Bottom) {
        Text(arrow, style = HollowText.Data, color = tone)
        Spacer(Modifier.width(HollowSpace.Xs))
        Text(num, style = HollowText.NumeralSm, color = HollowDisplayInk, maxLines = 1)
        if (unit.isNotEmpty()) {
            Spacer(Modifier.width(HollowSpace.Xs))
            Text(unit, style = HollowText.Caption, color = HollowInkSecondary, modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}
