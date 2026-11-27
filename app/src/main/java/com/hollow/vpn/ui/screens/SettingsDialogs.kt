package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowText
import android.content.ClipData
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.hollow.vpn.BuildConfig
import com.hollow.vpn.data.AppLinks
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.ui.components.HollowBrandShieldLogo
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowModalSheet
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.NothingBorder
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingRed
import com.hollow.vpn.ui.theme.NothingSurface
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SettingChoiceOption(
    val value: String,
    val subtitle: String,
    val label: String = value
)

internal val UPDATE_INTERVAL_OPTIONS = listOf(
    SettingChoiceOption("15 min", "Checks often — best if servers drop frequently", "Every 15 min"),
    SettingChoiceOption("30 min", "Checks twice an hour for faster servers", "Every 30 min"),
    SettingChoiceOption("1 hour", "Balanced speed and battery usage", "Every 1 hour (Recommended)"),
    SettingChoiceOption("6 hours", "Light background usage to save battery", "Every 6 hours"),
    SettingChoiceOption("12 hours", "Checks twice a day", "Every 12 hours"),
    SettingChoiceOption("24 hours", "Checks once a day", "Once a day")
)

private data class ProxyAppChoice(val packageName: String, val label: String)

@Composable
internal fun XrayDiagnosticLogsDialog(
    logs: List<String>,
    onCopyLogs: () -> Unit,
    onClearLogs: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Terminal,
                contentDescription = null,
                tint = HollowInk
            )
        },
        title = {
            Text(
                text = "Diagnostic logs",
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                color = HollowInk
            )
        },
        text = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                shape = RoundedCornerShape(HollowRadius.Md),
                color = NothingCanvas,
                border = BorderStroke(HollowStroke.Hairline, NothingBorder)
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(HollowSpace.Xl),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No diagnostic events captured yet.\nConnect to a server to inspect live Xray core status and tunnel verification logs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = HollowInkSecondary
                        )
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(HollowSpace.Md),
                        verticalArrangement = Arrangement.spacedBy(HollowSpace.Xs)
                    ) {
                        logs.forEach { line ->
                            Text(
                                text = line,
                                style = TextStyle(
                                    fontFamily = HollowSansFamily,
                                    fontSize = HollowType.Caption,
                                    letterSpacing = HollowTracking.Caption,
                                    lineHeight = HollowLineHeight.Caption,
                                    color = if (line.contains("ERROR:", ignoreCase = true)) {
                                        HollowOrange
                                    } else {
                                        HollowInk
                                    }
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)) {
                if (logs.isNotEmpty()) {
                    TextButton(
                        onClick = onClearLogs,
                        modifier = Modifier.testTag("btn_clear_xray_logs")
                    ) {
                        Text("Clear", color = HollowOrange)
                    }
                    Button(
                        onClick = onCopyLogs,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HollowInk,
                            contentColor = HollowCream
                        ),
                        modifier = Modifier.testTag("btn_copy_xray_logs")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(HollowSizes.IconSm)
                        )
                        Spacer(Modifier.width(HollowSpace.Sm))
                        Text("Copy")
                    }
                } else {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = HollowInk)
                    }
                }
            }
        },
        dismissButton = {
            if (logs.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = HollowInk)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PerAppProxyPickerDialog(
    selectedPackages: Set<String>,
    including: Boolean = false,
    onSave: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val apps = remember(context) {
        val pm = context.packageManager
        val discovered = linkedMapOf<String, ProxyAppChoice>()
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        runCatching {
            pm.queryIntentActivities(launcherIntent, 0).forEach { resolveInfo ->
                val pkg = resolveInfo.activityInfo?.packageName ?: return@forEach
                if (pkg == context.packageName) return@forEach
                val label = resolveInfo.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: pkg
                discovered.putIfAbsent(pkg, ProxyAppChoice(pkg, label))
            }
        }
        runCatching {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .asSequence()
                .filter { it.packageName != context.packageName && pm.getLaunchIntentForPackage(it.packageName) != null }
                .forEach { appInfo ->
                    val label = pm.getApplicationLabel(appInfo).toString().ifBlank { appInfo.packageName }
                    discovered.putIfAbsent(appInfo.packageName, ProxyAppChoice(appInfo.packageName, label))
                }
        }
        discovered.values.sortedWith(
            compareByDescending<ProxyAppChoice> { it.packageName in selectedPackages }
                .thenBy { it.label.lowercase() }
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var workingSelection by remember(selectedPackages) { mutableStateOf(selectedPackages) }
    var showSelectedOnly by remember { mutableStateOf(false) }

    val filteredApps = remember(apps, searchQuery, showSelectedOnly, workingSelection) {
        val q = searchQuery.trim()
        apps.filter { app ->
            val matchesQuery = q.isEmpty() ||
                app.label.contains(q, ignoreCase = true) ||
                app.packageName.contains(q, ignoreCase = true)
            val matchesFilter = !showSelectedOnly || app.packageName in workingSelection
            matchesQuery && matchesFilter
        }
    }

    HollowModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = HollowScreenPadding)
                .padding(bottom = HollowSpace.Xxl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (including) "Included apps" else "Excluded apps",
                        fontFamily = HollowSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = HollowType.Display,
                        lineHeight = HollowLineHeight.Display,
                        letterSpacing = HollowTracking.Display,
                        color = HollowInk
                    )
                    Spacer(Modifier.height(HollowSpace.Xxs))
                    Text(
                        text = if (including) {
                            "Only these apps connect through the VPN."
                        } else {
                            "These apps connect directly, bypassing the VPN."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = HollowType.Small),
                        color = HollowInkSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(HollowRadius.Sm),
                    color = HollowCreamSurface,
                    border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
                ) {
                    Text(
                        text = "${workingSelection.size} apps",
                        fontFamily = HollowSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = HollowType.Small,
                        lineHeight = HollowLineHeight.Small,
                        letterSpacing = HollowTracking.Small,
                        color = if (workingSelection.isNotEmpty()) HollowOrange else HollowInkSecondary,
                        modifier = Modifier.padding(horizontal = HollowSpace.Md, vertical = HollowSpace.Sm)
                    )
                }
            }

            Spacer(Modifier.height(HollowSpace.Lg))

            Spacer(Modifier.height(HollowSpace.Md))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed apps", color = HollowInkMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = HollowInkSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                tint = HollowInkSecondary
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = HollowCreamSurface,
                    unfocusedContainerColor = HollowCreamSurface,
                    focusedBorderColor = HollowInk,
                    unfocusedBorderColor = HollowCreamDivider,
                    focusedTextColor = HollowInk,
                    unfocusedTextColor = HollowInk
                ),
                shape = RoundedCornerShape(HollowRadius.Md),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(HollowSpace.Sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = showSelectedOnly,
                    onClick = { showSelectedOnly = !showSelectedOnly },
                    label = {
                        Text(
                            text = "Selected (${workingSelection.size})",
                            fontSize = HollowType.Small,
                            lineHeight = HollowLineHeight.Small,
                            letterSpacing = HollowTracking.Small,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HollowInk,
                        selectedLabelColor = HollowCream,
                        containerColor = HollowCreamSurface,
                        labelColor = HollowInkSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = showSelectedOnly,
                        borderColor = HollowCreamDivider
                    )
                )
                if (workingSelection.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            workingSelection = emptySet()
                            showSelectedOnly = false
                        }
                    ) {
                        Text(
                            text = "Clear all",
                            color = HollowOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(HollowSpace.Xs))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                if (filteredApps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showSelectedOnly) "No apps selected yet" else "No matching apps found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = HollowInkSecondary
                            )
                        }
                    }
                } else {
                    items(filteredApps, key = { it.packageName }) { app ->
                        val checked = app.packageName in workingSelection
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(HollowRadius.Md))
                                .clickable(role = Role.Checkbox) {
                                    workingSelection = if (checked) {
                                        workingSelection - app.packageName
                                    } else {
                                        workingSelection + app.packageName
                                    }
                                },
                            shape = RoundedCornerShape(HollowRadius.Md),
                            color = if (checked) HollowCreamSurface else Color.Transparent,
                            border = if (checked) BorderStroke(HollowStroke.Hairline, HollowCreamDivider) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .padding(horizontal = HollowSpace.Md, vertical = HollowSpace.Sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InstalledAppIcon(
                                    packageName = app.packageName,
                                    appLabel = app.label
                                )
                                Spacer(Modifier.width(HollowSpace.Md))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.label,
                                        color = HollowInk,
                                        style = HollowText.Body,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = app.packageName,
                                        fontFamily = HollowSansFamily,
                                        color = HollowInkSecondary,
                                        fontSize = HollowType.Caption,
                                        lineHeight = HollowLineHeight.Caption,
                                        letterSpacing = HollowTracking.Caption,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { value ->
                                        workingSelection = if (value) {
                                            workingSelection + app.packageName
                                        } else {
                                            workingSelection - app.packageName
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = HollowOrange,
                                        uncheckedColor = HollowInkSecondary,
                                        checkmarkColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(HollowSpace.Lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(HollowRadius.Md),
                    border = BorderStroke(HollowStroke.Hairline, NothingBorder),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NothingSurface,
                        contentColor = HollowInk
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(HollowSizes.Button)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { onSave(workingSelection) },
                    shape = RoundedCornerShape(HollowRadius.Md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HollowInk,
                        contentColor = HollowCream
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(HollowSizes.Button)
                ) {
                    Text(
                        text = if (workingSelection.isEmpty()) "Save (Off)" else "Save (${workingSelection.size})",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InstalledAppIcon(
    packageName: String,
    appLabel: String
) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, packageName) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.packageManager
                    .getApplicationIcon(packageName)
                    .toBitmap(width = 80, height = 80)
                    .asImageBitmap()
            }.getOrNull()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = appLabel,
            modifier = Modifier
                .size(HollowSizes.Chip)
                .clip(RoundedCornerShape(HollowRadius.Sm))
        )
    } else {
        Surface(
            modifier = Modifier.size(HollowSizes.Chip),
            shape = RoundedCornerShape(HollowRadius.Sm),
            color = HollowCream,
            border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Apps,
                    contentDescription = null,
                    tint = HollowInkSecondary,
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingOptionPickerDialog(
    title: String,
    options: List<SettingChoiceOption>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    HollowModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = HollowScreenPadding)
                .padding(bottom = HollowSpace.Xxl)
        ) {
            Text(
                text = title,
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = HollowType.Display,
                lineHeight = HollowLineHeight.Display,
                letterSpacing = HollowTracking.Display,
                color = HollowInk
            )

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                options.forEach { option ->
                    val isSelected = option.value == selectedValue
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(HollowRadius.Md))
                            .clickable(role = Role.RadioButton) { onSelect(option.value) },
                        shape = RoundedCornerShape(HollowRadius.Md),
                        color = if (isSelected) HollowInk else NothingSurface,
                        border = if (isSelected) null else BorderStroke(HollowStroke.Hairline, NothingBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HollowSpace.Md, vertical = HollowSpace.Md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelect(option.value) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = HollowOrange,
                                    unselectedColor = HollowInkSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(HollowSpace.Sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSelected) HollowCream else HollowInk
                                )
                                Spacer(modifier = Modifier.height(HollowSpace.Xxs))
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) {
                                        HollowCream.copy(alpha = 0.72f)
                                    } else {
                                        HollowInkSecondary
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(HollowRadius.Md),
                border = BorderStroke(HollowStroke.Hairline, NothingBorder),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NothingSurface,
                    contentColor = HollowInk
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HollowSizes.Button)
            ) {
                Text("Close", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun RawXrayJsonEditorDialog(
    server: ServerEntity,
    onDismiss: () -> Unit,
    onSaveJson: (String) -> Unit
) {
    var jsonText by remember(server) { mutableStateOf(server.rawConfigJson) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = {
            Text(
                text = "${server.countryName} · Raw Config",
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                color = HollowInk
            )
        },
        text = {
            OutlinedTextField(
                value = jsonText,
                onValueChange = { jsonText = it },
                textStyle = TextStyle(
                    fontFamily = HollowSansFamily,
                    fontSize = HollowType.Small,
                    lineHeight = HollowLineHeight.Small,
                    letterSpacing = HollowTracking.Small,
                    color = HollowInk
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NothingCanvas,
                    unfocusedContainerColor = NothingCanvas,
                    focusedBorderColor = HollowInk,
                    unfocusedBorderColor = NothingBorder,
                    focusedTextColor = HollowInk,
                    unfocusedTextColor = HollowInk
                ),
                shape = RoundedCornerShape(HollowRadius.Md),
                minLines = 10,
                maxLines = 15,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSaveJson(jsonText) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HollowInk,
                    contentColor = HollowCream
                )
            ) {
                Text("Save Config")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = HollowInk)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutHollowSheet(
    onCopyBuildInfo: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val versionSummary = "Hollow VPN v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) · Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT}) · ${BuildConfig.APPLICATION_ID}"

    HollowModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HollowScreenPadding)
                .padding(bottom = HollowSpace.Xxl)
        ) {
            // Nothing OS Glyph Showcase Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HollowRadius.Xl),
                color = NothingSurface,
                border = BorderStroke(HollowStroke.Hairline, NothingBorder)
            ) {
                val sheetRingInk = HollowInk
                Box(modifier = Modifier.fillMaxWidth()) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val center = Offset(size.width * 0.88f, size.height * 0.25f)
                        drawCircle(
                            color = NothingRed.copy(alpha = 0.25f),
                            radius = 68.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = sheetRingInk.copy(alpha = 0.08f),
                            radius = 104.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HollowSpace.Xl)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HollowSpace.Lg)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(HollowSizes.Field)
                                    .clip(RoundedCornerShape(HollowRadius.Md))
                                    .background(NothingSurfaceElevated)
                                    .border(HollowStroke.Hairline, NothingBorder, RoundedCornerShape(HollowRadius.Md)),
                                contentAlignment = Alignment.Center
                            ) {
                                HollowBrandShieldLogo(
                                    backgroundColor = NothingSurfaceElevated,
                                    strokeColor = HollowInk,
                                    modifier = Modifier.size(HollowSizes.IconLg)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "HOLLOW VPN",
                                    fontFamily = DotoDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = HollowType.Headline,
                                    lineHeight = HollowLineHeight.Headline,
                                    letterSpacing = HollowTracking.Headline,
                                    color = HollowInk
                                )
                                Text(
                                    text = "Stealth Xray Multi-Protocol Tunnel",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = HollowType.Small),
                                    color = HollowInkSecondary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = HollowOrange
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    style = HollowText.Label,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = HollowSpace.Md, vertical = HollowSpace.Xs)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(HollowSpace.Lg))

                        Text(
                            text = "Built for fast, resilient encrypted routing across VLESS Reality, VMess, Trojan, Shadowsocks, and Hysteria2 with zero tracking and strict local control.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = HollowType.Body,
                                letterSpacing = HollowTracking.Body,
                                lineHeight = HollowLineHeight.Body
                            ),
                            color = HollowInkSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            Text(
                text = "ARCHITECTURE & PRIVACY",
                style = HollowText.Section,
                color = HollowOrange
            )
            Spacer(modifier = Modifier.height(HollowSpace.Md))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HollowRadius.Lg),
                color = HollowCreamSurface,
                border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
            ) {
                Column(modifier = Modifier.padding(HollowSpace.Lg)) {
                    AboutFeatureRow(
                        icon = Icons.Outlined.Shield,
                        title = "Xray-core Tunnel Engine",
                        detail = "Real Xray outbound latency verification & high-speed multi-transport routing"
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = HollowCreamDivider,
                        modifier = Modifier.padding(vertical = HollowSpace.Md)
                    )
                    AboutFeatureRow(
                        icon = Icons.Outlined.Lock,
                        title = "Zero-Log Local State",
                        detail = "All servers, subscriptions, and routing preferences stay strictly on-device"
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = HollowCreamDivider,
                        modifier = Modifier.padding(vertical = HollowSpace.Md)
                    )
                    AboutFeatureRow(
                        icon = Icons.Outlined.Dns,
                        title = "Encrypted DNS & Split Apps",
                        detail = "DoH DNS protection with per-app bypass and LAN routing controls"
                    )
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            Text(
                text = "BUILD METADATA",
                style = HollowText.Section,
                color = HollowInkSecondary
            )
            Spacer(modifier = Modifier.height(HollowSpace.Md))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HollowRadius.Lg),
                color = HollowCreamSurface,
                border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
            ) {
                Column(
                    modifier = Modifier.padding(HollowSpace.Lg),
                    verticalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
                ) {
                    AboutMetaRow(label = "VERSION", value = "v${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})")
                    AboutMetaRow(label = "PACKAGE", value = BuildConfig.APPLICATION_ID)
                    AboutMetaRow(label = "ANDROID SDK", value = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                    AboutMetaRow(label = "REPOSITORY", value = AppLinks.REPO_DISPLAY)
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                OutlinedButton(
                    onClick = {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        cm?.setPrimaryClip(ClipData.newPlainText("Hollow VPN Build Info", versionSummary))
                        onCopyBuildInfo("Build info copied")
                    },
                    shape = RoundedCornerShape(HollowRadius.Md),
                    border = BorderStroke(HollowStroke.Hairline, NothingBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HollowInk),
                    modifier = Modifier
                        .weight(1f)
                        .height(HollowSizes.Button)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(HollowSizes.IconSm)
                    )
                    Spacer(Modifier.width(HollowSpace.Sm))
                    Text(
                        text = "Copy info",
                        fontSize = HollowType.Body,
                        lineHeight = HollowLineHeight.Body,
                        letterSpacing = HollowTracking.Body,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutFeatureRow(
    icon: ImageVector,
    title: String,
    detail: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
    ) {
        Box(
            modifier = Modifier
                .size(HollowSizes.Chip)
                .clip(RoundedCornerShape(HollowRadius.Sm))
                .background(NothingSurfaceElevated)
                .border(HollowStroke.Hairline, NothingBorder, RoundedCornerShape(HollowRadius.Sm)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HollowInk,
                modifier = Modifier.size(HollowSizes.IconSm)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = HollowText.Body,
                color = HollowInk
            )
            Spacer(modifier = Modifier.height(HollowSpace.Xxs))
            Text(
                text = detail,
                style = HollowText.Small,
                color = HollowInkSecondary
            )
        }
    }
}

@Composable
private fun AboutMetaRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = HollowSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = HollowType.Micro,
            lineHeight = HollowLineHeight.Micro,
            letterSpacing = HollowTracking.Micro,
            color = HollowInkSecondary
        )
        Spacer(modifier = Modifier.width(HollowSpace.Md))
        Text(
            text = value,
            fontFamily = HollowSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = HollowType.Small,
            lineHeight = HollowLineHeight.Small,
            letterSpacing = HollowTracking.Small,
            color = HollowInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
