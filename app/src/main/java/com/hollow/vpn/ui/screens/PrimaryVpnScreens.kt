package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowText
import com.hollow.vpn.ui.theme.HollowUploadAccent
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.hollowPhaseColors
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreHoriz
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
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
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
        targetValue = when (homePhase) {
            1 -> HollowObsidian
            2 -> HollowForest
            else -> HollowCream
        },
        animationSpec = tween(durationMillis = HollowMotion.Standard),
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
                    (fadeIn(tween(HollowMotion.Standard, easing = ease)) +
                        scaleIn(tween(HollowMotion.Emphasized, easing = ease), initialScale = 0.96f) +
                        slideInVertically(tween(HollowMotion.Emphasized, easing = ease)) { it / 22 })
                        .togetherWith(
                            fadeOut(tween(HollowMotion.Fast, easing = ease)) +
                                scaleOut(tween(HollowMotion.Standard, easing = ease), targetScale = 0.97f)
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
    // Tapping the server card expands it in place to show details about the proxy.
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
    // "City · 1.2.3.4" (falls back to just the IP while the city is unknown).
    val ipLocationLine = remember(cleanIpDisplay, telemetry.realPublicCity) {
        if (telemetry.realPublicCity.isNotBlank()) "${telemetry.realPublicCity} · $cleanIpDisplay" else cleanIpDisplay
    }

    val serverIp = selectedServer?.resolvedIp?.ifBlank { selectedServer.host } ?: telemetry.serverIpLabel
    // The app only knows the server's country (no city field), so that is the "city" label for the VPN.
    val vpnLocationLine = "${selectedServer?.countryName ?: "VPN"} · $serverIp"
    val infoLine = if (phase == 0) ipLocationLine else vpnLocationLine

    val protectedPhase = phase == 2
    val titleText = when (phase) { 2 -> "YOU ARE PROTECTED."; 1 -> "CONNECTING."; else -> "YOU ARE UNPROTECTED." }
    val pc = hollowPhaseColors(phase)
    val titleColor = pc.accent
    val infoColor = pc.muted

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

            Spacer(modifier = Modifier.height(if (isCompact) HollowSpace.Lg else HollowSpace.Xxl))

            Text(
                text = titleText,
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) HollowType.Title else HollowType.Display,
                lineHeight = if (isCompact) HollowLineHeight.Title else HollowLineHeight.Display,
                letterSpacing = if (isCompact) HollowTracking.Title else HollowTracking.Display,
                color = titleColor,
                modifier = Modifier.testTag("connection_status_text")
            )

            Spacer(modifier = Modifier.height(HollowSpace.Xxs))

            Row(
                modifier = Modifier
                    .heightIn(min = HollowSizes.CardRowMin)
                    .padding(vertical = HollowSpace.Xs, horizontal = HollowSpace.Xxs)
                    .testTag("row_real_ip"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                Text(
                    text = infoLine,
                    fontFamily = HollowSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = HollowType.Body,
                    lineHeight = HollowLineHeight.Body,
                    letterSpacing = HollowTracking.Body,
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
                                imageVector = Icons.Rounded.Add,
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
                            selectedServer.pingMs > 450 -> HollowOrange
                            else -> HollowInkSecondary
                        }
                        Spacer(modifier = Modifier.width(HollowSpace.Sm))
                        Text(
                            text = if (selectedServer.pingMs > 0) "${selectedServer.pingMs} ms" else "—",
                            style = HollowText.SmallMedium,
                            color = pingColor,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(HollowSpace.Xs))
                        val chevronRotation by animateFloatAsState(if (detailsExpanded) 180f else 0f, tween(HollowMotion.Standard), label = "server_card_chevron")
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (detailsExpanded) "Collapse server details" else "Expand server details",
                            tint = pc.muted,
                            modifier = Modifier.size(HollowSizes.Icon).rotate(chevronRotation)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add server",
                            tint = HollowInkSecondary,
                            modifier = Modifier.size(HollowSizes.Icon)
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
                    label = "Slide to disconnect",
                    trackColor = HollowForestCard,
                    thumbColor = pc.accent,
                    arrowColor = pc.onAccent,
                    labelColor = HollowDarkInk,
                    borderColor = HollowForestDivider,
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
                    border = BorderStroke(HollowStroke.Hairline, pc.border)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Cancel",
                            style = HollowText.BodyMedium,
                            color = pc.ink
                        )
                    }
                }
                else -> HollowSlideActionPill(
                    label = "Slide to connect",
                    trackColor = NothingSurfaceElevated,
                    thumbColor = pc.accent,
                    arrowColor = pc.onAccent,
                    labelColor = HollowInk,
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
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expandedContent: @Composable () -> Unit = {}
) {
    val pc = hollowPhaseColors(2)
    val downColor = pc.accent
    val upColor = HollowUploadAccent
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HollowRadius.Lg))
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(HollowRadius.Lg),
        color = pc.surface,
        border = BorderStroke(HollowStroke.Hairline, pc.border)
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize(tween(HollowMotion.Standard))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Connected for",
                    fontFamily = HollowSansFamily,
                    fontSize = HollowType.Micro,
                    lineHeight = HollowLineHeight.Micro,
                    letterSpacing = HollowTracking.Micro,
                    color = HollowForestMuted,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(HollowSpace.Xxs))
                Text(
                    text = formatConnectedFor(telemetry.elapsedSeconds),
                    fontFamily = HollowSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = HollowType.BodyStrong,
                    lineHeight = HollowLineHeight.BodyStrong,
                    letterSpacing = HollowTracking.BodyStrong,
                    color = HollowDarkInk,
                    maxLines = 1,
                    modifier = Modifier.testTag("connection_timer_text")
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs)) {
                    Text("↓", fontSize = HollowType.Caption, color = downColor)
                    Text(
                        formatSpeed2(telemetry.downloadBytesPerSec),
                        fontFamily = HollowSansFamily, fontSize = HollowType.Caption, color = HollowDarkInk, maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs)) {
                    Text("↑", fontSize = HollowType.Caption, color = upColor)
                    Text(
                        formatSpeed2(telemetry.uploadBytesPerSec),
                        fontFamily = HollowSansFamily, fontSize = HollowType.Caption, color = HollowDarkInk, maxLines = 1
                    )
                }
                val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f, tween(HollowMotion.Standard), label = "stats_card_chevron")
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse server details" else "Expand server details",
                    tint = HollowForestMuted,
                    modifier = Modifier.size(HollowSizes.Icon).rotate(chevronRotation)
                )
            }
        }
        AnimatedVisibility(visible = expanded, enter = fadeIn(tween(HollowMotion.Fast)) + expandVertically(tween(HollowMotion.Standard)), exit = fadeOut(tween(HollowMotion.Quick)) + shrinkVertically(tween(HollowMotion.Fast))) {
            expandedContent()
        }
        }
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

/** Expanded details of the selected proxy: location, network (ISP / ASN), timezone, coordinates and connection info. */
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
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                Text(
                    text = label,
                    fontFamily = HollowSansFamily,
                    fontSize = HollowType.Caption,
                    lineHeight = HollowLineHeight.Caption,
                    letterSpacing = HollowTracking.Caption,
                    color = mutedColor,
                    modifier = Modifier.width(92.dp)
                )
                Text(
                    text = value,
                    fontFamily = HollowSansFamily,
                    fontWeight = FontWeight.Medium,
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
                CircularProgressIndicator(modifier = Modifier.size(HollowSizes.IconSm), strokeWidth = 1.5.dp, color = accentColor)
                Spacer(modifier = Modifier.width(HollowSpace.Sm))
                Text("Loading server info…", fontFamily = HollowSansFamily, fontSize = HollowType.Caption, color = mutedColor)
            }
        } else if (details == null) {
            Spacer(modifier = Modifier.height(HollowSpace.Xs))
            Text("Extra network info unavailable right now.", fontFamily = HollowSansFamily, fontSize = HollowType.Caption, color = mutedColor)
        }
        Spacer(modifier = Modifier.height(HollowSpace.Xs))
        TextButton(onClick = onManageServers, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 4.dp)) {
            Text("Manage servers", style = HollowText.SmallMedium, color = accentColor)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = accentColor, modifier = Modifier.size(HollowSizes.IconSm))
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
        modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
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
        else -> HollowOrange
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
                    checkedColor = HollowOrange,
                    uncheckedColor = HollowInkSecondary,
                    checkmarkColor = Color.White
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NothingMatrixGreen)
                            .semantics { contentDescription = "Active server" }
                    )
                } else if (showLightningBadge) {
                    Surface(
                        shape = RoundedCornerShape(HollowRadius.Xs),
                        color = NothingCanvas,
                        border = BorderStroke(HollowStroke.Hairline, HollowOrange)
                    ) {
                        Text(
                            text = "FAST",
                            style = HollowText.Label,
                            color = HollowOrange,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)
            ) {
                Text(
                    text = cleanProtocol,
                    style = HollowText.Caption,
                    color = HollowInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = if (server.pingMs > 0) "${server.pingMs} ms" else "Timeout",
                    style = HollowText.SmallMedium.copy(fontFeatureSettings = "tnum"),
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
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "Server Options for ${server.countryName}",
                        tint = HollowInkSecondary,
                        modifier = Modifier.size(HollowSizes.Icon)
                    )
                }

                // Same three actions as Outrun: Copy, Edit, Delete.
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = NothingSurfaceElevated
                ) {
                    DropdownMenuItem(
                        text = { Text("Copy", color = HollowInk) },
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
                        text = { Text("Edit", color = HollowInk) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = HollowOrange) },
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
