package com.hollow.vpn.ui.screens

import androidx.compose.foundation.border
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowAccentText
import com.hollow.vpn.ui.theme.NothingOnInvertedSurface
import com.hollow.vpn.ui.theme.NothingInvertedSurface
import com.hollow.vpn.ui.theme.HollowLine2
import com.hollow.vpn.ui.theme.NothingSurface
import com.hollow.vpn.ui.theme.HollowMonoFamily
import com.hollow.vpn.ui.theme.HollowError
import com.hollow.vpn.ui.theme.HollowDisplayInk
import com.hollow.vpn.ui.components.HollowPrimaryButton
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.hollow.vpn.ui.components.HollowTopSquareButton
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowText
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
import com.hollow.vpn.vpn.IpRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext

@Composable
internal fun SettingsFullPage(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = HollowScreenPadding)
    ) {
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
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(HollowSizes.Icon)
                )
            }
            Spacer(modifier = Modifier.width(HollowSpace.Lg))
            Text(
                text = title,
                style = HollowText.Subheadline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = NothingOnCanvas,
                modifier = Modifier.weight(1f)
            )
            actions()
        }
        content()
    }
}

@Composable
private fun HeaderTextAction(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.testTag("btn_save_page")) {
        Text(label.uppercase(), style = HollowText.ButtonSmall, color = HollowDisplayInk)
    }
}

@Composable
private fun DiscardDialog(onDiscard: () -> Unit, onKeep: () -> Unit) {
    AlertDialog(
        onDismissRequest = onKeep,
        modifier = Modifier.border(HollowStroke.Hairline, HollowLine2, HollowDialog.Shape),
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = { Text("Discard unsaved changes?", style = HollowText.Heading, color = HollowDisplayInk) },
        confirmButton = {
            HollowPrimaryButton(onClick = onDiscard) { Text("DISCARD", style = HollowText.ButtonSmall) }
        },
        dismissButton = {
            TextButton(onClick = onKeep) { Text("KEEP EDITING", style = HollowText.ButtonSmall, color = HollowInk) }
        }
    )
}

@Composable
private fun ListHeader(title: String, note: String? = null) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            title.uppercase(),
            style = HollowText.Section,
            color = com.hollow.vpn.ui.theme.HollowAccentText,
            modifier = Modifier.padding(top = HollowSpace.Xxl, bottom = if (note != null) HollowSpace.Xs else HollowSpace.Sm)
        )
        if (note != null) Text(note, style = HollowText.Small, color = HollowInkSecondary)
    }
}

private suspend fun readSystemLog(): List<String> = withContext(Dispatchers.IO) {
    runCatching {
        val p = ProcessBuilder("logcat", "-d", "-t", "200", "-v", "time", "*:W").redirectErrorStream(true).start()
        val text = p.inputStream.bufferedReader().use { it.readText() }
        p.destroy()
        text.trim().ifEmpty { "No warnings." }.lines()
    }.getOrElse { listOf("Couldn't read system log: ${it.message}") }
}

@Composable
internal fun DebugLogsPage(
    xrayLogs: List<String>,
    debugInfo: () -> String,
    onClear: () -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val system by produceState<List<String>?>(null) {
        while (true) {
            value = readSystemLog()
            delay(2000)
        }
    }
    val lines: List<String>? = remember(xrayLogs, system) {
        system?.let { sys ->
            xrayLogs.ifEmpty { listOf("No events yet.") } + "" + "--- Android warnings and errors ---" + sys
        }
    }

    val listState = rememberLazyListState()
    var pinned by remember { mutableStateOf(true) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.filter { !it }.collect { pinned = !listState.canScrollForward }
    }
    LaunchedEffect(lines) {
        val l = lines ?: return@LaunchedEffect
        if (pinned && l.isNotEmpty()) listState.scrollToItem(l.lastIndex)
    }

    SettingsFullPage(
        title = "Log",
        onBack = onBack,
        actions = {
            if (xrayLogs.isNotEmpty()) {
                IconButton(onClick = onClear, modifier = Modifier.testTag("btn_clear_xray_logs")) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Clear logs", tint = HollowInkSecondary)
                }
            }
            IconButton(
                onClick = { lines?.let { shareLogs(ctx, debugInfo(), it, onShowMessage) } },
                modifier = Modifier.testTag("btn_copy_xray_logs")
            ) {
                Icon(Icons.Outlined.Share, contentDescription = "Share logs", tint = HollowInk)
            }
        }
    ) {
        val shown = lines
        if (shown == null) {
            Text("Loading…", style = HollowText.Small, color = HollowInkSecondary)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(bottom = HollowSpace.Lg)
            ) {
                items(shown.size) { i ->
                    Text(
                        text = shown[i],
                        style = HollowText.Caption.copy(fontFamily = HollowMonoFamily),
                        color = if (shown[i].contains("ERROR:", ignoreCase = true)) HollowAccentText else HollowInk
                    )
                }
            }
        }
    }
}

private fun shareLogs(ctx: Context, info: String, lines: List<String>, onFail: (String) -> Unit) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, "Hollow VPN logs")
        .putExtra(Intent.EXTRA_TEXT, info + "\n\n" + lines.joinToString("\n"))
    runCatching { ctx.startActivity(Intent.createChooser(send, "Share logs")) }
        .onFailure { onFail("Couldn't share logs") }
}

private class AppInfo(val label: String, val pkg: String)

private sealed class SystemApps {
    object NotLoaded : SystemApps()
    object Loading : SystemApps()
    class Content(val apps: List<AppInfo>) : SystemApps()
}

private fun Context.installedInternetApps(withLaunchIntent: Boolean): List<String> {
    val pm = packageManager
    return pm.getInstalledApplications(0).map { it.packageName }.filter { pkg ->
        pkg != packageName &&
            pm.checkPermission(Manifest.permission.INTERNET, pkg) == PackageManager.PERMISSION_GRANTED &&
            (pm.getLaunchIntentForPackage(pkg) != null) == withLaunchIntent
    }
}

private fun Context.appInfos(packages: Collection<String>): List<AppInfo> = packages.map { pkg ->
    val label = runCatching { packageManager.getApplicationInfo(pkg, 0).loadLabel(packageManager).toString() }
        .getOrDefault(pkg)
    AppInfo(label, pkg)
}

@Composable
internal fun SplitAppsPage(
    including: Boolean,
    initial: Set<String>,
    onSave: (Set<String>) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    var sel by remember { mutableStateOf(initial) }
    var confirm by remember { mutableStateOf(false) }
    var showSystem by remember { mutableStateOf(false) }
    val dirty = sel != initial
    val leave: () -> Unit = { if (dirty) confirm = true else onBack() }
    BackHandler(true) { leave() }

    val title = if (including) "Included apps" else "Excluded apps"
    val note = if (including) "Only these apps connect through the VPN." else "These apps connect directly, bypassing the VPN."

    val regular by produceState<List<AppInfo>?>(null) {
        value = withContext(Dispatchers.Default) {
            ctx.appInfos(ctx.installedInternetApps(true)).sortedBy { it.label.lowercase() }
        }
    }
    val systemPackages by produceState<List<String>>(emptyList()) {
        value = withContext(Dispatchers.Default) { ctx.installedInternetApps(false) }
    }
    val systemApps by produceState<SystemApps>(SystemApps.NotLoaded, showSystem) {
        if (!showSystem) {
            value = SystemApps.NotLoaded
            return@produceState
        }
        value = SystemApps.Loading
        value = withContext(Dispatchers.Default) {
            SystemApps.Content(ctx.appInfos(systemPackages).sortedBy { it.label.lowercase() })
        }
    }

    SettingsFullPage(
        title = title,
        onBack = leave,
        actions = { HeaderTextAction("Save") { onSave(sel); onBack() } }
    ) {
        val regularList = regular
        if (regularList == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                com.hollow.vpn.ui.components.HollowSegmentedSpinner()
            }
        } else {
            val loadedSystem = (systemApps as? SystemApps.Content)?.apps.orEmpty()
            val known = regularList + loadedSystem
            val missing = sel.filter { pkg -> known.none { it.pkg == pkg } }
            val chosen = (known.filter { it.pkg in sel } + ctx.appInfos(missing)).sortedBy { it.label.lowercase() }
            val others = regularList.filter { it.pkg !in sel }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("split_apps_list"),
                contentPadding = PaddingValues(bottom = HollowSpace.Lg)
            ) {
                item(key = "h1") { ListHeader("$title (${chosen.size})", note) }
                items(chosen, key = { "s_" + it.pkg }) { a ->
                    PickRow(a, add = false) { sel = sel - a.pkg }
                }
                item(key = "h2") { ListHeader("All other regular apps (${others.size})") }
                items(others, key = { "o_" + it.pkg }) { a ->
                    PickRow(a, add = true) { sel = sel + a.pkg }
                }
                item(key = "system_apps_checkbox") {
                    SystemAppsCheckboxRow(showSystem) { showSystem = !showSystem }
                }
                when (val state = systemApps) {
                    SystemApps.NotLoaded -> item(key = "system_apps_loading") { Spacer(Modifier.height(64.dp)) }
                    SystemApps.Loading -> item(key = "system_apps_loading") {
                        Box(Modifier.heightIn(min = 64.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            com.hollow.vpn.ui.components.HollowSegmentedSpinner()
                        }
                    }
                    is SystemApps.Content -> items(state.apps.filter { it.pkg !in sel }, key = { "y_" + it.pkg }) { a ->
                        PickRow(a, add = true) { sel = sel + a.pkg }
                    }
                }
            }
        }
    }
    if (confirm) DiscardDialog(onDiscard = { confirm = false; onBack() }, onKeep = { confirm = false })
}

@Composable
private fun SystemAppsCheckboxRow(checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Show system apps",
            style = HollowText.Body,
            color = HollowInk,
            modifier = Modifier.padding(vertical = HollowSpace.Md).weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = NothingInvertedSurface,
                uncheckedColor = HollowInkSecondary,
                checkmarkColor = NothingOnInvertedSurface
            ),
            modifier = Modifier.padding(HollowSpace.Xs).clearAndSetSemantics {}
        )
    }
}

@Composable
private fun PickRow(app: AppInfo, add: Boolean, showIcon: Boolean = true, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onAction),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            AppPickIcon(app.pkg)
            Spacer(Modifier.width(HollowSpace.Sm))
        }
        Text(
            app.label,
            style = HollowText.Body,
            color = HollowInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(vertical = HollowSpace.Md)
        )
        Spacer(Modifier.width(HollowSpace.Xs))
        Icon(
            imageVector = if (add) Icons.Outlined.AddCircleOutline else Icons.Outlined.RemoveCircle,
            contentDescription = if (add) "Add" else "Remove",
            tint = if (add) HollowInkSecondary else HollowInk,
            modifier = Modifier.padding(HollowSpace.Sm).size(HollowSizes.Icon)
        )
    }
}

@Composable
private fun AppPickIcon(pkg: String) {
    val pm = LocalContext.current.packageManager
    val px = with(LocalDensity.current) { HollowSizes.Icon.roundToPx() }
    val icon by produceState<ImageBitmap?>(null, pkg) {
        value = withContext(Dispatchers.Default) {
            runCatching {
                val d = pm.getApplicationIcon(pkg)
                val bmp = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                d.setBounds(0, 0, px, px)
                d.draw(canvas)
                bmp.asImageBitmap()
            }.getOrNull()
        }
    }
    val m = Modifier.size(HollowSizes.Icon)
    val i = icon
    if (i != null) Image(i, null, m) else Box(m)
}

@Composable
internal fun SplitIpsPage(
    including: Boolean,
    initial: List<String>,
    onSave: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    val title = if (including) "Included IP addresses" else "Excluded IP addresses"
    var ips by remember { mutableStateOf(initial) }
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var confirm by remember { mutableStateOf(false) }
    val dirty = ips != initial
    val leave: () -> Unit = { if (dirty) confirm = true else onBack() }
    BackHandler(true) { leave() }
    val add: () -> Unit = {
        val ip = IpRoutes.parse(text)
        if (ip == null) {
            error = true
        } else {
            if (ip !in ips) ips = ips + ip
            text = ""
            error = false
        }
    }
    SettingsFullPage(
        title = title,
        onBack = leave,
        actions = { HeaderTextAction("Save") { onSave(ips); onBack() } }
    ) {
        Text(
            "IP address",
            style = HollowText.Caption,
            color = HollowInkSecondary,
            modifier = Modifier.padding(bottom = HollowSpace.Xs)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            HollowField(
                value = text,
                onValueChange = { text = it; error = false },
                modifier = Modifier.weight(1f).testTag("field_split_ip"),
                placeholder = "192.0.2.0",
                isError = error,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { add() }),
                trailingIcon = if (text.isNotEmpty()) ({
                    IconButton(onClick = { text = ""; error = false }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = HollowInkSecondary)
                    }
                }) else null
            )
            Spacer(Modifier.width(HollowSpace.Xs))
            IconButton(
                onClick = { if (IpRoutes.parse(text) != null) add() },
                modifier = Modifier.testTag("btn_add_split_ip")
            ) {
                Icon(
                    Icons.Outlined.AddCircleOutline,
                    contentDescription = "Add",
                    tint = if (IpRoutes.parse(text) != null) HollowInk else HollowInkSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(HollowSizes.Icon)
                )
            }
        }
        Spacer(Modifier.height(HollowSpace.Sm))
        Text(
            if (error) "Not a valid IPv4 or IPv6 address." else "Enter an IPv4 or IPv6 address, optionally with a prefix like 10.0.0.0/8.",
            style = HollowText.Caption,
            color = if (error) HollowError else HollowInkSecondary
        )
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = HollowSpace.Lg),
            verticalArrangement = Arrangement.Top
        ) {
            item(key = "h") { ListHeader("$title (${ips.size})") }
            items(ips, key = { it }) { ip ->
                PickRow(AppInfo(ip, ip), add = false, showIcon = false) { ips = ips - ip }
            }
        }
    }
    if (confirm) DiscardDialog(onDiscard = { confirm = false; onBack() }, onKeep = { confirm = false })
}
