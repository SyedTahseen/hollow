package com.hollow.vpn.ui.components

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowSpace
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.hollow.vpn.ui.theme.HollowText
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.NothingBackdropDot
import com.hollow.vpn.ui.theme.NothingDottedLine
import com.hollow.vpn.ui.theme.NothingMotion
import com.hollow.vpn.ui.theme.NothingPillActiveBg
import com.hollow.vpn.ui.theme.NothingPillActiveIcon
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.nothingPressScale

@Composable
fun NothingDotMatrixBackdrop(
    modifier: Modifier = Modifier,
    dotColor: Color = NothingBackdropDot,
    spacing: Dp = 64.dp,
    dotRadius: Dp = 0.65.dp
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val stepPx = spacing.toPx().coerceAtLeast(8f)
        val rPx = dotRadius.toPx().coerceAtLeast(0.65f)
        var y = stepPx * 0.5f
        while (y < size.height) {
            var x = stepPx * 0.5f
            while (x < size.width) {
                drawCircle(
                    color = dotColor,
                    radius = rPx,
                    center = Offset(x, y)
                )
                x += stepPx
            }
            y += stepPx
        }
    }
}

/** List divider: a solid 1dp hairline in the `line` token (name kept for existing call sites). */
@Composable
fun NothingDottedDivider(
    modifier: Modifier = Modifier,
    color: Color = com.hollow.vpn.ui.theme.NothingBorder
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NothingPillTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabTestTags: List<String> = emptyList(),
    onLongPressTab: (Int) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            val tag = tabTestTags.getOrNull(index)
            val interactionSource = remember { MutableInteractionSource() }
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) NothingPillActiveBg else Color.Transparent,
                animationSpec = tween(durationMillis = HollowMotion.Fast, easing = NothingMotion.FluidEase),
                label = "pill_tab_bg_$index"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) NothingPillActiveBg else NothingTopBtnBorder,
                animationSpec = tween(durationMillis = HollowMotion.Fast, easing = NothingMotion.FluidEase),
                label = "pill_tab_border_$index"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) NothingPillActiveIcon else HollowInk,
                animationSpec = tween(durationMillis = HollowMotion.Fast, easing = NothingMotion.FluidEase),
                label = "pill_tab_text_$index"
            )
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .then(if (tag != null) Modifier.testTag(tag) else Modifier)
                    .nothingPressScale(interactionSource, pressedScale = HollowMotion.PressControl)
                    .clip(CircleShape)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        role = Role.Tab,
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPressTab(index)
                        },
                        onClick = { onSelectTab(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = containerColor,
                    border = BorderStroke(HollowStroke.Hairline, borderColor)
                ) {
                    Text(
                        text = title,
                        style = HollowText.Caption,
                        color = contentColor,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm)
                    )
                }
            }
        }
    }
}

@Composable
fun HollowBlinkingDot(color: Color, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "blink")
    val a by t.animateFloat(
        initialValue = 1f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(650, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink_alpha"
    )
    Box(
        modifier = modifier
            .size(HollowSizes.Dot)
            .alpha(a)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun HollowIndeterminateBar(color: Color, track: Color, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "bar_fill")
    val phase by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "bar_fraction"
    )
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
    ) {
        val count = 20
        val dotDiameter = 6.dp.toPx().coerceAtMost(size.height)
        val dotRadius = dotDiameter / 2f
        val usableWidth = (size.width - dotDiameter).coerceAtLeast(1f)
        val cy = size.height / 2f

        for (i in 0 until count) {
            val norm = i.toFloat() / (count - 1).toFloat()
            val cx = dotRadius + norm * usableWidth
            val dist = ((phase - norm) + 1f) % 1f
            val glow = if (dist < 0.34f) 1f - (dist / 0.34f) else 0f
            drawCircle(
                color = if (glow > 0.05f) color.copy(alpha = 0.25f + 0.75f * glow) else track,
                radius = dotRadius * (0.82f + 0.24f * glow),
                center = Offset(cx, cy)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared Nothing controls
// ─────────────────────────────────────────────────────────────────────────────

/** Primary button: display fill + bg text (black/white inversion, never red). 6dp radius, mono label. */
@Composable
fun HollowPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(HollowRadius.Md),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = com.hollow.vpn.ui.theme.NothingInvertedSurface,
            contentColor = com.hollow.vpn.ui.theme.NothingOnInvertedSurface,
            disabledContainerColor = com.hollow.vpn.ui.theme.NothingInvertedSurface.copy(alpha = 0.3f),
            disabledContentColor = com.hollow.vpn.ui.theme.NothingOnInvertedSurface
        ),
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm),
        content = content
    )
}

/** Outline button: transparent, 1dp line-2 border, primary text. */
@Composable
fun HollowOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(HollowRadius.Md),
        border = BorderStroke(HollowStroke.Hairline, com.hollow.vpn.ui.theme.HollowLine2),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = HollowInk,
            disabledContentColor = HollowInk.copy(alpha = 0.3f)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm),
        content = content
    )
}

/** Input: 6dp border, focus = primary border (no glow, no accent), error = error border. */
@Composable
fun hollowFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HollowInk,
    unfocusedBorderColor = com.hollow.vpn.ui.theme.HollowLine2,
    errorBorderColor = com.hollow.vpn.ui.theme.HollowError,
    cursorColor = com.hollow.vpn.ui.theme.HollowDisplayInk,
    errorCursorColor = com.hollow.vpn.ui.theme.HollowError,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedTextColor = HollowInk,
    unfocusedTextColor = HollowInk,
    errorTextColor = HollowInk,
    focusedPlaceholderColor = com.hollow.vpn.ui.theme.HollowInkMuted,
    unfocusedPlaceholderColor = com.hollow.vpn.ui.theme.HollowInkMuted,
    focusedLabelColor = com.hollow.vpn.ui.theme.HollowInkSecondary,
    unfocusedLabelColor = com.hollow.vpn.ui.theme.HollowInkSecondary,
    errorLabelColor = com.hollow.vpn.ui.theme.HollowError,
    errorSupportingTextColor = com.hollow.vpn.ui.theme.HollowError
)

/** Inline status line — `[SAVED]` / `[ERROR: …]` instead of toasts. Error uses the error token on the value only. */
@Composable
fun HollowInlineStatus(text: String, isError: Boolean = false, modifier: Modifier = Modifier) {
    Text(
        text = "[" + text.uppercase() + "]",
        style = HollowText.Label,
        color = if (isError) com.hollow.vpn.ui.theme.HollowError else com.hollow.vpn.ui.theme.HollowInkSecondary,
        modifier = modifier
    )
}

/**
 * Segmented bar (signature): discrete square cells, 2dp gaps, no radius.
 * Fill = display (black/white inversion). Over-limit cells only use the signal accent.
 * Always pair it with a numeric readout.
 */
@Composable
fun NothingSegmentedBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    segments: Int = 20,
    overLimit: Boolean = false,
    cellHeight: Dp = 11.dp
) {
    val lit = if (overLimit) com.hollow.vpn.ui.theme.NothingRed else com.hollow.vpn.ui.theme.HollowDisplayInk
    val empty = com.hollow.vpn.ui.theme.NothingBorder
    val filled = (fraction.coerceIn(0f, 1f) * segments).let { kotlin.math.ceil(it.toDouble()).toInt() }
    Canvas(modifier = modifier.fillMaxWidth().height(cellHeight)) {
        val gap = 2.dp.toPx()
        val cellW = (size.width - gap * (segments - 1)) / segments
        for (i in 0 until segments) {
            drawRect(
                color = if (i < filled) lit else empty,
                topLeft = Offset(i * (cellW + gap), 0f),
                size = androidx.compose.ui.geometry.Size(cellW, size.height)
            )
        }
    }
}

/** Dashed 1dp outline (used by the empty state). */
fun Modifier.nothingDashedBorder(color: Color, radius: Dp = HollowRadius.Lg): Modifier = this.then(
    Modifier.drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
            )
        )
    }
)

// ─────────────────────────────────────────────────────────────────────────────
// Alerts, state dots, dot-matrix clock, pills  (mirrors .alert / .livedot / .clock / .pill)
// ─────────────────────────────────────────────────────────────────────────────

enum class HollowAlertKind { Info, Success, Warning, Error }

/** `.alert` — 3dp left border in the status colour + ~9% tint of the same colour. Title in display, text in secondary. */
@Composable
fun HollowAlert(
    kind: HollowAlertKind,
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    val tone = when (kind) {
        HollowAlertKind.Info -> com.hollow.vpn.ui.theme.HollowDisplayInk
        HollowAlertKind.Success -> com.hollow.vpn.ui.theme.HollowSuccess
        HollowAlertKind.Warning -> com.hollow.vpn.ui.theme.HollowWarning
        HollowAlertKind.Error -> com.hollow.vpn.ui.theme.HollowError
    }
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(HollowRadius.Sm)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(tone.copy(alpha = 0.09f))
            .drawBehind { drawRect(tone, size = androidx.compose.ui.geometry.Size(3.dp.toPx(), size.height)) }
            .padding(start = HollowSpace.Lg, end = HollowSpace.Md, top = HollowSpace.Md, bottom = HollowSpace.Md)
    ) {
        Box(Modifier.padding(top = 5.dp).size(HollowSizes.Dot).clip(CircleShape).background(tone))
        Spacer(Modifier.width(HollowSpace.Md))
        Column(Modifier.weight(1f)) {
            if (title != null) {
                Text(title, style = HollowText.BodyBold, color = com.hollow.vpn.ui.theme.HollowDisplayInk)
            }
            Text(text, style = HollowText.Small, color = com.hollow.vpn.ui.theme.HollowInkSecondary)
        }
    }
}

enum class HollowStateDot { Idle, Running, NeedsInput, Live, Done }

/**
 * Unified state machine dot:
 *  Idle = grey static · Running = display, hard-stepped blink · NeedsInput = signal red static
 *  Live = signal red, hard-stepped blink (1.5s, steps(1) like `.livedot`) · Done = grey solid.
 */
@Composable
fun HollowStateDotView(state: HollowStateDot, modifier: Modifier = Modifier, size: Dp = 12.dp) {
    var on by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    val blinking = state == HollowStateDot.Running || state == HollowStateDot.Live
    androidx.compose.runtime.LaunchedEffect(blinking) {
        on = true
        while (blinking) {
            kotlinx.coroutines.delay(750)
            on = !on
        }
    }
    val color = when (state) {
        HollowStateDot.Idle, HollowStateDot.Done -> com.hollow.vpn.ui.theme.HollowInkMuted
        HollowStateDot.Running -> com.hollow.vpn.ui.theme.HollowDisplayInk
        HollowStateDot.NeedsInput, HollowStateDot.Live -> com.hollow.vpn.ui.theme.NothingRed
    }
    Box(
        modifier = modifier
            .size(size)
            .alpha(if (blinking && !on) 0.3f else 1f)
            .clip(CircleShape)
            .background(color)
    )
}

/** `.pill` — a: display fill · i: outline · sig: signal red fill with white text (needs-decision only). */
@Composable
fun HollowPill(text: String, modifier: Modifier = Modifier, signal: Boolean = false, active: Boolean = false) {
    val bg = when {
        signal -> com.hollow.vpn.ui.theme.NothingRed
        active -> com.hollow.vpn.ui.theme.NothingInvertedSurface
        else -> Color.Transparent
    }
    val fg = when {
        signal -> com.hollow.vpn.ui.theme.NothingAccentInk
        active -> com.hollow.vpn.ui.theme.NothingOnInvertedSurface
        else -> com.hollow.vpn.ui.theme.HollowInkMuted
    }
    Text(
        text = text.uppercase(),
        style = HollowText.Label,
        color = fg,
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .then(if (bg == Color.Transparent) Modifier.border(HollowStroke.Hairline, com.hollow.vpn.ui.theme.HollowLine2, CircleShape) else Modifier)
            .padding(horizontal = HollowSpace.Sm, vertical = 2.dp)
    )
}

/** Dot-matrix punctuation `:` — two round dots (`.colon`), slightly smaller than the digit dots. */
@Composable
fun NothingDotColon(color: Color, modifier: Modifier = Modifier, dot: Dp = 3.dp) {
    Column(
        modifier = modifier.padding(horizontal = HollowSpace.Xs),
        verticalArrangement = Arrangement.spacedBy(dot * 1.6f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(dot).clip(CircleShape).background(color))
        Box(Modifier.size(dot).clip(CircleShape).background(color))
    }
}

/** `.clock` — Doto digits with round-dot colons. Pass "HH:MM:SS" (or any `:`-separated numerals). */
@Composable
fun NothingClock(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = HollowText.NumeralSm,
    color: Color = com.hollow.vpn.ui.theme.HollowDisplayInk
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        text.split(":").forEachIndexed { i, part ->
            if (i > 0) NothingDotColon(color = com.hollow.vpn.ui.theme.HollowInkSecondary)
            Text(part, style = style, color = color, maxLines = 1)
        }
    }
}


/**
 * Inline status banner — replaces toasts/snackbars (spec: "No toasts — use inline status").
 * Failure-sounding messages use the error tone; everything else is a neutral display-ink alert.
 */
@Composable
fun HollowStatusBanner(message: String, modifier: Modifier = Modifier) {
    val lower = message.lowercase()
    val isError = listOf("fail", "denied", "error", "couldn", "invalid", "unable", "cannot", "can't").any { it in lower }
    HollowAlert(
        kind = if (isError) HollowAlertKind.Error else HollowAlertKind.Info,
        text = message,
        modifier = modifier.padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm)
    )
}

/**
 * Segmented spinner — 12 round dots on a ring, a lit tail steps around it (hard steps, no easing).
 * Replaces Material's circular progress indicator (spec: `[LOADING…]` or a segmented spinner).
 */
@Composable
fun HollowSegmentedSpinner(
    modifier: Modifier = Modifier,
    size: Dp = HollowSizes.IconLg,
    color: Color = com.hollow.vpn.ui.theme.HollowDisplayInk,
    track: Color = com.hollow.vpn.ui.theme.NothingBorder
) {
    val t = rememberInfiniteTransition(label = "spinner")
    val phase by t.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            androidx.compose.animation.core.tween(1200, easing = LinearEasing), RepeatMode.Restart
        ),
        label = "spinner_phase"
    )
    Canvas(modifier = modifier.size(size)) {
        val n = 12
        val head = phase.toInt() % n
        val ringR = this.size.minDimension / 2f
        val dotR = ringR * 0.14f
        val orbit = ringR - dotR
        for (i in 0 until n) {
            val behind = (head - i + n) % n
            val lit = behind < 4
            val ang = Math.toRadians(i * 360.0 / n - 90.0)
            drawCircle(
                color = if (lit) color.copy(alpha = 1f - behind * 0.22f) else track,
                radius = dotR,
                center = Offset(
                    center.x + (orbit * kotlin.math.cos(ang)).toFloat(),
                    center.y + (orbit * kotlin.math.sin(ang)).toFloat()
                )
            )
        }
    }
}
