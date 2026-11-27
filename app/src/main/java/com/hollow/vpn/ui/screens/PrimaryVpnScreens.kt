package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.components.NothingClock
import com.hollow.vpn.ui.components.HollowStateDot
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowText
import com.hollow.vpn.ui.theme.HollowUploadAccent
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.hollowPhaseColors
import com.hollow.vpn.ui.theme.HollowDisplayInk
import com.hollow.vpn.ui.theme.HollowSuccess
import com.hollow.vpn.ui.theme.HollowWarning
import com.hollow.vpn.ui.theme.HollowError
import com.hollow.vpn.ui.theme.HollowLine2
import com.hollow.vpn.ui.theme.HollowMonoFamily
import com.hollow.vpn.ui.theme.NothingInvertedSurface
import com.hollow.vpn.ui.theme.NothingOnInvertedSurface
import com.hollow.vpn.ui.theme.NothingSurface
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import com.hollow.vpn.vpn.ServerIpDetails
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
import com.hollow.vpn.ui.components.HollowBlinkingDot
import com.hollow.vpn.ui.components.HollowBrandShieldLogo
import com.hollow.vpn.ui.components.HollowIndeterminateBar
import com.hollow.vpn.ui.components.HollowSlideActionPill
import com.hollow.vpn.ui.components.HollowStatusBrandRow
import com.hollow.vpn.ui.components.NothingDotMatrixBackdrop
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
import com.hollow.vpn.ui.theme.HollowAccentText
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowLime
import com.hollow.vpn.ui.theme.HollowObsidian
import com.hollow.vpn.ui.theme.HollowObsidianDivider
import com.hollow.vpn.ui.theme.HollowObsidianSurface
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.NothingBackdropDot
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingMatrixGreen
import com.hollow.vpn.ui.theme.NothingMotion
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingRed
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
import com.hollow.vpn.ui.theme.TabularMonoBody
import com.hollow.vpn.ui.theme.nothingPressScale
import java.util.Locale
import kotlin.math.max

internal fun formatConsumerProtocol(raw: String, transport: String? = null): String {
    val cleaned = raw
        .replace("(V2Ray)", "", ignoreCase = true)
        .replace("(Xray)", "", ignoreCase = true)
        .replace("V2Ray · ", "", ignoreCase = true)
        .replace("Xray ", "", ignoreCase = true)
        .trim()
        .ifBlank { "Auto" }

    val cleanTransport = transport?.trim().orEmpty()
    val joined = if (cleanTransport.isNotBlank() && !cleaned.contains(cleanTransport, ignoreCase = true)) {
        "$cleaned / $cleanTransport"
    } else {
        cleaned
    }
    return joined
        .replace(Regex("\\s*[·•∙*]\\s*"), " / ")
        .replace(Regex("\\s*/\\s*(/\\s*)+"), " / ")
        .trim()
}

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
    onRefreshRealIp: () -> Unit = {},
    onLoadServerDetails: suspend (String) -> ServerIpDetails? = { null }
) {
    val addServerState = rememberAddServerState()
    val homePhase = when {
        telemetry.isConnecting -> 1
        telemetry.isConnected -> 2
        else -> 0
    }

    val backgroundColor by animateColorAsState(
        targetValue = HollowCream,
        animationSpec = tween(durationMillis = HollowMotion.Standard),
        label = "home_bg_color"
    )

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
    val displayInk = HollowDisplayInk
    val palette = MapColors(
        background = HollowCream, sphere = HollowCream,
        land = displayInk.copy(alpha = 0.07f), landActive = displayInk.copy(alpha = 0.18f),
        landLine = displayInk.copy(alpha = 0.14f)
    )
    // Pin = state dot: success when protected, warning while connecting, display when idle.
    val pinTone = when (homePhase) { 2 -> HollowSuccess; 1 -> HollowWarning; else -> NothingRed }

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
                dotColor = NothingBackdropDot
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
                    fadeIn(tween(HollowMotion.Emphasized, easing = ease))
                        .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))
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
                    onRefreshRealIp = onRefreshRealIp,
                    onLoadServerDetails = onLoadServerDetails
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
    onRefreshRealIp: () -> Unit,
    onLoadServerDetails: suspend (String) -> ServerIpDetails?
) {
    var detailsExpanded by remember { mutableStateOf(false) }
    val lookupIp = remember(selectedServer?.id, selectedServer?.resolvedIp, selectedServer?.host) {
        selectedServer?.let { it.resolvedIp.ifBlank { it.host } }.orEmpty()
    }
    val detailsLoad by produceState<Pair<Boolean, ServerIpDetails?>>(false to null, lookupIp, detailsExpanded) {
        if (detailsExpanded && IPV4_REGEX.matches(lookupIp)) {
            value = false to null
            value = true to onLoadServerDetails(lookupIp)
        } else if (!IPV4_REGEX.matches(lookupIp)) {
            value = true to null
        }
    }
    val cleanIpDisplay = remember(telemetry.realPublicIpLabel) {
        telemetry.realPublicIpLabel.substringBefore(" · ").ifBlank { telemetry.realPublicIpLabel }
    }
    val serverIp = selectedServer?.resolvedIp?.ifBlank { selectedServer.host } ?: telemetry.serverIpLabel
    val chipPlace = if (phase == 2) selectedServer?.countryName ?: "VPN" else telemetry.realPublicCity
    val chipIp = if (phase == 2) serverIp else cleanIpDisplay

    val protectedPhase = phase == 2
    val titleText = when (phase) { 2 -> "YOU ARE PROTECTED."; 1 -> "CONNECTING."; else -> "YOU ARE UNPROTECTED." }
    val pc = hollowPhaseColors(phase)
    val titleColor = if (phase == 0) NothingRed else HollowDisplayInk

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    BoxWithConstraints(
        modifier = Modifier
            .widthIn(max = HollowSizes.MaxContent)
            .fillMaxSize()
            .padding(horizontal = HollowScreenPadding)
    ) {
        val screenMaxHeight = maxHeight
        val isCompact = screenMaxHeight < 580.dp
        val cardGap = if (isCompact) 8.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(
                dotState = when (phase) { 2 -> HollowStateDot.Live; 1 -> HollowStateDot.Running; else -> HollowStateDot.NeedsInput },
                textColor = HollowDisplayInk,
                buttonContainer = NothingTopBtnFill, buttonContent = NothingOnCanvas, buttonBorder = NothingTopBtnBorder,
                addServerState = addServerState, addServerActions = addServerActions, onOpenSettings = onOpenSettings
            )

            Spacer(modifier = Modifier.height(if (isCompact) HollowSpace.Lg else HollowSpace.Xxl))

            Text(
                text = titleText,
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Medium,
                fontSize = if (isCompact) HollowType.Title else HollowType.Heading,
                lineHeight = if (isCompact) HollowLineHeight.Title else HollowLineHeight.Heading,
                letterSpacing = HollowTracking.Heading,
                color = titleColor,
                modifier = Modifier.testTag("connection_status_text")
            )

            Spacer(modifier = Modifier.height(HollowSpace.Xxs))

            Spacer(modifier = Modifier.height(HollowSpace.Xs))
            HomeInfoChip(
                place = chipPlace,
                ip = chipIp,
                scramble = phase == 1,
                placeColor = pc.ink,
                ipColor = pc.muted,
                modifier = Modifier.testTag("row_real_ip")
            )

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
                    expanded = detailsExpanded,
                    onClick = { if (selectedServer != null) detailsExpanded = !detailsExpanded else onOpenServerList() },
                    modifier = Modifier.testTag("active_server_pill"),
                    expandedContent = {
                        if (selectedServer != null) {
                            ServerDetailsPanel(
                                server = selectedServer,
                                details = detailsLoad.second,
                                loading = !detailsLoad.first,
                                textColor = pc.ink,
                                mutedColor = pc.muted,
                                dividerColor = pc.border,
                                accentColor = pc.accent,
                                onManageServers = onOpenServerList
                            )
                        }
                    }
                )
            } else Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .nothingPressScale(serverCardInteraction, pressedScale = HollowMotion.PressCard)
                    .clip(RoundedCornerShape(HollowRadius.Lg))
                    .clickable(
                        interactionSource = serverCardInteraction,
                        indication = androidx.compose.material3.ripple(),
                        role = Role.Button
                    ) {
                        if (selectedServer != null) detailsExpanded = !detailsExpanded else addServerState.menuOpen = true
                    }
                    .testTag("active_server_pill"),
                shape = RoundedCornerShape(HollowRadius.Lg),
                color = pc.surface,
                border = BorderStroke(HollowStroke.Hairline, pc.border)
            ) {
                Column(modifier = Modifier.fillMaxWidth().animateContentSize(tween(HollowMotion.Standard))) {
                Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HollowSpace.Lg, vertical = if (isCompact) HollowSpace.Md else HollowSpace.Md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedServer == null) {
                        Box(
                            modifier = Modifier
                                .size(HollowSizes.Chip)
                                .clip(RoundedCornerShape(HollowRadius.Sm))
                                .background(NothingSurfaceElevated)
                                .border(HollowStroke.Hairline, HollowCreamDivider, RoundedCornerShape(HollowRadius.Sm)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = HollowInk,
                                modifier = Modifier.size(HollowSizes.Icon)
                            )
                        }
                        Spacer(modifier = Modifier.width(HollowSpace.Md))
                    } else {
                        CountryFlag(countryCode = selectedServer.countryCode)
                        Spacer(modifier = Modifier.width(HollowSpace.Md))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedServer?.countryName ?: "Add a server",
                            style = HollowText.BodyMedium,
                            color = pc.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(HollowSpace.Xxs))
                        val subText = if (selectedServer != null) {
                            val host = selectedServer.resolvedIp.ifBlank { selectedServer.host }
                            "$host:${selectedServer.port}"
                        } else {
                            "Link, config or QR code"
                        }
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = HollowType.Caption),
                            color = pc.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (selectedServer != null) {
                        val pingColor = when {
                            selectedServer.pingMs in 1..150 -> NothingMatrixGreen
                            selectedServer.pingMs in 151..450 -> HollowInk
                            selectedServer.pingMs > 450 -> HollowError
                            else -> HollowInkSecondary
                        }
                        Spacer(modifier = Modifier.width(HollowSpace.Sm))
                        Text(
                            text = if (selectedServer.pingMs > 0) "${selectedServer.pingMs} ms" else "—",
                            style = HollowText.SmallMedium,
                            color = pingColor,
                            maxLines = 1
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add server",
                            tint = HollowInkSecondary,
                            modifier = Modifier.size(HollowSizes.Icon)
                        )
                    }
                }
                if (selectedServer != null) {
                    TrayHandle(
                        color = pc.muted,
                        expanded = detailsExpanded,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = HollowSpace.Xs)
                    )
                }
                }
                if (selectedServer != null) {
                    AnimatedVisibility(visible = detailsExpanded, enter = fadeIn(tween(HollowMotion.Fast)) + expandVertically(tween(HollowMotion.Standard)), exit = fadeOut(tween(HollowMotion.Quick)) + shrinkVertically(tween(HollowMotion.Fast))) {
                        ServerDetailsPanel(
                            server = selectedServer,
                            details = detailsLoad.second,
                            loading = !detailsLoad.first,
                            textColor = pc.ink,
                            mutedColor = pc.muted,
                            dividerColor = pc.border,
                            accentColor = pc.accent,
                            onManageServers = onOpenServerList
                        )
                    }
                }
                }
            }

            Spacer(modifier = Modifier.height(cardGap + HollowSpace.Sm))

            when (phase) {
                2 -> HollowSlideActionPill(
                    label = "Disconnect",
                    contentLabel = "Slide to disconnect",
                    trackColor = com.hollow.vpn.ui.theme.HollowBar.Fill,
                    thumbColor = NothingInvertedSurface,
                    arrowColor = NothingOnInvertedSurface,
                    labelColor = HollowInk,
                    chevronColor = HollowInk,
                    borderColor = com.hollow.vpn.ui.theme.HollowBar.Border,
                    reverse = true,
                    onSlideComplete = onToggleConnect,
                    modifier = Modifier.testTag("bottom_disconnect_connect_button")
                )
                1 -> Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HollowSizes.Button)
                        .clip(RoundedCornerShape(HollowRadius.Md))
                        .clickable(role = Role.Button, onClick = onToggleConnect)
                        .testTag("bottom_disconnect_connect_button"),
                    shape = RoundedCornerShape(HollowRadius.Md),
                    color = pc.surface,
                    border = BorderStroke(HollowStroke.Hairline, HollowLine2)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "CANCEL",
                            style = HollowText.Button,
                            color = pc.ink
                        )
                    }
                }
                else -> HollowSlideActionPill(
                    label = "Connect",
                    contentLabel = "Slide to connect",
                    trackColor = com.hollow.vpn.ui.theme.HollowBar.Fill,
                    thumbColor = NothingInvertedSurface,
                    arrowColor = NothingOnInvertedSurface,
                    labelColor = HollowInk,
                    chevronColor = HollowInk,
                    borderColor = com.hollow.vpn.ui.theme.HollowBar.Border,
                    onSlideComplete = onToggleConnect,
                    modifier = Modifier.testTag("bottom_disconnect_connect_button")
                )
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))
        }
    }
    }
}


private fun formatConnectedFor(seconds: Long): String {
    val s = seconds.coerceAtLeast(0L)
    return String.format(Locale.US, "%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
}

private fun formatSpeed2(bytesPerSec: Long): String {
    val b = bytesPerSec.coerceAtLeast(0L).toDouble()
    return when {
        b < 1024.0 -> String.format(Locale.US, "%.1f B/s", b)
        b < 1024.0 * 1024.0 -> String.format(Locale.US, "%.1f KB/s", b / 1024.0)
        b < 1024.0 * 1024.0 * 1024.0 -> String.format(Locale.US, "%.1f MB/s", b / (1024.0 * 1024.0))
        else -> String.format(Locale.US, "%.1f GB/s", b / (1024.0 * 1024.0 * 1024.0))
    }
}

@Composable
private fun TrayHandle(color: Color, expanded: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(40.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.55f))
            .semantics { contentDescription = if (expanded) "Collapse server details" else "Expand server details" }
    )
}

@Composable
private fun ConnectionStatsCard(
    telemetry: ConnectionTelemetryState,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expandedContent: @Composable () -> Unit = {}
) {
    val downColor = HollowSuccess
    val upColor = HollowInk
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(com.hollow.vpn.ui.theme.HollowBar.Shape)
            .clickable(role = Role.Button, onClick = onClick),
        shape = com.hollow.vpn.ui.theme.HollowBar.Shape,
        color = com.hollow.vpn.ui.theme.HollowBar.Fill,
        border = BorderStroke(HollowStroke.Hairline, com.hollow.vpn.ui.theme.HollowBar.Border)
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize(tween(HollowMotion.Standard))) {
        Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(com.hollow.vpn.ui.theme.HollowBar.Height)
                .padding(horizontal = HollowSpace.Lg),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NothingClock(
                    text = formatConnectedFor(telemetry.elapsedSeconds),
                    style = HollowText.NumeralSm,
                    color = HollowDisplayInk,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("connection_timer_text")
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs)) {
                        Text("↓", fontFamily = HollowMonoFamily, fontSize = HollowType.Small, lineHeight = HollowLineHeight.Small, color = downColor)
                        Text(
                            formatSpeed2(telemetry.downloadBytesPerSec),
                            fontFamily = HollowMonoFamily, fontWeight = FontWeight.Normal,
                            fontSize = HollowType.Small, lineHeight = HollowLineHeight.Small,
                            color = HollowInk, maxLines = 1, softWrap = false,
                            textAlign = TextAlign.End,
                            // Fixed slot (10 mono chars) so digits/units never shift the neighbours.
                            modifier = Modifier.widthIn(min = 84.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs)) {
                        Text("↑", fontFamily = HollowMonoFamily, fontSize = HollowType.Small, lineHeight = HollowLineHeight.Small, color = upColor)
                        Text(
                            formatSpeed2(telemetry.uploadBytesPerSec),
                            fontFamily = HollowMonoFamily, fontWeight = FontWeight.Normal,
                            fontSize = HollowType.Small, lineHeight = HollowLineHeight.Small,
                            color = HollowInk, maxLines = 1, softWrap = false,
                            textAlign = TextAlign.End,
                            // Fixed slot (10 mono chars) so digits/units never shift the neighbours.
                            modifier = Modifier.widthIn(min = 84.dp)
                        )
                    }
                }
            }
        }
        TrayHandle(
            color = HollowInkMuted,
            expanded = expanded,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = HollowSpace.Xs)
        )
        }
        AnimatedVisibility(visible = expanded, enter = fadeIn(tween(HollowMotion.Fast)) + expandVertically(tween(HollowMotion.Standard)), exit = fadeOut(tween(HollowMotion.Quick)) + shrinkVertically(tween(HollowMotion.Fast))) {
            expandedContent()
        }
        }
    }
}

@Composable
private fun HomeInfoChip(
    place: String,
    ip: String,
    scramble: Boolean,
    placeColor: Color,
    ipColor: Color,
    modifier: Modifier = Modifier
) {
    val placeText = place.trim()
    val label = when {
        ip.isBlank() && placeText.isBlank() -> "Locating…"
        placeText.isBlank() -> ip
        ip.isBlank() -> placeText
        else -> "$placeText · $ip"
    }
    val highlight = if (placeText.isNotBlank()) placeText else ""
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(tween(500)),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val style = androidx.compose.ui.text.TextStyle(
                fontFamily = HollowMonoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = HollowType.Small,
                lineHeight = HollowLineHeight.Small,
                letterSpacing = HollowTracking.Small
            )
            if (scramble) {
                ScrambleText(label, highlight, style, normal = placeColor, weak = ipColor)
            } else {
                Text(
                    text = androidx.compose.ui.text.buildAnnotatedString {
                        append(label)
                        if (highlight.isNotEmpty()) addStyle(androidx.compose.ui.text.SpanStyle(color = placeColor), 0, highlight.length)
                    },
                    style = style,
                    color = ipColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ScrambleText(
    text: String,
    highlight: String,
    style: androidx.compose.ui.text.TextStyle,
    normal: Color,
    weak: Color
) {
    var shown by remember(text) { mutableStateOf(text) }
    LaunchedEffect(text) {
        val order = text.indices.filter { text[it] != '.' && text[it] != ' ' && text[it] != '-' && text[it] != '·' }.shuffled()
        val chars = text.toCharArray()
        for (i in order) {
            kotlinx.coroutines.delay(50)
            chars[i] = '*'
            shown = String(chars)
        }
    }
    val start = if (highlight.isNotEmpty()) text.indexOf(highlight) else -1
    val annotated = androidx.compose.ui.text.buildAnnotatedString {
        append(shown)
        if (start >= 0) addStyle(androidx.compose.ui.text.SpanStyle(color = normal), start, start + highlight.length)
    }
    Box {
        Text(text, style = style, color = Color.Transparent, maxLines = 1)
        Text(annotated, style = style, color = weak, maxLines = 1)
    }
}

private val IPV4_REGEX = Regex("""^\d{1,3}(\.\d{1,3}){3}$""")

private fun formatServerLocalTime(timezone: String): String? {
    if (timezone.isBlank()) return null
    return try {
        ZonedDateTime.now(ZoneId.of(timezone)).format(DateTimeFormatter.ofPattern("EEE, MMM d · hh:mm a", Locale.US))
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun ServerDetailsPanel(
    server: ServerEntity,
    details: ServerIpDetails?,
    loading: Boolean,
    textColor: Color,
    mutedColor: Color,
    dividerColor: Color,
    accentColor: Color,
    onManageServers: () -> Unit
) {
    val ip = server.resolvedIp.ifBlank { server.host }
    val rows = buildList {
        add("IP address" to ip)
        add("Port" to server.port.toString())
        val location = listOf(details?.city, details?.region, details?.country ?: server.countryName.takeIf { it.isNotBlank() })
            .filter { !it.isNullOrBlank() }.distinct().joinToString(", ")
        if (location.isNotBlank()) add("Location" to location)
        details?.isp?.takeIf { it.isNotBlank() }?.let { add("ISP" to it) }
        details?.org?.takeIf { it.isNotBlank() && !it.equals(details?.isp, ignoreCase = true) }?.let { add("Company" to it) }
        details?.asn?.takeIf { it.isNotBlank() }?.let { add("ASN" to it) }
        details?.postal?.takeIf { it.isNotBlank() }?.let { add("Postal" to it) }
        details?.timezone?.takeIf { it.isNotBlank() }?.let { tz ->
            add("Timezone" to tz)
            formatServerLocalTime(tz)?.let { add("Local time" to it) }
        }
        if (details?.latitude != null && details.longitude != null) {
            add("Coordinates" to String.format(
                Locale.US, "%.4f° %s, %.4f° %s",
                kotlin.math.abs(details.latitude), if (details.latitude >= 0) "N" else "S",
                kotlin.math.abs(details.longitude), if (details.longitude >= 0) "E" else "W"
            ))
        }
        add("Protocol" to formatConsumerProtocol(server.protocolCore, server.transport))
        add("Latency" to if (server.pingMs > 0) "${server.pingMs} ms" else "—")
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = HollowSpace.Lg).padding(bottom = HollowSpace.Md)) {
        HorizontalDivider(color = dividerColor)
        Spacer(modifier = Modifier.height(HollowSpace.Sm))
        rows.forEach { (label, value) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = HollowSpace.Xs), verticalAlignment = Alignment.Top) {
                Text(
                    text = label.uppercase(),
                    fontFamily = HollowMonoFamily,
                    fontSize = HollowType.Caption,
                    lineHeight = HollowLineHeight.Caption,
                    letterSpacing = HollowTracking.Micro,
                    color = mutedColor,
                    modifier = Modifier.width(96.dp)
                )
                Text(
                    text = value,
                    fontFamily = HollowMonoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = HollowType.Caption,
                    lineHeight = HollowLineHeight.Caption,
                    letterSpacing = HollowTracking.Caption,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (loading) {
            Spacer(modifier = Modifier.height(HollowSpace.Xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.hollow.vpn.ui.components.HollowSegmentedSpinner(size = HollowSizes.IconSm)
                Spacer(modifier = Modifier.width(HollowSpace.Sm))
                Text("[LOADING…]", fontFamily = HollowMonoFamily, fontSize = HollowType.Caption, letterSpacing = HollowTracking.Micro, color = mutedColor)
            }
        } else if (details == null) {
            Spacer(modifier = Modifier.height(HollowSpace.Xs))
            Text("Extra network info unavailable right now.", fontFamily = HollowMonoFamily, fontSize = HollowType.Caption, color = mutedColor)
        }
        Spacer(modifier = Modifier.height(HollowSpace.Xs))
        TextButton(onClick = onManageServers, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = HollowSpace.Xs)) {
            Text("MANAGE SERVERS", style = HollowText.ButtonSmall, color = HollowInk)
            Spacer(modifier = Modifier.width(HollowSpace.Xs))
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = HollowInk, modifier = Modifier.size(HollowSizes.IconSm))
        }
    }
}

@Composable
private fun HomeTopBar(
    dotState: HollowStateDot,
    textColor: Color,
    buttonContainer: Color,
    buttonContent: Color,
    buttonBorder: Color?,
    addServerState: AddServerState,
    addServerActions: AddServerActions,
    @Suppress("UNUSED_PARAMETER") onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HollowStatusBrandRow(
            dotState = dotState,
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
    onToggleFavorite: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cleanProtocol = remember(server.protocolCore, server.transport) {
        formatConsumerProtocol(server.protocolCore, server.transport)
    }

    val pingColor = when {
        server.pingMs in 1..150 -> NothingMatrixGreen
        server.pingMs in 151..450 -> HollowInk
        else -> HollowError
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 0.dp, top = HollowSpace.Md, bottom = HollowSpace.Md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelectionMode) {
            Checkbox(
                checked = isCheckedForBatch,
                onCheckedChange = { onToggleBatchCheck() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NothingInvertedSurface,
                    uncheckedColor = HollowLine2,
                    checkmarkColor = NothingOnInvertedSurface
                )
            )
            Spacer(modifier = Modifier.width(HollowSpace.Xs))
        }

        CountryFlag(countryCode = server.countryCode)
        Spacer(modifier = Modifier.width(HollowSpace.Lg))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                Text(
                    text = server.countryName.ifBlank { cleanProtocol },
                    style = HollowText.Body,
                    color = HollowInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (server.isSelected) {
                    Box(
                        modifier = Modifier
                            .size(HollowSizes.Dot)
                            .clip(CircleShape)
                            .background(NothingMatrixGreen)
                            .semantics { contentDescription = "Active server" }
                    )
                } else if (showLightningBadge) {
                    Surface(
                        shape = RoundedCornerShape(HollowRadius.Xs),
                        color = Color.Transparent,
                        border = BorderStroke(HollowStroke.Hairline, HollowLine2)
                    ) {
                        Text(
                            text = "FAST",
                            style = HollowText.Label,
                            color = HollowInk,
                            modifier = Modifier.padding(horizontal = HollowSpace.Xs, vertical = HollowSpace.Xxs)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(HollowSpace.Xxs))
            val addrText = "${server.resolvedIp.ifBlank { server.host }}:${server.port}"
            Text(
                text = addrText,
                style = HollowText.Small,
                color = HollowInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = cleanProtocol,
                style = HollowText.Caption,
                color = HollowInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(HollowSpace.Sm))
        Text(
            text = if (server.pingMs > 0) "${server.pingMs} ms" else "TIMEOUT",
            style = HollowText.Data.copy(fontFeatureSettings = "tnum"),
            color = pingColor,
            maxLines = 1
        )

        if (!isSelectionMode) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove ${server.countryName} from favorites" else "Add ${server.countryName} to favorites",
                    tint = if (isFavorite) HollowDisplayInk else HollowInkSecondary,
                    modifier = Modifier.size(HollowSizes.Icon)
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
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "Server Options for ${server.countryName}",
                        tint = HollowInkSecondary,
                        modifier = Modifier.size(HollowSizes.Icon)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = NothingSurface,
                    shape = RoundedCornerShape(HollowRadius.Sm),
                    border = BorderStroke(HollowStroke.Hairline, HollowLine2)
                ) {
                    DropdownMenuItem(
                        text = { Text("Copy", style = HollowText.Data, color = HollowInk) },
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
                        text = { Text("Edit", style = HollowText.Data, color = HollowInk) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", style = HollowText.Data, color = HollowError) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(HollowSpace.Sm))
        }
    }
}
