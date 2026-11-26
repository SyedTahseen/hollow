package com.hollow.vpn.ui.screens

import androidx.compose.foundation.clickable
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.data.AppSettingsState
import com.hollow.vpn.ui.components.HollowSwitch
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.vpn.IpRoutes
import com.hollow.vpn.vpn.RootShell
import com.hollow.vpn.vpn.RootXrayCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * Settings > Connection pages ported from Outrun: Latency test, Advanced settings (LAN connections,
 * Custom DNS, IPv6, Block QUIC, MTU, Auto-connect on boot). Built from Hollow's own building blocks.
 */

internal val LatencyTestIcon = Icons.Outlined.Speed
internal val AdvancedSettingsIcon = Icons.Outlined.Tune

private const val ROUTING_BYPASS_LAN = "Bypass LAN"
private const val ROUTING_GLOBAL = "Global"

internal fun lanConnectionsOn(settings: AppSettingsState) =
    settings.routingMode.contains(ROUTING_BYPASS_LAN, ignoreCase = true)

// ─────────────────────────────── Latency test ───────────────────────────────

@Composable
internal fun LatencyTestPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit
) {
    FeatureHero(LatencyTestIcon)
    Spacer(Modifier.height(HollowSpace.Lg))
    DetailDescription(
        "Each server is tested with a real request to this address, sent through the server. " +
            "The time covers connecting to the server, the secure connection and the response."
    )
    DetailRadioList(
        options = AppSettingsState.TEST_URLS.map { SettingChoiceOption(it.id, it.url, it.name) },
        selectedValue = settings.testUrlChoice
    ) { id -> onUpdateSettings { it.copy(testUrlChoice = id) } }
    FeatureCaption("Used for the next test. Servers keep the latency they already have until they are tested again.")
}

// ─────────────────────────────── Advanced settings ───────────────────────────────

@Composable
internal fun AdvancedSettingsPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onOpenCustomDns: () -> Unit,
    onOpenMtu: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    AdvancedValueItem(
        title = "Custom DNS",
        value = if (settings.customDnsEnabled && settings.customDnsList.isNotEmpty()) "On" else "Off",
        description = "Connect with a self-hosted or third-party domain name system (DNS).",
        testTag = "row_custom_dns",
        onClick = onOpenCustomDns
    )
    AdvancedToggleItem(
        title = "LAN connections",
        checked = lanConnectionsOn(settings),
        description = "Lets you reach printers, speakers and other devices on your local network while connected.",
        testTag = "switch_lan"
    ) { on ->
        onUpdateSettings { it.copy(routingMode = if (on) ROUTING_BYPASS_LAN else ROUTING_GLOBAL) }
    }
    AdvancedToggleItem(
        title = "IPv6 support",
        checked = settings.ipv6Enabled,
        description = "Tunnels IPv6 traffic through the VPN. When off, IPv6 is blocked so it can't bypass servers " +
            "without IPv6 support.",
        testTag = "switch_ipv6"
    ) { on -> onUpdateSettings { it.copy(ipv6Enabled = on) } }
    AdvancedToggleItem(
        title = "Block QUIC",
        checked = settings.blockQuic,
        description = "Blocks QUIC (UDP port 443) so apps like Chrome and YouTube use regular TCP instead. Can be " +
            "faster and more stable with VLESS, Trojan and other TCP-based servers.",
        testTag = "switch_block_quic"
    ) { on -> onUpdateSettings { it.copy(blockQuic = on) } }
    AdvancedValueItem(
        title = "MTU",
        value = settings.mtu.toString(),
        description = "The largest packet the VPN sends. Lower it if some sites or apps stall or time out only while connected.",
        testTag = "row_mtu",
        onClick = onOpenMtu
    )
    AdvancedToggleItem(
        title = "Auto-connect on boot",
        checked = settings.autoConnectBoot,
        description = "Connects the selected server when the phone starts. Needs the VPN permission granted once. " +
            "On some phones you must also allow auto-start and unrestricted battery use for this app.",
        testTag = "switch_auto_connect_boot"
    ) { on -> onUpdateSettings { it.copy(autoConnectBoot = on) } }
    AdvancedToggleItem(
        title = "Root mode",
        checked = settings.rootMode,
        description = "Needs a rooted device. Sends all traffic through the VPN using root instead of Android's " +
            "VPN service, so there is no VPN permission prompt or key icon. Per-app split tunneling is not " +
            "available in this mode.",
        testTag = "switch_root_mode"
    ) { on ->
        if (!on) {
            onUpdateSettings { it.copy(rootMode = false) }
        } else {
            scope.launch {
                if (!RootXrayCore.isAvailable(ctx)) {
                    onShowMessage("Root mode needs the Xray binary (libxray.so), which is not bundled in this build")
                } else if (withContext(Dispatchers.IO) { RootShell.granted() }) {
                    onUpdateSettings { it.copy(rootMode = true) }
                    onShowMessage("Root access granted")
                } else {
                    onShowMessage("Root access wasn't granted")
                }
            }
        }
    }
    FeatureCaption("Changes apply the next time you connect.")
}

@Composable
private fun AdvancedToggleItem(
    title: String,
    checked: Boolean,
    description: String,
    testTag: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(enabled = enabled, role = Role.Switch) { onCheckedChange(!checked) }
            .padding(vertical = HollowSpace.Md)
            .testTag(testTag),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AdvancedTitle(title, enabled)
            AdvancedDescription(description)
        }
        Spacer(Modifier.width(HollowSpace.Lg))
        HollowSwitch(checked = checked && enabled, onCheckedChange = { if (enabled) onCheckedChange(it) })
    }
}

@Composable
private fun AdvancedValueItem(
    title: String,
    value: String,
    description: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = HollowSpace.Md)
            .testTag(testTag),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AdvancedTitle(title, true)
            AdvancedDescription(description)
        }
        Spacer(Modifier.width(HollowSpace.Lg))
        Row(modifier = Modifier.heightIn(min = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontFamily = DotoDisplayFamily,
                fontSize = HollowType.Micro,
                lineHeight = HollowLineHeight.Micro,
                letterSpacing = HollowTracking.Micro,
                color = HollowOrange
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = HollowInkSecondary,
                modifier = Modifier.size(HollowSizes.Icon)
            )
        }
    }
}


@Composable
private fun AdvancedTitle(text: String, enabled: Boolean) {
    Text(
        text = text,
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = HollowType.Small,
        lineHeight = HollowLineHeight.Small,
        letterSpacing = HollowTracking.Small,
        color = if (enabled) HollowInk else HollowInkMuted
    )
}

@Composable
private fun AdvancedDescription(text: String) {
    Text(
        text = text,
        fontFamily = DotoDisplayFamily,
        fontSize = HollowType.Micro,
        letterSpacing = HollowTracking.Micro,
        lineHeight = HollowLineHeight.Micro,
        color = HollowInkSecondary,
        modifier = Modifier.padding(top = HollowSpace.Xs)
    )
}

// ─────────────────────────────── MTU ───────────────────────────────

@Composable
internal fun MtuPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onChosen: () -> Unit
) {
    FeatureHero(AdvancedSettingsIcon)
    Spacer(Modifier.height(HollowSpace.Lg))
    DetailDescription(
        "The largest packet the VPN sends. Lower it if some sites or apps stall, load halfway or time out " +
            "only while connected. Applies the next time you connect."
    )
    DetailRadioList(
        options = AppSettingsState.MTU_CHOICES.map { (v, note) -> SettingChoiceOption(v.toString(), note) },
        selectedValue = settings.mtu.toString()
    ) { chosen ->
        onUpdateSettings { it.copy(mtu = chosen.toIntOrNull() ?: AppSettingsState.DEFAULT_MTU) }
        onChosen()
    }
}

// ─────────────────────────────── Custom DNS ───────────────────────────────

@Composable
internal fun CustomDnsPage(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val servers = settings.customDnsList

    fun add() {
        val ip = IpRoutes.parse(text)?.takeIf { '/' !in it }
        if (ip == null) {
            error = true
        } else {
            if (ip !in servers) onUpdateSettings { it.copy(customDnsList = it.customDnsList + ip, customDnsEnabled = true) }
            text = ""
            error = false
        }
    }

    FeatureHero(Icons.Outlined.Tune)
    Spacer(Modifier.height(HollowSpace.Lg))
    DetailDescription("Connect with a self-hosted or third-party DNS instead of the default DNS.")
    DetailToggleRow(
        title = "Custom DNS",
        checked = settings.customDnsEnabled,
        onCheckedChange = { on -> onUpdateSettings { it.copy(customDnsEnabled = on) } },
        testTag = "switch_custom_dns"
    )
    if (settings.customDnsEnabled && settings.netShieldEnabled) {
        FeatureCaption(
            "NetShield is on and takes priority, so these servers are not used until you turn NetShield off.",
            color = HollowOrange
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = HollowSpace.Sm),
        verticalAlignment = Alignment.Top
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; error = false },
            placeholder = { Text("9.9.9.9", color = HollowInkMuted, fontFamily = DotoDisplayFamily) },
            isError = error,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { add() }),
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = { text = ""; error = false }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = HollowInkSecondary)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = HollowCreamSurface,
                unfocusedContainerColor = HollowCreamSurface,
                focusedBorderColor = HollowInk,
                unfocusedBorderColor = HollowCreamDivider,
                focusedTextColor = HollowInk,
                unfocusedTextColor = HollowInk,
                errorBorderColor = HollowOrange,
                errorContainerColor = HollowCreamSurface
            ),
            shape = RoundedCornerShape(HollowRadius.Md),
            modifier = Modifier.weight(1f).testTag("field_custom_dns")
        )
        Spacer(Modifier.width(HollowSpace.Sm))
        FeatureButton(
            label = "Add",
            onClick = { add() },
            modifier = Modifier.height(HollowSizes.Field),
            testTag = "btn_add_custom_dns"
        )
    }
    FeatureCaption(
        text = if (error) "Not a valid IPv4 or IPv6 address." else "Enter the address of a DNS server, e.g. 1.1.1.1.",
        color = if (error) HollowOrange else HollowInkSecondary
    )

    Spacer(Modifier.height(HollowSpace.Md))
    SettingsSectionHeader("DNS servers (${servers.size})")
    if (servers.isEmpty()) FeatureCaption("No DNS servers added yet.")
    servers.forEach { ip ->
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.Control),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ip,
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Medium,
                fontSize = HollowType.Body,
                lineHeight = HollowLineHeight.Body,
                letterSpacing = HollowTracking.Body,
                color = HollowInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (!settings.ipv6Enabled && ':' in ip) {
                Text("IPv6 off", fontFamily = DotoDisplayFamily, fontSize = HollowType.Micro, color = HollowInkMuted)
            }
            IconButton(
                onClick = { onUpdateSettings { it.copy(customDnsList = it.customDnsList - ip) } },
                modifier = Modifier.testTag("btn_remove_dns_$ip")
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Remove $ip", tint = HollowInkSecondary)
            }
        }
    }
    if (servers.isNotEmpty()) FeatureCaption("Applies the next time you connect.")
}
