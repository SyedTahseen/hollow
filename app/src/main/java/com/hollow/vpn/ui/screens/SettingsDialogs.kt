package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowLine2
import com.hollow.vpn.ui.components.HollowIcons
import com.hollow.vpn.ui.theme.HollowDisplayInk
import com.hollow.vpn.ui.theme.NothingOnInvertedSurface
import com.hollow.vpn.ui.theme.HollowMonoFamily
import com.hollow.vpn.ui.theme.NothingInvertedSurface
import com.hollow.vpn.ui.components.hollowFieldColors
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
                style = HollowText.Heading,
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
                                    selectedColor = NothingInvertedSurface,
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
        modifier = Modifier.border(HollowStroke.Hairline, HollowLine2, HollowDialog.Shape),
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = {
            Text(
                text = "${server.countryName} · Raw Config",
                style = HollowText.Heading,
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
                colors = hollowFieldColors(),
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
                            color = sheetRingInk.copy(alpha = 0.16f),
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
                                    fontWeight = FontWeight.Medium,
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
                                shape = RoundedCornerShape(HollowRadius.Pill),
                                color = NothingInvertedSurface
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    style = HollowText.Label,
                                    color = NothingOnInvertedSurface,
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
                color = HollowInkSecondary
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
                        icon = HollowIcons.Shield,
                        title = "Xray-core Tunnel Engine",
                        detail = "Real Xray outbound latency verification & high-speed multi-transport routing"
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = HollowCreamDivider,
                        modifier = Modifier.padding(vertical = HollowSpace.Md)
                    )
                    AboutFeatureRow(
                        icon = HollowIcons.Lock,
                        title = "Zero-Log Local State",
                        detail = "All servers, subscriptions, and routing preferences stay strictly on-device"
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = HollowCreamDivider,
                        modifier = Modifier.padding(vertical = HollowSpace.Md)
                    )
                    AboutFeatureRow(
                        icon = HollowIcons.Dns,
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
                        imageVector = HollowIcons.Copy,
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
            fontFamily = HollowMonoFamily,
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
