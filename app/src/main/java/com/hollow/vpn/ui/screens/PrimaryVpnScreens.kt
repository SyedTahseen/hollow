package com.hollow.vpn.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.R
import com.hollow.vpn.data.FetchingProgressState
import com.hollow.vpn.data.FetchingStage
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.ui.ConnectionTelemetryState
import com.hollow.vpn.ui.components.FetchingSpinnerRing
import com.hollow.vpn.ui.components.HollowBlinkingDot
import com.hollow.vpn.ui.components.HollowBrandShieldLogo
import com.hollow.vpn.ui.components.HollowDisconnectedExposureCard
import com.hollow.vpn.ui.components.HollowIndeterminateBar
import com.hollow.vpn.ui.components.HollowRouteState
import com.hollow.vpn.ui.components.HollowSlideActionPill
import com.hollow.vpn.ui.components.HollowStatusBrandRow
import com.hollow.vpn.ui.components.HollowTopSquareButton
import com.hollow.vpn.ui.components.NothingDotMatrixBackdrop
import com.hollow.vpn.ui.components.NothingDottedDivider
import com.hollow.vpn.ui.components.NothingSignalDots
import com.hollow.vpn.ui.map.GeoPoint
import com.hollow.vpn.ui.map.MapColors
import com.hollow.vpn.ui.map.MapPin
import com.hollow.vpn.ui.map.WorldGeo
import com.hollow.vpn.ui.map.WorldMap
import com.hollow.vpn.ui.components.CountryFlag
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowAmber
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowDarkInk
import com.hollow.vpn.ui.theme.HollowForest
import com.hollow.vpn.ui.theme.HollowForestCard
import com.hollow.vpn.ui.theme.HollowForestDivider
import com.hollow.vpn.ui.theme.HollowForestMuted
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowLime
import com.hollow.vpn.ui.theme.HollowObsidian
import com.hollow.vpn.ui.theme.HollowObsidianDivider
import com.hollow.vpn.ui.theme.HollowObsidianMuted
import com.hollow.vpn.ui.theme.HollowObsidianSurface
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.JetBrainsMonoFamily
import com.hollow.vpn.ui.theme.NothingBackdropDot
import com.hollow.vpn.ui.theme.NothingBlack
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingMatrixGreen
import com.hollow.vpn.ui.theme.NothingMotion
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
import com.hollow.vpn.ui.theme.SpaceGroteskFamily
import com.hollow.vpn.ui.theme.TabularMonoBody
import com.hollow.vpn.ui.theme.nothingPressScale
import java.util.Locale
import kotlin.math.max

/**
 * Formats internal protocol identifiers cleanly for consumer UI without technical clutter.
 */
internal fun formatConsumerProtocol(raw: String, transport: String? = null): String {
    val cleaned = raw
        .replace("(V2Ray)", "", ignoreCase = true)
        .replace("(Xray)", "", ignoreCase = true)
        .replace("V2Ray · ", "", ignoreCase = true)
        .replace("Xray ", "", ignoreCase = true)
        .trim()
        .ifBlank { "Auto" }

    val cleanTransport = transport?.trim().orEmpty()
    return if (cleanTransport.isNotBlank() && !cleaned.contains(cleanTransport, ignoreCase = true)) {
        "$cleaned · $cleanTransport"
    } else {
        cleaned
    }
}

/**
 * SCREEN 1: Hollow Startup & Subscription Streaming Pipeline Fetching Screen
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplashFetchingScreen(
    statusSubtitle: String,
    progress: FetchingProgressState = FetchingProgressState(isActive = true, detail = statusSubtitle),
    onCancelOrSkip: () -> Unit
) {
    BackHandler { onCancelOrSkip() }
    val rawDetail = progress.detail.ifBlank { statusSubtitle }
    val displaySubtitle = rawDetail
        .replace("V2Ray / Xray ", "", ignoreCase = true)
        .replace("V2Ray/Xray ", "", ignoreCase = true)

    val rawProgressFraction = when (progress.stage) {
        FetchingStage.DOWNLOADING -> 0.14f
        FetchingStage.TESTING_PIPELINE -> {
            val total = progress.targetTotalCount.coerceAtLeast(1)
            (0.18f + 0.62f * (progress.testedCount.toFloat() / total.toFloat())).coerceIn(0.18f, 0.80f)
        }
        FetchingStage.SPEED_AND_GEO -> 0.88f
        FetchingStage.FINALIZING -> 1.0f
        FetchingStage.IDLE -> 0.12f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgressFraction,
        animationSpec = tween(durationMillis = 320),
        label = "fetch_pipeline_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HollowCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        NothingDotMatrixBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .align(Alignment.Center)
                .padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: Brand + Live Pipeline status on left, Hide button on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HollowBrandShieldLogo(size = 34.dp, cornerRadius = 10.dp)
                    Column {
                        Text(
                            text = stringResource(R.string.app_name).uppercase(),
                            fontFamily = DotoDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = 0.8.sp,
                            color = HollowInk
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            HollowBlinkingDot(color = HollowOrange)
                            Text(
                                text = "LIVE PIPELINE",
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp,
                                color = HollowInkSecondary
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = HollowCreamSurface,
                    border = BorderStroke(1.dp, HollowCreamDivider),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(role = Role.Button, onClick = onCancelOrSkip)
                        .testTag("btn_hide_fetching_top")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Hide",
                            tint = HollowInk,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "Hide",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            color = HollowInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Main Editorial Pipeline Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = HollowCreamSurface,
                border = BorderStroke(1.dp, HollowCreamDivider)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FetchingSpinnerRing(size = 64.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = progress.headline.ifBlank { stringResource(R.string.fetching_servers_title) },
                        fontFamily = DotoDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.5.sp,
                        lineHeight = 23.sp,
                        color = HollowInk,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = displaySubtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = HollowInkSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress track
                    if (progress.stage == FetchingStage.DOWNLOADING && progress.parsedCount == 0) {
                        HollowIndeterminateBar(
                            color = HollowOrange,
                            track = HollowCreamDivider,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress.workingCount > 0) NothingMatrixGreen else HollowOrange,
                            trackColor = HollowCreamDivider,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dual-Lane Concurrent Pipeline Strip: FETCH -> TEST
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = HollowCream,
                        border = BorderStroke(1.dp, HollowCreamDivider)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    HollowBlinkingDot(color = HollowOrange)
                                    Text(
                                        text = "FETCH",
                                        fontFamily = JetBrainsMonoFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.8.sp,
                                        color = HollowInkSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${progress.parsedCount} parsed",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = HollowInk
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = HollowInkSecondary,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(16.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    HollowBlinkingDot(
                                        color = if (progress.workingCount > 0) NothingMatrixGreen else HollowOrange
                                    )
                                    Text(
                                        text = "TEST",
                                        fontFamily = JetBrainsMonoFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.8.sp,
                                        color = HollowInkSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${progress.workingCount} ready · ${progress.testedCount} probed",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (progress.workingCount > 0) NothingMatrixGreen else HollowInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3-Column Live Metrics Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FetchingMetricCell(
                            label = "REACHABLE",
                            value = "${progress.reachableCount}",
                            valueColor = HollowInk,
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(
                            thickness = 1.dp,
                            color = HollowCreamDivider,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        FetchingMetricCell(
                            label = "WORKING",
                            value = "${progress.workingCount}",
                            valueColor = if (progress.workingCount > 0) NothingMatrixGreen else HollowInk,
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(
                            thickness = 1.dp,
                            color = HollowCreamDivider,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        FetchingMetricCell(
                            label = "FASTEST",
                            value = if (progress.fastestPingMs > 0) "${progress.fastestPingMs}ms" else "—",
                            valueColor = if (progress.fastestPingMs > 0) NothingMatrixGreen else HollowInkSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Live Discovered Country Flags
                    AnimatedVisibility(
                        visible = progress.discoveredCountries.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(thickness = 1.dp, color = HollowCreamDivider)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "DISCOVERED LOCATIONS (${progress.countryCount})",
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 0.9.sp,
                                color = HollowInkSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                progress.discoveredCountries.take(8).forEach { (code, _) ->
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = HollowCream,
                                        border = BorderStroke(1.dp, HollowCreamDivider)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = code,
                                                fontFamily = JetBrainsMonoFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                color = HollowInk
                                            )
                                        }
                                    }
                                }
                                val extraCountries = progress.countryCount - progress.discoveredCountries.take(8).size
                                if (extraCountries > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = HollowCream,
                                        border = BorderStroke(1.dp, HollowCreamDivider)
                                    ) {
                                        Text(
                                            text = "+$extraCountries",
                                            fontFamily = JetBrainsMonoFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = HollowInkSecondary,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCancelOrSkip,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, HollowCreamDivider),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_hide_fetching")
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = HollowInk,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hide",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.5.sp,
                    color = HollowInk
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Runs in background — keep using Hollow while servers verify",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = HollowInkSecondary,
                textAlign = TextAlign.Center
            )
            }
        }
    }
}

@Composable
private fun FetchingMetricCell(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 0.7.sp,
            color = HollowInkSecondary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontFamily = DotoDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.5.sp,
            color = valueColor,
            maxLines = 1
        )
    }
}

/**
 * PAGES 1, 2, 3: Hollow Home / Connect Dashboard (Disconnected, Connecting, Protected)
 */
@Composable
fun ConnectedHomeScreen(
    selectedServer: ServerEntity?,
    telemetry: ConnectionTelemetryState,
    tunModeEnabled: Boolean = true,
    bottomContentPadding: Dp = 0.dp,
    onToggleConnect: () -> Unit,
    onOpenServerList: () -> Unit,
    onOpenSettings: () -> Unit,
    addServerActions: AddServerActions,
    onRefreshRealIp: () -> Unit = {}
) {
    val addServerState = rememberAddServerState()
    val homePhase = when {
        telemetry.isConnecting -> 1
        telemetry.isConnected -> 2
        else -> 0
    }

    val backgroundColor by animateColorAsState(
        targetValue = when (homePhase) {
            1 -> HollowObsidian
            2 -> HollowForest
            else -> HollowCream
        },
        animationSpec = tween(durationMillis = 280),
        label = "home_bg_color"
    )

    // ── World map layer (full-bleed, drawn behind the status bar; all content above is transparent) ──
    val appContext = LocalContext.current
    val geoReady by produceState(initialValue = WorldGeo.cached != null) {
        WorldGeo.load(appContext)
        value = WorldGeo.cached != null
    }
    val realPoint = remember(telemetry.realPublicLat, telemetry.realPublicLon, telemetry.realPublicCountryCode, geoReady) {
        val lat = telemetry.realPublicLat
        val lon = telemetry.realPublicLon
        if (lat != null && lon != null) GeoPoint(lon, lat)
        else telemetry.realPublicCountryCode.takeIf { it.length == 2 && !it.equals("XX", true) }
            ?.let { WorldGeo.centerOf(it) }
    }
    val serverPoint = remember(selectedServer?.countryCode, geoReady) {
        selectedServer?.countryCode?.takeIf { it.length == 2 }?.let { WorldGeo.centerOf(it) }
    }
    val mapTarget = if (homePhase == 0) realPoint ?: serverPoint else serverPoint ?: realPoint
    val mapCountry = if (homePhase == 0) telemetry.realPublicCountryCode.takeIf { it.length == 2 && !it.equals("XX", true) }
        else selectedServer?.countryCode?.takeIf { it.length == 2 }
    val palette = when (homePhase) {
        2 -> MapColors(
            background = HollowForest, sphere = HollowForest,
            land = Color.White.copy(alpha = 0.07f), landActive = HollowLime.copy(alpha = 0.22f),
            landLine = Color.White.copy(alpha = 0.14f)
        )
        1 -> MapColors(
            background = HollowObsidian, sphere = HollowObsidian,
            land = Color.White.copy(alpha = 0.07f), landActive = Color.White.copy(alpha = 0.20f),
            landLine = Color.White.copy(alpha = 0.14f)
        )
        else -> MapColors(
            background = HollowCream, sphere = HollowCream,
            land = HollowInk.copy(alpha = 0.07f), landActive = HollowOrange.copy(alpha = 0.22f),
            landLine = HollowInk.copy(alpha = 0.16f)
        )
    }
    val pinTone = when (homePhase) { 2 -> HollowLime; 1 -> Color.White; else -> HollowOrange }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (mapTarget != null) {
            WorldMap(
                target = mapTarget,
                countryCode = mapCountry,
                dotColor = pinTone,
                colors = palette,
                centerY = 0.40f,
                pin = when (homePhase) { 2 -> MapPin.Connected; 1 -> MapPin.Connecting; else -> MapPin.Selected },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            NothingDotMatrixBackdrop(
                dotColor = when (homePhase) {
                    2 -> HollowLime.copy(alpha = 0.032f)
                    1 -> Color.White.copy(alpha = 0.026f)
                    else -> NothingBackdropDot
                }
            )
        }
        AddServerOverlays(state = addServerState, actions = addServerActions, handleRequests = false)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = bottomContentPadding)
        ) {
            AnimatedContent(
                targetState = homePhase,
                transitionSpec = {
                    val ease = NothingMotion.FluidEase
                    (fadeIn(tween(260, easing = ease)) +
                        scaleIn(tween(300, easing = ease), initialScale = 0.96f) +
                        slideInVertically(tween(300, easing = ease)) { it / 22 })
                        .togetherWith(
                            fadeOut(tween(180, easing = ease)) +
                                scaleOut(tween(220, easing = ease), targetScale = 0.97f)
                        )
                },
                label = "home_phase_motion"
            ) { phase ->
                HomeContent(
                    phase = phase,
                    selectedServer = selectedServer,
                    telemetry = telemetry,
                    tunModeEnabled = tunModeEnabled,
                    onToggleConnect = onToggleConnect,
                    onOpenServerList = onOpenServerList,
                    onOpenSettings = onOpenSettings,
                    addServerState = addServerState,
                    addServerActions = addServerActions,
                    onRefreshRealIp = onRefreshRealIp
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    phase: Int,
    selectedServer: ServerEntity?,
    telemetry: ConnectionTelemetryState,
    tunModeEnabled: Boolean,
    onToggleConnect: () -> Unit,
    onOpenServerList: () -> Unit,
    addServerState: AddServerState,
    addServerActions: AddServerActions,
    onOpenSettings: () -> Unit,
    onRefreshRealIp: () -> Unit
) {
    val cleanIpDisplay = remember(telemetry.realPublicIpLabel) {
        telemetry.realPublicIpLabel.substringBefore(" · ").ifBlank { telemetry.realPublicIpLabel }
    }
    // "City · 1.2.3.4" (falls back to just the IP while the city is unknown).
    val ipLocationLine = remember(cleanIpDisplay, telemetry.realPublicCity) {
        if (telemetry.realPublicCity.isNotBlank()) "${telemetry.realPublicCity} · $cleanIpDisplay" else cleanIpDisplay
    }

    val serverIp = selectedServer?.resolvedIp?.ifBlank { selectedServer.host } ?: telemetry.serverIpLabel
    // The app only knows the server's country (no city field), so that is the "city" label for the VPN.
    val vpnLocationLine = "${selectedServer?.countryName ?: "VPN"} · $serverIp"
    val infoLine = if (phase == 0) ipLocationLine else vpnLocationLine

    val protectedPhase = phase == 2
    val connectingPhase = phase == 1
    val titleText = when (phase) { 2 -> "YOU ARE PROTECTED."; 1 -> "CONNECTING."; else -> "YOU ARE UNPROTECTED." }
    val titleColor = when (phase) { 2 -> HollowLime; 1 -> HollowAmber; else -> Color(0xFFD71921) }
    val infoColor = when (phase) { 2 -> HollowForestMuted; 1 -> HollowObsidianMuted; else -> HollowInkSecondary }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(horizontal = HollowScreenPadding)
    ) {
        val screenMaxHeight = maxHeight
        val isCompact = screenMaxHeight < 580.dp
        val cardGap = if (isCompact) 8.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(12.dp))
            when (phase) {
                2 -> HomeTopBar(
                    dotColor = HollowLime, textColor = HollowDarkInk,
                    buttonContainer = HollowForestCard, buttonContent = HollowDarkInk, buttonBorder = HollowForestDivider,
                    addServerState = addServerState, addServerActions = addServerActions, onOpenSettings = onOpenSettings
                )
                1 -> HomeTopBar(
                    dotColor = HollowAmber, textColor = HollowDarkInk,
                    buttonContainer = HollowObsidianSurface, buttonContent = HollowDarkInk, buttonBorder = HollowObsidianDivider,
                    addServerState = addServerState, addServerActions = addServerActions, onOpenSettings = onOpenSettings
                )
                else -> HomeTopBar(
                    dotColor = HollowOrange, textColor = HollowInk,
                    buttonContainer = NothingTopBtnFill, buttonContent = NothingOnCanvas, buttonBorder = NothingTopBtnBorder,
                    addServerState = addServerState, addServerActions = addServerActions, onOpenSettings = onOpenSettings
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 28.dp))

            Text(
                text = titleText,
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 21.sp else 24.sp,
                lineHeight = if (isCompact) 25.sp else 29.sp,
                letterSpacing = 0.5.sp,
                color = titleColor,
                modifier = Modifier.testTag("connection_status_text")
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier
                    .heightIn(min = 34.dp)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .testTag("row_real_ip"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = infoLine,
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = infoColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Empty, transparent space: the world map behind the whole screen shows through here.
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 120.dp)
            )

            Spacer(modifier = Modifier.height(cardGap))

            val serverCardInteraction = remember { MutableInteractionSource() }
            if (protectedPhase) {
                ConnectionStatsCard(
                    telemetry = telemetry,
                    onClick = onOpenServerList,
                    modifier = Modifier.testTag("active_server_pill")
                )
            } else Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .nothingPressScale(serverCardInteraction, pressedScale = 0.97f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = serverCardInteraction,
                        indication = androidx.compose.material3.ripple(),
                        role = Role.Button
                    ) {
                        if (selectedServer != null) onOpenServerList() else addServerState.menuOpen = true
                    }
                    .testTag("active_server_pill"),
                shape = RoundedCornerShape(20.dp),
                color = if (connectingPhase) HollowObsidianSurface else HollowCreamSurface,
                border = BorderStroke(1.dp, if (connectingPhase) HollowObsidianDivider else HollowCreamDivider)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = if (isCompact) 10.dp else 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedServer == null) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NothingSurfaceElevated)
                                .border(1.dp, HollowCreamDivider, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = HollowInk,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    } else {
                        CountryFlag(countryCode = selectedServer.countryCode)
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedServer?.countryName ?: "Add a server",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = if (connectingPhase) HollowDarkInk else HollowInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val subText = if (selectedServer != null) {
                            val host = selectedServer.resolvedIp.ifBlank { selectedServer.host }
                            "$host:${selectedServer.port}"
                        } else {
                            "Link, config or QR code"
                        }
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = if (connectingPhase) HollowObsidianMuted else HollowInkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (selectedServer != null) {
                        val pingColor = when {
                            selectedServer.pingMs in 1..150 -> NothingMatrixGreen
                            selectedServer.pingMs in 151..450 -> HollowInk
                            selectedServer.pingMs > 450 -> HollowOrange
                            else -> HollowInkSecondary
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedServer.pingMs > 0) "${selectedServer.pingMs} ms" else "—",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = pingColor,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Icon(
                        imageVector = if (selectedServer != null) {
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight
                        } else {
                            Icons.Rounded.Add
                        },
                        contentDescription = if (selectedServer != null) "Choose server" else "Add server",
                        tint = HollowInkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(cardGap + 8.dp))

            when (phase) {
                2 -> HollowSlideActionPill(
                    label = "Slide to disconnect",
                    trackColor = HollowForestCard,
                    thumbColor = HollowLime,
                    arrowColor = NothingBlack,
                    labelColor = HollowDarkInk,
                    borderColor = HollowForestDivider,
                    onSlideComplete = onToggleConnect,
                    modifier = Modifier.testTag("bottom_disconnect_connect_button")
                )
                1 -> Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(role = Role.Button, onClick = onToggleConnect)
                        .testTag("bottom_disconnect_connect_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = HollowObsidianSurface,
                    border = BorderStroke(1.dp, HollowObsidianDivider)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp),
                            color = HollowDarkInk
                        )
                    }
                }
                else -> HollowSlideActionPill(
                    label = "Slide to connect",
                    trackColor = NothingSurfaceElevated,
                    thumbColor = HollowOrange,
                    arrowColor = Color.White,
                    labelColor = HollowInk,
                    onSlideComplete = onToggleConnect,
                    modifier = Modifier.testTag("bottom_disconnect_connect_button")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

private fun formatConnectedFor(seconds: Long): String {
    val s = seconds.coerceAtLeast(0L)
    return when {
        s < 60L -> "$s sec"
        s < 3600L -> "${s / 60} min ${s % 60} sec"
        else -> "${s / 3600} hr ${(s % 3600) / 60} min"
    }
}

private fun formatSpeed2(bytesPerSec: Long): String {
    val b = bytesPerSec.coerceAtLeast(0L).toDouble()
    return when {
        b < 1024.0 -> String.format(Locale.US, "%.2f B/s", b)
        b < 1024.0 * 1024.0 -> String.format(Locale.US, "%.2f KB/s", b / 1024.0)
        b < 1024.0 * 1024.0 * 1024.0 -> String.format(Locale.US, "%.2f MB/s", b / (1024.0 * 1024.0))
        else -> String.format(Locale.US, "%.2f GB/s", b / (1024.0 * 1024.0 * 1024.0))
    }
}

/** Footer card while protected: "Connected for <time>" plus live download / upload speed, in the connected palette. */
@Composable
private fun ConnectionStatsCard(
    telemetry: ConnectionTelemetryState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val downColor = HollowLime
    val upColor = Color(0xFFF28B9B)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = HollowForestCard,
        border = BorderStroke(1.dp, HollowForestDivider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Connected for",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 10.sp,
                    letterSpacing = 0.7.sp,
                    color = HollowForestMuted,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatConnectedFor(telemetry.elapsedSeconds),
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = HollowDarkInk,
                    maxLines = 1,
                    modifier = Modifier.testTag("connection_timer_text")
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("↓", fontSize = 11.sp, color = downColor)
                    Text(
                        formatSpeed2(telemetry.downloadBytesPerSec),
                        fontFamily = SpaceGroteskFamily, fontSize = 11.sp, color = HollowDarkInk, maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("↑", fontSize = 11.sp, color = upColor)
                    Text(
                        formatSpeed2(telemetry.uploadBytesPerSec),
                        fontFamily = SpaceGroteskFamily, fontSize = 11.sp, color = HollowDarkInk, maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    dotColor: Color,
    textColor: Color,
    buttonContainer: Color,
    buttonContent: Color,
    buttonBorder: Color?,
    addServerState: AddServerState,
    addServerActions: AddServerActions,
    @Suppress("UNUSED_PARAMETER") onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HollowStatusBrandRow(
            dotColor = dotColor,
            textColor = textColor
        )

        AddServerButton(
            state = addServerState,
            actions = addServerActions,
            containerColor = buttonContainer,
            contentColor = buttonContent,
            borderColor = buttonBorder,
            testTag = "home_add_server_button"
        )
    }
}

/**
 * Editorial server node row used inside expanded country sections on Page 4.
 */
@Composable
fun ServerItemRowContent(
    server: ServerEntity,
    showLightningBadge: Boolean,
    onSelect: () -> Unit,
    onPing: () -> Unit,
    onInspectJson: () -> Unit,
    onDelete: () -> Unit,
    onCopyFeedback: (String) -> Unit = {},
    onShareQr: () -> Unit = {},
    isSelectionMode: Boolean = false,
    isCheckedForBatch: Boolean = false,
    onToggleBatchCheck: () -> Unit = {},
    showCountryBadge: Boolean = false,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cleanProtocol = remember(server.protocolCore, server.transport) {
        formatConsumerProtocol(server.protocolCore, server.transport)
    }

    val pingColor = when {
        server.pingMs in 1..150 -> NothingMatrixGreen
        server.pingMs in 151..450 -> HollowInk
        else -> HollowOrange
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelectionMode) {
            Checkbox(
                checked = isCheckedForBatch,
                onCheckedChange = { onToggleBatchCheck() },
                colors = CheckboxDefaults.colors(
                    checkedColor = HollowOrange,
                    uncheckedColor = HollowInkSecondary,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        CountryFlag(countryCode = server.countryCode)
        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = server.countryName.ifBlank { cleanProtocol },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = HollowInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (server.isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NothingMatrixGreen)
                            .semantics { contentDescription = "Active server" }
                    )
                } else if (showLightningBadge) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NothingCanvas,
                        border = BorderStroke(1.dp, HollowOrange)
                    ) {
                        Text(
                            text = "FAST",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp,
                            color = HollowOrange,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            val addrText = "${server.resolvedIp.ifBlank { server.host }}:${server.port}"
            Text(
                text = addrText,
                style = TabularMonoBody.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                color = HollowInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = cleanProtocol,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = HollowInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = if (server.pingMs > 0) "${server.pingMs} ms" else "Timeout",
                    style = TabularMonoBody.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = pingColor,
                    maxLines = 1
                )
            }
        }

        if (!isSelectionMode) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove ${server.countryName} from favorites" else "Add ${server.countryName} to favorites",
                    tint = if (isFavorite) HollowOrange else HollowInkSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (!isSelectionMode) {
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Server Options for ${server.countryName}",
                        tint = HollowInkSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = NothingSurfaceElevated
                ) {
                    DropdownMenuItem(
                        text = { Text("Connect Now", color = HollowInk) },
                        onClick = {
                            menuExpanded = false
                            onSelect()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            val pingSub = if (server.pingMs > 0) "${server.pingMs} ms" else "Timeout"
                            Text("Ping Test ($pingSub)", color = HollowInk)
                        },
                        onClick = {
                            menuExpanded = false
                            onPing()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Inspect Raw Config", color = HollowInk) },
                        onClick = {
                            menuExpanded = false
                            onInspectJson()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (server.shareUri.isNotBlank()) "Copy Share Link" else "Copy Raw Config",
                                color = HollowInk
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            val payload = server.shareUri.ifBlank { server.rawConfigJson }
                            if (payload.isNotBlank()) {
                                val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                cm?.setPrimaryClip(android.content.ClipData.newPlainText("Hollow VPN Server", payload))
                                onCopyFeedback("Copied server config for ${server.countryName}")
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Show QR Code", color = HollowInk) },
                        onClick = {
                            menuExpanded = false
                            onShareQr()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Server", color = HollowOrange) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
