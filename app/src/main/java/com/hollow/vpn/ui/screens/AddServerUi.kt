package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowModalSheet
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.data.AppSettingsState
import com.hollow.vpn.ui.AddServerRequest
import com.hollow.vpn.ui.components.HollowSwitch
import com.hollow.vpn.ui.components.HollowTopSquareButton
import com.hollow.vpn.ui.components.NothingDottedDivider
import com.hollow.vpn.ui.components.QrScanDialog
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.JetBrainsMonoFamily
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingSurface
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill

private val MANUAL_PROTOCOL_PRESETS = listOf("VLESS", "VMess", "Trojan", "Shadowsocks", "Hysteria2")
private val MANUAL_TRANSPORT_PRESETS = listOf("Reality", "WS", "gRPC", "TCP", "Hysteria2")

typealias ManualServerSubmit = (
    countryName: String,
    host: String,
    port: Int,
    protocol: String,
    transport: String,
    uuid: String,
    sni: String,
    rawJson: String,
    publicKey: String,
    shortId: String,
    wsPath: String,
    wsHost: String,
    grpcServiceName: String
) -> Unit

/** Everything the server list needs to add servers. Built once in MainActivity. */
class AddServerActions(
    val settings: AppSettingsState,
    /** Pending request from another screen (home empty state, settings…). Consumed once handled. */
    val request: AddServerRequest?,
    val onConsumeRequest: () -> Unit,
    val onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    val onAddSubscription: (String) -> Unit,
    val onImportText: (String) -> Unit,
    val onAddManualServer: ManualServerSubmit,
    val onShowMessage: (String) -> Unit
)

@Stable
class AddServerState {
    var menuOpen by mutableStateOf(false)
    var showSubscription by mutableStateOf(false)
    var showManual by mutableStateOf(false)
    var showScanner by mutableStateOf(false)
}

@Composable
fun rememberAddServerState(): AddServerState = remember { AddServerState() }

/** A pasted/scanned/imported single-line http(s) URL is a subscription; everything else is raw server text. */
private fun routeIncoming(raw: String, actions: AddServerActions) {
    val t = raw.trim()
    val singleUrl = t.isNotEmpty() && t.none { it.isWhitespace() } &&
        (t.startsWith("http://", ignoreCase = true) || t.startsWith("https://", ignoreCase = true))
    if (singleUrl) actions.onAddSubscription(t) else actions.onImportText(raw)
}

/**
 * The "+" button in the server list header. Opens a dropdown with every way to add servers:
 * Scan QR code, Paste from clipboard, Import from file, Add manually, Add subscription link.
 */
@Composable
fun AddServerButton(
    state: AddServerState,
    actions: AddServerActions,
    modifier: Modifier = Modifier,
    containerColor: Color = NothingTopBtnFill,
    contentColor: Color = NothingOnCanvas,
    borderColor: Color? = NothingTopBtnBorder,
    testTag: String = "country_add"
) {
    val context = LocalContext.current

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val text = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
            if (text.isNullOrBlank()) actions.onShowMessage("Couldn't read that file") else routeIncoming(text, actions)
        }
    }

    Box(modifier = modifier) {
        HollowTopSquareButton(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            contentDescription = "Add server",
            onClick = { state.menuOpen = true },
            modifier = Modifier.testTag(testTag)
        ) { tint ->
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(21.dp)
            )
        }

        DropdownMenu(
            expanded = state.menuOpen,
            onDismissRequest = { state.menuOpen = false },
            offset = DpOffset(x = 0.dp, y = 8.dp),
            containerColor = NothingSurfaceElevated,
            shape = RoundedCornerShape(HollowRadius.Lg),
            modifier = Modifier.width(244.dp)
        ) {
            AddServerMenuItem(Icons.Outlined.QrCodeScanner, "Scan QR code", "menu_add_scan") {
                state.menuOpen = false
                state.showScanner = true
            }
            AddServerMenuItem(Icons.Outlined.ContentPaste, "Paste from clipboard", "menu_add_paste") {
                state.menuOpen = false
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val pasted = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                routeIncoming(pasted, actions)
            }
            AddServerMenuItem(Icons.Outlined.FileOpen, "Import from file", "menu_add_file") {
                state.menuOpen = false
                fileLauncher.launch(arrayOf("*/*"))
            }
            AddServerMenuItem(Icons.Outlined.Edit, "Add manually", "menu_add_manual") {
                state.menuOpen = false
                state.showManual = true
            }
            AddServerMenuItem(Icons.Outlined.Link, "Add subscription link", "menu_add_subscription") {
                state.menuOpen = false
                state.showSubscription = true
            }
        }
    }
}

@Composable
private fun AddServerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md),
                modifier = Modifier.padding(vertical = HollowSpace.Xxs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HollowInk,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = HollowType.BodyStrong,
                        fontWeight = FontWeight.Medium
                    ),
                    color = HollowInk
                )
            }
        },
        onClick = onClick,
        modifier = Modifier.testTag(tag)
    )
}

/** Dialogs, the manual sheet and the full-screen scanner. Compose once next to [AddServerButton]. */
@Composable
fun AddServerOverlays(
    state: AddServerState,
    actions: AddServerActions,
    handleRequests: Boolean = true
) {
    // Requests from other screens (home empty state, settings, failed fetch) open the right surface here.
    LaunchedEffect(actions.request, handleRequests) {
        if (!handleRequests) return@LaunchedEffect
        when (actions.request) {
            AddServerRequest.MENU -> state.menuOpen = true
            AddServerRequest.SUBSCRIPTION -> state.showSubscription = true
            AddServerRequest.MANUAL -> state.showManual = true
            null -> return@LaunchedEffect
        }
        actions.onConsumeRequest()
    }

    if (state.showScanner) {
        QrScanDialog(
            onQrDecoded = { decoded ->
                state.showScanner = false
                routeIncoming(decoded, actions)
            },
            onDismiss = { state.showScanner = false }
        )
    }

    if (state.showSubscription) {
        AddSubscriptionDialog(
            settings = actions.settings,
            onUpdateSettings = actions.onUpdateSettings,
            onAdd = { url ->
                state.showSubscription = false
                actions.onAddSubscription(url)
            },
            onDismiss = { state.showSubscription = false }
        )
    }

    if (state.showManual) {
        AddManualServerSheet(
            onSubmit = actions.onAddManualServer,
            onDismiss = { state.showManual = false }
        )
    }
}

@Composable
private fun AddSubscriptionDialog(
    settings: AppSettingsState,
    onUpdateSettings: ((AppSettingsState) -> AppSettingsState) -> Unit,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf(settings.activeSubscriptionUrl) }
    var showIntervalPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = {
            Text(
                text = "Add subscription link",
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                color = HollowInk
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Servers from this URL are added now and refreshed when you update subscriptions.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = HollowType.Body),
                    color = HollowInkSecondary
                )

                Spacer(modifier = Modifier.height(HollowSpace.Lg))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HollowSizes.Field),
                    shape = RoundedCornerShape(HollowRadius.Md),
                    color = HollowCreamSurface,
                    border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = HollowSpace.Lg),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (url.isEmpty()) {
                            Text(
                                text = "https://example.com/sub",
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = HollowType.Body,
                                color = HollowInkMuted
                            )
                        }
                        BasicTextField(
                            value = url,
                            onValueChange = { url = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = HollowType.Body,
                                color = HollowInk
                            ),
                            cursorBrush = SolidColor(HollowOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subscription_url_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(HollowSpace.Md))

                SubscriptionOptionRow(
                    label = "Auto update",
                    onClick = { onUpdateSettings { it.copy(subAutoUpdate = !settings.subAutoUpdate) } }
                ) {
                    HollowSwitch(
                        checked = settings.subAutoUpdate,
                        onCheckedChange = { enabled -> onUpdateSettings { it.copy(subAutoUpdate = enabled) } },
                        modifier = Modifier.testTag("switch_sub_auto_update")
                    )
                }
                NothingDottedDivider()
                SubscriptionOptionRow(
                    label = "Update interval",
                    onClick = { showIntervalPicker = true },
                    role = Role.Button,
                    modifier = Modifier.testTag("row_sub_update_interval")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
                    ) {
                        Text(
                            text = settings.subUpdateInterval,
                            fontFamily = JetBrainsMonoFamily,
                            fontSize = HollowType.Small,
                            color = HollowInk
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = HollowInkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                NothingDottedDivider()
                SubscriptionOptionRow(
                    label = "Replace existing",
                    onClick = { onUpdateSettings { it.copy(subReplaceExisting = !settings.subReplaceExisting) } }
                ) {
                    HollowSwitch(
                        checked = settings.subReplaceExisting,
                        onCheckedChange = { enabled -> onUpdateSettings { it.copy(subReplaceExisting = enabled) } },
                        modifier = Modifier.testTag("switch_sub_replace_existing")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (url.isNotBlank()) onAdd(url.trim()) },
                enabled = url.isNotBlank(),
                shape = RoundedCornerShape(HollowRadius.Md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HollowInk,
                    contentColor = HollowCream
                ),
                modifier = Modifier.testTag("btn_add_and_fetch")
            ) {
                Text(
                    text = "Add & fetch",
                    fontSize = HollowType.Body,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = HollowInkSecondary)
            }
        }
    )

    if (showIntervalPicker) {
        SettingOptionPickerDialog(
            title = "Select Update Interval",
            options = UPDATE_INTERVAL_OPTIONS,
            selectedValue = settings.subUpdateInterval,
            onSelect = { chosen ->
                onUpdateSettings { it.copy(updateIntervalLabel = chosen, subUpdateInterval = chosen) }
                showIntervalPicker = false
            },
            onDismiss = { showIntervalPicker = false }
        )
    }
}

@Composable
private fun SubscriptionOptionRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    role: Role? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(role = role, onClick = onClick)
            .padding(vertical = HollowSpace.Sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = HollowType.BodyStrong
            ),
            color = HollowInk,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

/** Structured manual entry (host, port, protocol, transport, Reality/WS/gRPC fields, optional raw JSON). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddManualServerSheet(
    onSubmit: ManualServerSubmit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var country by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("443") }
    var protocol by remember { mutableStateOf("VLESS") }
    var transport by remember { mutableStateOf("Reality") }
    var uuid by remember { mutableStateOf("") }
    var sni by remember { mutableStateOf("") }
    var publicKey by remember { mutableStateOf("") }
    var shortId by remember { mutableStateOf("") }
    var wsPath by remember { mutableStateOf("") }
    var wsHost by remember { mutableStateOf("") }
    var grpcService by remember { mutableStateOf("") }
    var rawJson by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = HollowInk,
        unfocusedBorderColor = HollowCreamDivider,
        focusedLabelColor = HollowInk,
        unfocusedLabelColor = HollowInkSecondary,
        cursorColor = HollowOrange,
        focusedContainerColor = HollowCream,
        unfocusedContainerColor = HollowCream
    )
    val fieldShape = RoundedCornerShape(HollowRadius.Md)

    HollowModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HollowScreenPadding)
                .padding(bottom = HollowSpace.Xxl),
            verticalArrangement = Arrangement.spacedBy(HollowSpace.Md)
        ) {
            Text(
                text = "ADD MANUALLY",
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = HollowType.Display,
                letterSpacing = 0.9.sp,
                color = HollowInk
            )

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Server Country / Remark") },
                singleLine = true,
                colors = fieldColors,
                shape = fieldShape,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("Server Address / Host") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier.weight(0.68f)
                )
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier.weight(0.32f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                MANUAL_PROTOCOL_PRESETS.forEach { preset ->
                    PresetChip(
                        label = preset,
                        selected = protocol.equals(preset, ignoreCase = true)
                    ) {
                        protocol = preset
                        if (preset.equals("Hysteria2", ignoreCase = true)) {
                            transport = "Hysteria2"
                        } else if (transport.equals("Hysteria2", ignoreCase = true)) {
                            transport = "Reality"
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)) {
                OutlinedTextField(
                    value = protocol,
                    onValueChange = { protocol = it },
                    label = { Text("Protocol") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = transport,
                    onValueChange = { transport = it },
                    label = { Text("Transport") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                MANUAL_TRANSPORT_PRESETS.forEach { preset ->
                    PresetChip(
                        label = preset,
                        selected = transport.equals(preset, ignoreCase = true)
                    ) { transport = preset }
                }
            }

            OutlinedTextField(
                value = uuid,
                onValueChange = { uuid = it },
                label = {
                    Text(
                        if (protocol.equals("Hysteria2", ignoreCase = true) ||
                            protocol.equals("Trojan", ignoreCase = true)
                        ) "Auth Password" else "UUID / Password"
                    )
                },
                singleLine = true,
                colors = fieldColors,
                shape = fieldShape,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sni,
                onValueChange = { sni = it },
                label = { Text("SNI / Server Name") },
                singleLine = true,
                colors = fieldColors,
                shape = fieldShape,
                modifier = Modifier.fillMaxWidth()
            )

            if (transport.equals("Reality", ignoreCase = true) &&
                !protocol.equals("Hysteria2", ignoreCase = true)
            ) {
                OutlinedTextField(
                    value = publicKey,
                    onValueChange = { publicKey = it },
                    label = { Text("Reality Public Key (pbk)") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_public_key_input")
                )
                OutlinedTextField(
                    value = shortId,
                    onValueChange = { shortId = it },
                    label = { Text("Reality Short ID (sid)") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_short_id_input")
                )
            }

            if (transport.equals("WS", ignoreCase = true) || transport.equals("WebSocket", ignoreCase = true)) {
                OutlinedTextField(
                    value = wsPath,
                    onValueChange = { wsPath = it },
                    label = { Text("WebSocket Path (e.g. /ws)") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_ws_path_input")
                )
                OutlinedTextField(
                    value = wsHost,
                    onValueChange = { wsHost = it },
                    label = { Text("WebSocket Host Header") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_ws_host_input")
                )
            }

            if (transport.equals("gRPC", ignoreCase = true)) {
                OutlinedTextField(
                    value = grpcService,
                    onValueChange = { grpcService = it },
                    label = { Text("gRPC Service Name") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_grpc_service_input")
                )
            }

            OutlinedTextField(
                value = rawJson,
                onValueChange = { rawJson = it },
                label = { Text("Optional Raw Config (JSON)") },
                textStyle = TextStyle(
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = HollowType.Small,
                    color = HollowInk
                ),
                colors = fieldColors,
                shape = fieldShape,
                minLines = 4,
                maxLines = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("raw_xray_json_input")
            )

            Button(
                onClick = {
                    val complete = rawJson.trim().startsWith("{") ||
                        (host.isNotBlank() && uuid.isNotBlank())
                    onSubmit(
                        country,
                        host,
                        port.toIntOrNull() ?: 443,
                        protocol,
                        transport,
                        uuid,
                        sni,
                        rawJson,
                        publicKey,
                        shortId,
                        wsPath,
                        wsHost,
                        grpcService
                    )
                    // Keep the sheet open when required fields are missing so the entered values survive
                    // while the view model shows its validation message.
                    if (complete) onDismiss()
                },
                shape = RoundedCornerShape(HollowRadius.Md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HollowInk,
                    contentColor = HollowCream
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HollowSizes.Button)
                    .testTag("btn_save_manual_server")
            ) {
                Text(
                    text = "Save Custom Server & Ping Test",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = HollowType.BodyStrong
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PresetChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = NothingSurface,
            labelColor = HollowInkSecondary,
            selectedContainerColor = HollowInk,
            selectedLabelColor = HollowCream,
            selectedLeadingIconColor = HollowOrange
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = HollowCreamDivider
        ),
        shape = RoundedCornerShape(HollowRadius.Sm)
    )
}
