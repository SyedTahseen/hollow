package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowText
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

class AddServerActions(
    val settings: AppSettingsState,
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

private fun routeIncoming(raw: String, actions: AddServerActions) {
    val t = raw.trim()
    val singleUrl = t.isNotEmpty() && t.none { it.isWhitespace() } &&
        (t.startsWith("http://", ignoreCase = true) || t.startsWith("https://", ignoreCase = true))
    if (singleUrl) actions.onAddSubscription(t) else actions.onImportText(raw)
}

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
                modifier = Modifier.size(HollowSizes.Icon)
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
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
                Text(text = label, style = HollowText.Body, color = HollowInk)
            }
        },
        onClick = onClick,
        modifier = Modifier.testTag(tag)
    )
}

@Composable
fun AddServerOverlays(
    state: AddServerState,
    actions: AddServerActions,
    handleRequests: Boolean = true
) {
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
            onAdd = { url ->
                state.showSubscription = false
                actions.onAddSubscription(url)
            },
            onDismiss = { state.showSubscription = false }
        )
    }

    if (state.showManual) {
        AddManualServerDialog(
            onAdd = { raw ->
                state.showManual = false
                routeIncoming(raw, actions)
            },
            onDismiss = { state.showManual = false }
        )
    }
}

@Composable
private fun AddSubscriptionDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = { Text("Add subscription link", style = HollowText.Heading, color = HollowInk) },
        text = {
            Column {
                Text(
                    "Servers from this URL are added now and when you update subscriptions.",
                    style = HollowText.Small,
                    color = HollowInk
                )
                Spacer(Modifier.height(HollowSpace.Md))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    singleLine = true,
                    textStyle = HollowText.Body,
                    placeholder = { Text("https://…", style = HollowText.Body, color = HollowInkMuted) },
                    shape = RoundedCornerShape(HollowRadius.Md),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HollowInk,
                        unfocusedBorderColor = HollowCreamDivider,
                        cursorColor = HollowOrange,
                        focusedContainerColor = HollowCream,
                        unfocusedContainerColor = HollowCream
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("subscription_url_input")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (url.isNotBlank()) onAdd(url.trim()) },
                modifier = Modifier.testTag("btn_add_and_fetch")
            ) {
                Text("Add", style = HollowText.ButtonSmall, color = HollowOrange)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", style = HollowText.ButtonSmall, color = HollowInk) }
        }
    )
}

@Composable
private fun AddManualServerDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var bad by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = { Text("Add manually", style = HollowText.Heading, color = HollowInk) },
        text = {
            Column {
                Text(
                    "Paste a WireGuard .conf or a vless://, trojan://, ss://, vmess://, hysteria2://, tuic://, wireguard://, socks5:// or http://host:port link (one per line).",
                    style = HollowText.Small,
                    color = HollowInk
                )
                Spacer(Modifier.height(HollowSpace.Md))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; bad = false },
                    singleLine = false,
                    minLines = 3,
                    maxLines = 8,
                    textStyle = HollowText.Body,
                    shape = RoundedCornerShape(HollowRadius.Md),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HollowInk,
                        unfocusedBorderColor = HollowCreamDivider,
                        cursorColor = HollowOrange,
                        focusedContainerColor = HollowCream,
                        unfocusedContainerColor = HollowCream
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("manual_links_field")
                )
                if (bad) {
                    Text(
                        "No valid link found",
                        style = HollowText.Caption,
                        color = HollowOrange,
                        modifier = Modifier.padding(top = HollowSpace.Sm)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (text.isBlank()) bad = true else onAdd(text) }) {
                Text("Add", style = HollowText.ButtonSmall, color = HollowOrange)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", style = HollowText.ButtonSmall, color = HollowInk) }
        }
    )
}
