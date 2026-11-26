package com.hollow.vpn.ui.components

import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSpace
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowForest
import com.hollow.vpn.ui.theme.HollowForestCard
import com.hollow.vpn.ui.theme.HollowForestDivider
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowObsidian
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.NothingBlack
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingPillActiveBg
import com.hollow.vpn.ui.theme.NothingPillActiveIcon
import com.hollow.vpn.ui.theme.NothingPillNavBorder
import com.hollow.vpn.ui.theme.NothingPillNavFill
import com.hollow.vpn.ui.theme.NothingSwitchOffBorder
import com.hollow.vpn.ui.theme.NothingSwitchOffThumb
import com.hollow.vpn.ui.theme.NothingSwitchOffTrack
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ──────────────────────────────────────────────────────────────────────────────
// Hollow Editorial Redesign Primitives
// ──────────────────────────────────────────────────────────────────────────────

enum class HollowBottomTab {
    CONNECT,
    SERVERS,
    SETTINGS
}

@Composable
fun HollowBrandShieldLogo(
    size: Dp = 120.dp,
    cornerRadius: Dp = 34.dp,
    backgroundColor: Color = HollowOrange,
    strokeColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = this.size.minDimension / 108f
            val shieldPath = Path().apply {
                moveTo(54f * scale, 28f * scale)
                lineTo(75f * scale, 37f * scale)
                lineTo(75f * scale, 54f * scale)
                cubicTo(
                    75f * scale, 68f * scale,
                    66f * scale, 77f * scale,
                    54f * scale, 81f * scale
                )
                cubicTo(
                    42f * scale, 77f * scale,
                    33f * scale, 68f * scale,
                    33f * scale, 54f * scale
                )
                lineTo(33f * scale, 37f * scale)
                close()
            }
            drawPath(
                path = shieldPath,
                color = strokeColor,
                style = Stroke(
                    width = 7f * scale,
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Round
                )
            )
            drawCircle(
                color = strokeColor,
                radius = 7f * scale,
                center = Offset(54f * scale, 56f * scale)
            )
        }
    }
}

@Composable
fun HollowStatusBrandRow(
    @Suppress("UNUSED_PARAMETER") dotColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = androidx.compose.ui.text.buildAnnotatedString {
            append("HOLLOW ")
            pushStyle(androidx.compose.ui.text.SpanStyle(color = com.hollow.vpn.ui.theme.NothingRed))
            append("VPN")
            pop()
        },
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = HollowType.Display,
        lineHeight = HollowLineHeight.Display,
        letterSpacing = HollowTracking.Display,
        maxLines = 1,
        color = textColor,
        modifier = modifier
    )
}

@Composable
fun HollowTopSquareButton(
    containerColor: Color,
    contentColor: Color,
    borderColor: Color? = NothingTopBtnBorder,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(Color) -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val animContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "top_btn_container"
    )
    val animBorder by animateColorAsState(
        targetValue = borderColor ?: NothingTopBtnBorder,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "top_btn_border"
    )
    val animContent by animateColorAsState(
        targetValue = contentColor,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "top_btn_content"
    )
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(with(com.hollow.vpn.ui.theme.NothingMotion) { Modifier })
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                role = Role.Button,
                onClick = onClick
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        val isPressed by interactionSource.collectIsPressedAsState()
        val btnScale by animateFloatAsState(
            targetValue = if (isPressed) 0.90f else 1f,
            animationSpec = if (isPressed) {
                tween(HollowMotion.Quick, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase)
            } else {
                com.hollow.vpn.ui.theme.NothingMotion.tactileSpring()
            },
            label = "top_btn_scale"
        )
        Surface(
            modifier = Modifier
                .size(HollowSizes.Chip)
                .graphicsLayer {
                    scaleX = btnScale
                    scaleY = btnScale
                },
            shape = CircleShape,
            color = animContainer,
            border = BorderStroke(HollowStroke.Hairline, animBorder)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content(animContent)
            }
        }
    }
}

@Composable
fun HollowBottomNavigationBar(
    selectedTab: HollowBottomTab,
    backgroundColor: Color,
    borderColor: Color,
    activeIndicatorColor: Color,
    activeContentColor: Color,
    inactiveContentColor: Color,
    onSelectConnect: () -> Unit,
    onSelectServers: () -> Unit,
    onSelectSettings: () -> Unit,
    modifier: Modifier = Modifier,
    /** false = no full-width backdrop behind the pill, so whatever is behind the bar (the map) shows through. */
    showBackdrop: Boolean = true
) {
    val isDarkPhaseBg = backgroundColor == HollowForest || backgroundColor == HollowObsidian
    val capsuleFill = when (backgroundColor) {
        HollowForest -> HollowForestCard
        HollowObsidian -> Color(0xFF141417)
        else -> NothingPillNavFill
    }
    val capsuleBorder = when (backgroundColor) {
        HollowForest -> HollowForestDivider
        HollowObsidian -> Color(0xFF26262C)
        else -> NothingPillNavBorder
    }
    val activePillBg = if (isDarkPhaseBg) Color.White else NothingPillActiveBg
    val activeIconTint = if (isDarkPhaseBg) NothingBlack else NothingPillActiveIcon

    val animBg by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(HollowMotion.Standard, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "nav_bar_bg"
    )
    val animCapsuleFill by animateColorAsState(
        targetValue = capsuleFill,
        animationSpec = tween(HollowMotion.Standard, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "nav_capsule_fill"
    )
    val animCapsuleBorder by animateColorAsState(
        targetValue = capsuleBorder,
        animationSpec = tween(HollowMotion.Standard, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "nav_capsule_border"
    )
    val animActivePillBg by animateColorAsState(
        targetValue = activePillBg,
        animationSpec = tween(HollowMotion.Standard, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "nav_active_pill"
    )

    val selectedIndex = when (selectedTab) {
        HollowBottomTab.CONNECT -> 0
        HollowBottomTab.SERVERS -> 1
        HollowBottomTab.SETTINGS -> 2
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(if (showBackdrop) animBg else Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = HollowSpace.Gutter, vertical = HollowSpace.Sm),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 352.dp)
                .height(HollowSizes.Field),
            shape = CircleShape,
            color = animCapsuleFill,
            border = BorderStroke(HollowStroke.Hairline, animCapsuleBorder)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = HollowSpace.Sm, vertical = HollowSpace.Sm)
            ) {
                val itemGap = 6.dp
                val slotWidth = ((maxWidth - itemGap * 2) / 3f).coerceAtLeast(1.dp)
                val targetIndicatorOffset = (slotWidth + itemGap) * selectedIndex
                val animatedIndicatorOffset by animateDpAsState(
                    targetValue = targetIndicatorOffset,
                    animationSpec = spring(
                        dampingRatio = 0.78f,
                        stiffness = 460f
                    ),
                    label = "nav_capsule_glide"
                )

                // Gliding Nothing OS 3.0 active capsule indicator
                Box(
                    modifier = Modifier
                        .offset(x = animatedIndicatorOffset)
                        .width(slotWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(animActivePillBg)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(itemGap)
                ) {
                    HollowBottomNavItem(
                        label = "Connect",
                        selected = selectedTab == HollowBottomTab.CONNECT,
                        activeIconColor = activeIconTint,
                        inactiveContentColor = inactiveContentColor,
                        onClick = onSelectConnect,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tab_connect")
                    ) { tint ->
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Connect",
                            tint = tint,
                            modifier = Modifier.size(HollowSizes.Icon)
                        )
                    }

                    HollowBottomNavItem(
                        label = "Servers",
                        selected = selectedTab == HollowBottomTab.SERVERS,
                        activeIconColor = activeIconTint,
                        inactiveContentColor = inactiveContentColor,
                        onClick = onSelectServers,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tab_servers")
                    ) { tint ->
                        Icon(
                            imageVector = Icons.Outlined.Dns,
                            contentDescription = "Servers",
                            tint = tint,
                            modifier = Modifier.size(HollowSizes.Icon)
                        )
                    }

                    HollowBottomNavItem(
                        label = "Settings",
                        selected = selectedTab == HollowBottomTab.SETTINGS,
                        activeIconColor = activeIconTint,
                        inactiveContentColor = inactiveContentColor,
                        onClick = onSelectSettings,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tab_settings")
                    ) { tint ->
                        Icon(
                            imageVector = if (selectedTab == HollowBottomTab.SETTINGS) {
                                Icons.Rounded.Settings
                            } else {
                                Icons.Outlined.Settings
                            },
                            contentDescription = "Settings",
                            tint = tint,
                            modifier = Modifier.size(HollowSizes.Icon)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HollowBottomNavItem(
    label: String,
    selected: Boolean,
    activeIconColor: Color,
    inactiveContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val iconTint by animateColorAsState(
        targetValue = if (selected) activeIconColor else inactiveContentColor,
        animationSpec = tween(durationMillis = HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "nav_pill_tint"
    )
    val iconScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.86f
            selected -> 1.08f
            else -> 0.96f
        },
        animationSpec = com.hollow.vpn.ui.theme.NothingMotion.tactileSpring(),
        label = "nav_icon_scale"
    )
    Box(
        modifier = modifier
            .fillMaxHeight()
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
            }
        ) {
            icon(iconTint)
        }
    }
}

/**
 * Interactive Nothing OS slide-to-connect / slide-to-disconnect pill.
 */
@Composable
fun HollowSlideActionPill(
    label: String,
    trackColor: Color,
    thumbColor: Color,
    arrowColor: Color,
    labelColor: Color,
    onSlideComplete: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = HollowCreamDivider
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val thumbSize = HollowSizes.Field - 12.dp
    val trackPadding = 6.dp

    val nudgeMotion = rememberInfiniteTransition(label = "slide_nudge")
    val nudgeOffsetDp by nudgeMotion.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slide_nudge_x"
    )

    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(HollowSizes.Field)
            .clip(RoundedCornerShape(HollowRadius.Pill))
            .background(trackColor)
            .border(HollowStroke.Hairline, borderColor, RoundedCornerShape(HollowRadius.Pill))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                role = Role.Button,
                onClick = onSlideComplete
            )
            .semantics { contentDescription = label }
    ) {
        val maxOffsetPx = with(density) {
            (maxWidth - thumbSize - trackPadding * 2).toPx().coerceAtLeast(1f)
        }
        val offsetX = remember(label) { Animatable(0f) }
        val progress = (offsetX.value / maxOffsetPx).coerceIn(0f, 1f)
        val nudgePx = with(density) { nudgeOffsetDp.dp.toPx() }
        val nudgeFactor = (1f - progress * 10f).coerceIn(0f, 1f)

        // Progressive Nothing OS track fill glow while dragging
        if (progress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { (offsetX.value + thumbSize.toPx() + trackPadding.toPx() * 2).toDp() })
                    .clip(RoundedCornerShape(HollowRadius.Pill))
                    .background(thumbColor.copy(alpha = 0.14f + progress * 0.22f))
            )
        }

        Text(
            text = label.uppercase(),
            fontFamily = DotoDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = HollowType.Small,
            lineHeight = HollowLineHeight.Small,
            letterSpacing = HollowTracking.Small,
            color = labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 58.dp, end = 38.dp)
                .alpha((1f - progress * 1.45f).coerceIn(0f, 1f))
        )

        val thumbScale = 1f + progress * 0.06f
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = trackPadding)
                .offset { IntOffset((offsetX.value + nudgePx * nudgeFactor).roundToInt(), 0) }
                .size(thumbSize)
                .graphicsLayer {
                    scaleX = thumbScale
                    scaleY = thumbScale
                }
                .clip(CircleShape)
                .background(thumbColor)
                .pointerInput(maxOffsetPx, label) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value >= maxOffsetPx * 0.65f) {
                                    offsetX.animateTo(maxOffsetPx, tween(HollowMotion.Quick))
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSlideComplete()
                                    offsetX.snapTo(0f)
                                } else {
                                    offsetX.animateTo(
                                        0f,
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring())
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val next = (offsetX.value + dragAmount).coerceIn(0f, maxOffsetPx)
                                offsetX.snapTo(next)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = arrowColor,
                modifier = Modifier.size(HollowSizes.Icon)
            )
        }
    }
}

/**
 * Nothing OS toggle switch:
 * ON: Signature Nothing Red track with crisp white circular dot thumb
 * OFF: Matte black track with thin graphite border and muted gray dot thumb
 */
@Composable
fun HollowSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val trackColor by animateColorAsState(
        targetValue = if (checked) HollowOrange else NothingSwitchOffTrack,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "hollow_switch_track"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) HollowOrange else NothingSwitchOffBorder,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "hollow_switch_border"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) Color.White else NothingSwitchOffThumb,
        animationSpec = tween(HollowMotion.Fast, easing = com.hollow.vpn.ui.theme.NothingMotion.FluidEase),
        label = "hollow_switch_thumb"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) (HollowSizes.SwitchWidth - HollowSpace.Sm - HollowSizes.SwitchThumb) else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.68f,
            stiffness = 520f
        ),
        label = "hollow_switch_offset"
    )
    val thumbScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = com.hollow.vpn.ui.theme.NothingMotion.tactileSpring(),
        label = "hollow_switch_scale"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(HollowSizes.SwitchWidth)
                .height(HollowSizes.SwitchHeight)
                .clip(RoundedCornerShape(HollowRadius.Md))
                .background(trackColor)
                .border(HollowStroke.Emphasis, borderColor, RoundedCornerShape(HollowRadius.Md))
                .padding(horizontal = HollowSpace.Xs),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(HollowSizes.SwitchThumb)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                    }
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Shared visuals: splash and server cards
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun FetchingSpinnerRing(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val transition = rememberInfiniteTransition(label = "nothing_glyph_loader")
    val rot by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glyph_loader_rot"
    )

    val spinnerInkColor = HollowInk

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r = this.size.minDimension * 0.42f
            val dotCount = 12
            rotate(degrees = rot, pivot = Offset(cx, cy)) {
                for (i in 0 until dotCount) {
                    val angle = (2.0 * PI * i) / dotCount
                    val dx = cx + r * cos(angle).toFloat()
                    val dy = cy + r * sin(angle).toFloat()
                    val isLead = i == 0
                    val trailFade = ((dotCount - i).toFloat() / dotCount.toFloat()).coerceIn(0.22f, 1f)
                    drawCircle(
                        color = if (isLead) HollowOrange else spinnerInkColor.copy(alpha = trailFade * 0.78f),
                        radius = if (isLead) 3.4.dp.toPx() else 2.0.dp.toPx(),
                        center = Offset(dx, dy)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(size * 0.34f)
                .clip(CircleShape)
                .border(HollowStroke.Emphasis, HollowCreamDivider, CircleShape)
                .background(NothingCanvas),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(HollowSizes.Dot)
                    .clip(CircleShape)
                    .background(HollowOrange)
            )
        }
    }
}

private fun DrawScope.hStripes(vararg colors: Long) {
    val sh = size.height / colors.size
    colors.forEachIndexed { i, c -> drawRect(Color(c), Offset(0f, i * sh), Size(size.width, sh + 0.5f)) }
}

private fun DrawScope.vStripes(vararg colors: Long) {
    val sw = size.width / colors.size
    colors.forEachIndexed { i, c -> drawRect(Color(c), Offset(i * sw, 0f), Size(sw + 0.5f, size.height)) }
}

private fun DrawScope.nordicCross(bg: Long, cross: Long, inner: Long? = null) {
    val w = size.width
    val h = size.height
    drawRect(Color(bg), size = size)
    if (inner != null) {
        drawRect(Color(cross), Offset(0f, h * 0.38f), Size(w, h * 0.24f))
        drawRect(Color(cross), Offset(w * 0.28f, 0f), Size(w * 0.24f, h))
        drawRect(Color(inner), Offset(0f, h * 0.44f), Size(w, h * 0.12f))
        drawRect(Color(inner), Offset(w * 0.34f, 0f), Size(w * 0.12f, h))
    } else {
        drawRect(Color(cross), Offset(0f, h * 0.40f), Size(w, h * 0.20f))
        drawRect(Color(cross), Offset(w * 0.30f, 0f), Size(w * 0.20f, h))
    }
}

/**
 * Straight (rectangular, flat) country flag from bundled WebP images (res/drawable-nodpi/flag_xx.webp).
 * Falls back to a simple drawn flag / globe when no image exists for the code.
 */
@Composable
fun CountryFlag(
    countryCode: String?,
    modifier: Modifier = Modifier,
    width: Dp = 38.dp,
    height: Dp = 26.dp
) {
    val res = countryCode?.trim()?.uppercase()?.let { FLAG_RES[it] }
    if (res != null) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(res),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = modifier
                .size(width, height)
                .clip(RoundedCornerShape(HollowRadius.Bar))
        )
    } else {
        CanvasCountryFlag(countryCode = countryCode, modifier = modifier, width = width, height = height)
    }
}

/**
 * Fallback flag drawn with vector shapes (used only if no bundled image exists).
 */
@Composable
private fun CanvasCountryFlag(
    countryCode: String?,
    modifier: Modifier = Modifier,
    width: Dp = 24.dp,
    height: Dp = 16.dp
) {
    val scheme = MaterialTheme.colorScheme
    val code = countryCode?.trim()?.uppercase().orEmpty()
    Canvas(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(HollowRadius.Bar))
    ) {
        val w = size.width
        val h = size.height
        when (code) {
            "DE" -> hStripes(0xFF1A1A1A, 0xFFD92B2B, 0xFFF4C430)
            "NL" -> hStripes(0xFFAE1C28, 0xFFFFFFFF, 0xFF21468B)
            "RU" -> hStripes(0xFFFFFFFF, 0xFF0039A6, 0xFFD52B1E)
            "AT" -> hStripes(0xFFED2939, 0xFFFFFFFF, 0xFFED2939)
            "HU" -> hStripes(0xFFCE2939, 0xFFFFFFFF, 0xFF477050)
            "BG" -> hStripes(0xFFFFFFFF, 0xFF00966E, 0xFFD62612)
            "LT" -> hStripes(0xFFFDB913, 0xFF006A44, 0xFFC1272D)
            "EE" -> hStripes(0xFF0072CE, 0xFF000000, 0xFFFFFFFF)
            "LV" -> hStripes(0xFF9E3039, 0xFFFFFFFF, 0xFF9E3039)
            "LU" -> hStripes(0xFFED2939, 0xFFFFFFFF, 0xFF00A1DE)
            "YE" -> hStripes(0xFFCE1126, 0xFFFFFFFF, 0xFF000000)
            "IQ", "EG", "SY" -> hStripes(0xFFCE1126, 0xFFFFFFFF, 0xFF000000)
            "SL" -> hStripes(0xFF1EB53A, 0xFFFFFFFF, 0xFF0072C6)
            "PL", "ID", "MC" -> hStripes(if (code == "PL") 0xFFFFFFFF else 0xFFCE1126, if (code == "PL") 0xFFDC143C else 0xFFFFFFFF)
            "UA" -> hStripes(0xFF0057B7, 0xFFFFD700)
            "FR" -> vStripes(0xFF0055A4, 0xFFFFFFFF, 0xFFEF4135)
            "IT" -> vStripes(0xFF009246, 0xFFFFFFFF, 0xFFCE2B37)
            "IE" -> vStripes(0xFF169B62, 0xFFFFFFFF, 0xFFFF883E)
            "BE" -> vStripes(0xFF000000, 0xFFFAE042, 0xFFED2939)
            "RO" -> vStripes(0xFF002B7F, 0xFFFCD116, 0xFFCE1126)
            "MD" -> vStripes(0xFF0046AE, 0xFFFFD200, 0xFFCC092F)
            "NG" -> vStripes(0xFF008751, 0xFFFFFFFF, 0xFF008751)
            "ES" -> {
                drawRect(Color(0xFFAA151B), size = size)
                drawRect(Color(0xFFF1BF00), Offset(0f, h * 0.25f), Size(w, h * 0.5f))
            }
            "SG" -> {
                hStripes(0xFFED2939, 0xFFFFFFFF)
                drawCircle(Color.White, w * 0.17f, Offset(w * 0.24f, h * 0.26f))
                drawCircle(Color(0xFFED2939), w * 0.14f, Offset(w * 0.28f, h * 0.26f))
            }
            "US" -> {
                val n = 13
                val sh = h / n
                for (i in 0 until n) {
                    drawRect(
                        if (i % 2 == 0) Color(0xFFB22234) else Color.White,
                        Offset(0f, i * sh), Size(w, sh + 0.5f)
                    )
                }
                drawRect(Color(0xFF3C3B6E), size = Size(w * 0.42f, sh * 7))
            }
            "GB", "UK" -> {
                drawRect(Color(0xFF012169), size = size)
                drawLine(Color.White, Offset(0f, 0f), Offset(w, h), strokeWidth = h * 0.24f)
                drawLine(Color.White, Offset(w, 0f), Offset(0f, h), strokeWidth = h * 0.24f)
                drawLine(Color(0xFFC8102E), Offset(0f, 0f), Offset(w, h), strokeWidth = h * 0.10f)
                drawLine(Color(0xFFC8102E), Offset(w, 0f), Offset(0f, h), strokeWidth = h * 0.10f)
                drawRect(Color.White, Offset(0f, h * 0.33f), Size(w, h * 0.34f))
                drawRect(Color.White, Offset(w * 0.4f, 0f), Size(w * 0.2f, h))
                drawRect(Color(0xFFC8102E), Offset(0f, h * 0.42f), Size(w, h * 0.16f))
                drawRect(Color(0xFFC8102E), Offset(w * 0.44f, 0f), Size(w * 0.12f, h))
            }
            "CA" -> {
                drawRect(Color(0xFFD52B1E), size = size)
                drawRect(Color.White, Offset(w * 0.25f, 0f), Size(w * 0.5f, h))
                drawCircle(Color(0xFFD52B1E), w * 0.11f, Offset(w * 0.5f, h * 0.5f))
            }
            "JP" -> {
                drawRect(Color.White, size = size)
                drawCircle(Color(0xFFBC002D), h * 0.3f, Offset(w / 2f, h / 2f))
            }
            "KR" -> {
                drawRect(Color.White, size = size)
                drawArc(Color(0xFFCD2E3A), 180f, 180f, true, Offset(w * 0.34f, h * 0.24f), Size(w * 0.32f, h * 0.52f))
                drawArc(Color(0xFF0047A0), 0f, 180f, true, Offset(w * 0.34f, h * 0.24f), Size(w * 0.32f, h * 0.52f))
            }
            "IN" -> {
                hStripes(0xFFFF9933, 0xFFFFFFFF, 0xFF138808)
                drawCircle(Color(0xFF000080), h * 0.12f, Offset(w / 2f, h / 2f), style = Stroke(width = 1.2f))
            }
            "CN" -> {
                drawRect(Color(0xFFDE2910), size = size)
                drawCircle(Color(0xFFFFDE00), h * 0.17f, Offset(w * 0.22f, h * 0.30f))
                drawCircle(Color(0xFFFFDE00), h * 0.045f, Offset(w * 0.42f, h * 0.14f))
                drawCircle(Color(0xFFFFDE00), h * 0.045f, Offset(w * 0.48f, h * 0.30f))
                drawCircle(Color(0xFFFFDE00), h * 0.045f, Offset(w * 0.42f, h * 0.46f))
            }
            "TR" -> {
                drawRect(Color(0xFFE30A17), size = size)
                drawCircle(Color.White, h * 0.26f, Offset(w * 0.40f, h * 0.5f))
                drawCircle(Color(0xFFE30A17), h * 0.21f, Offset(w * 0.45f, h * 0.5f))
                drawCircle(Color.White, h * 0.08f, Offset(w * 0.58f, h * 0.5f))
            }
            "HK" -> {
                drawRect(Color(0xFFDE2910), size = size)
                drawCircle(Color.White, h * 0.22f, Offset(w / 2f, h / 2f))
            }
            "TW" -> {
                drawRect(Color(0xFFFE0000), size = size)
                drawRect(Color(0xFF000095), size = Size(w * 0.5f, h * 0.5f))
                drawCircle(Color.White, h * 0.14f, Offset(w * 0.25f, h * 0.25f))
            }
            "BR" -> {
                drawRect(Color(0xFF009C3B), size = size)
                val d = Path().apply {
                    moveTo(w * 0.5f, h * 0.1f); lineTo(w * 0.92f, h * 0.5f)
                    lineTo(w * 0.5f, h * 0.9f); lineTo(w * 0.08f, h * 0.5f); close()
                }
                drawPath(d, Color(0xFFFFDF00))
                drawCircle(Color(0xFF002776), h * 0.22f, Offset(w / 2f, h / 2f))
            }
            "CH" -> {
                drawRect(Color(0xFFDA291C), size = size)
                drawRect(Color.White, Offset(w * 0.44f, h * 0.2f), Size(w * 0.12f, h * 0.6f))
                drawRect(Color.White, Offset(w * 0.3f, h * 0.44f), Size(w * 0.4f, h * 0.12f))
            }
            "SE" -> nordicCross(0xFF006AA7, 0xFFFECC02)
            "FI" -> nordicCross(0xFFFFFFFF, 0xFF003580)
            "DK" -> nordicCross(0xFFC8102E, 0xFFFFFFFF)
            "NO" -> nordicCross(0xFFBA0C2F, 0xFFFFFFFF, 0xFF00205B)
            "IS" -> nordicCross(0xFF02529C, 0xFFFFFFFF, 0xFFDC1E35)
            "CZ" -> {
                hStripes(0xFFFFFFFF, 0xFFD7141A)
                val t = Path().apply { moveTo(0f, 0f); lineTo(w * 0.5f, h / 2f); lineTo(0f, h); close() }
                drawPath(t, Color(0xFF11457E))
            }
            "SK" -> {
                hStripes(0xFFFFFFFF, 0xFF0B4EA2, 0xFFEE1C25)
            }
            "RS" -> hStripes(0xFFC6363C, 0xFF0C4076, 0xFFFFFFFF)
            "HR" -> hStripes(0xFFFF0000, 0xFFFFFFFF, 0xFF171796)
            "SI" -> hStripes(0xFFFFFFFF, 0xFF005DA4, 0xFFED1C24)
            "GR" -> {
                for (i in 0 until 9) {
                    drawRect(
                        if (i % 2 == 0) Color(0xFF0D5EAF) else Color.White,
                        Offset(0f, i * h / 9f), Size(w, h / 9f + 0.5f)
                    )
                }
                drawRect(Color(0xFF0D5EAF), size = Size(w * 0.38f, h * 5f / 9f))
                drawRect(Color.White, Offset(w * 0.16f, 0f), Size(w * 0.06f, h * 5f / 9f))
                drawRect(Color.White, Offset(0f, h * 0.22f), Size(w * 0.38f, h * 0.11f))
            }
            "AU", "NZ" -> {
                drawRect(Color(0xFF00247D), size = size)
                drawRect(Color.White, Offset(0f, h * 0.14f), Size(w * 0.5f, h * 0.12f))
                drawRect(Color.White, Offset(w * 0.19f, 0f), Size(w * 0.12f, h * 0.4f))
                drawRect(Color(0xFFCC142B), Offset(0f, h * 0.17f), Size(w * 0.5f, h * 0.06f))
                drawRect(Color(0xFFCC142B), Offset(w * 0.22f, 0f), Size(w * 0.06f, h * 0.4f))
                drawCircle(Color.White, h * 0.05f, Offset(w * 0.75f, h * 0.30f))
                drawCircle(Color.White, h * 0.05f, Offset(w * 0.68f, h * 0.55f))
                drawCircle(Color.White, h * 0.05f, Offset(w * 0.82f, h * 0.60f))
                drawCircle(Color.White, h * 0.05f, Offset(w * 0.75f, h * 0.82f))
            }
            "MX" -> {
                vStripes(0xFF006847, 0xFFFFFFFF, 0xFFCE1126)
                drawCircle(Color(0xFF8C6B3F), h * 0.12f, Offset(w / 2f, h / 2f))
            }
            "AR" -> {
                hStripes(0xFF74ACDF, 0xFFFFFFFF, 0xFF74ACDF)
                drawCircle(Color(0xFFF6B40E), h * 0.09f, Offset(w / 2f, h / 2f))
            }
            "CL" -> {
                hStripes(0xFFFFFFFF, 0xFFD52B1E)
                drawRect(Color(0xFF0039A6), size = Size(w * 0.33f, h * 0.5f))
            }
            "CO" -> {
                drawRect(Color(0xFFFCD116), Offset(0f, 0f), Size(w, h * 0.5f))
                drawRect(Color(0xFF003893), Offset(0f, h * 0.5f), Size(w, h * 0.25f))
                drawRect(Color(0xFFCE1126), Offset(0f, h * 0.75f), Size(w, h * 0.25f))
            }
            "PT" -> {
                drawRect(Color(0xFF006600), Offset(0f, 0f), Size(w * 0.4f, h))
                drawRect(Color(0xFFFF0000), Offset(w * 0.4f, 0f), Size(w * 0.6f, h))
                drawCircle(Color(0xFFFFE000), h * 0.2f, Offset(w * 0.4f, h / 2f))
            }
            "IL" -> {
                drawRect(Color.White, size = size)
                drawRect(Color(0xFF0038B8), Offset(0f, h * 0.1f), Size(w, h * 0.12f))
                drawRect(Color(0xFF0038B8), Offset(0f, h * 0.78f), Size(w, h * 0.12f))
                drawCircle(Color(0xFF0038B8), h * 0.18f, Offset(w / 2f, h / 2f), style = Stroke(width = 1.4f))
            }
            "AE" -> {
                drawRect(Color(0xFF00732F), Offset(0f, 0f), Size(w, h / 3f))
                drawRect(Color.White, Offset(0f, h / 3f), Size(w, h / 3f))
                drawRect(Color(0xFF000000), Offset(0f, 2f * h / 3f), Size(w, h / 3f + 0.5f))
                drawRect(Color(0xFFFF0000), size = Size(w * 0.25f, h))
            }
            "SA" -> {
                drawRect(Color(0xFF006C35), size = size)
                drawRect(Color.White, Offset(w * 0.2f, h * 0.62f), Size(w * 0.6f, h * 0.06f))
            }
            "ZA" -> {
                drawRect(Color(0xFFDE3831), Offset(0f, 0f), Size(w, h / 2f))
                drawRect(Color(0xFF002395), Offset(0f, h / 2f), Size(w, h / 2f))
                val t = Path().apply { moveTo(0f, 0f); lineTo(w * 0.45f, h / 2f); lineTo(0f, h); close() }
                drawPath(t, Color(0xFF007A4D))
            }
            "TH" -> {
                hStripes(0xFFA51931, 0xFFF4F5F8, 0xFF2D2A4A, 0xFFF4F5F8, 0xFFA51931)
            }
            "VN" -> {
                drawRect(Color(0xFFDA251D), size = size)
                drawCircle(Color(0xFFFFFF00), h * 0.2f, Offset(w / 2f, h / 2f))
            }
            "MY" -> {
                for (i in 0 until 7) {
                    drawRect(
                        if (i % 2 == 0) Color(0xFFCC0001) else Color.White,
                        Offset(0f, i * h / 7f), Size(w, h / 7f + 0.5f)
                    )
                }
                drawRect(Color(0xFF010066), size = Size(w * 0.5f, h * 4f / 7f))
            }
            "PH" -> {
                drawRect(Color(0xFF0038A8), Offset(0f, 0f), Size(w, h / 2f))
                drawRect(Color(0xFFCE1126), Offset(0f, h / 2f), Size(w, h / 2f))
                val t = Path().apply { moveTo(0f, 0f); lineTo(w * 0.5f, h / 2f); lineTo(0f, h); close() }
                drawPath(t, Color.White)
            }
            "PK" -> {
                drawRect(Color(0xFF01411C), size = size)
                drawRect(Color.White, size = Size(w * 0.25f, h))
            }
            "KZ" -> {
                drawRect(Color(0xFF00AFCA), size = size)
                drawCircle(Color(0xFFFEC50C), h * 0.2f, Offset(w / 2f, h / 2f))
            }
            "IR" -> hStripes(0xFF239F40, 0xFFFFFFFF, 0xFFDA0000)
            else -> {
                drawRect(scheme.surfaceContainerHigh, size = size)
                val c = scheme.onSurfaceVariant.copy(alpha = 0.55f)
                val st = Stroke(width = 1.2f)
                drawCircle(c, h * 0.32f, Offset(w / 2f, h / 2f), style = st)
                drawLine(c, Offset(w / 2f - h * 0.32f, h / 2f), Offset(w / 2f + h * 0.32f, h / 2f), 1.2f)
                drawOval(c, Offset(w / 2f - h * 0.14f, h / 2f - h * 0.32f), Size(h * 0.28f, h * 0.64f), style = st)
            }
        }
        drawRect(
            color = Color.Black.copy(alpha = 0.18f),
            style = Stroke(width = 1f)
        )
    }
}
