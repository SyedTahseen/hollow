package com.hollow.vpn.ui.components

import com.hollow.vpn.ui.theme.HollowSizes
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
import androidx.compose.foundation.clickable
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

/**
 * Subtle Nothing OS OLED dot-matrix background texture.
 */
@Composable
fun NothingDotMatrixBackdrop(
    modifier: Modifier = Modifier,
    dotColor: Color = NothingBackdropDot,
    spacing: Dp = 18.dp,
    dotRadius: Dp = 1.dp
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val stepPx = spacing.toPx().coerceAtLeast(8f)
        val rPx = dotRadius.toPx().coerceAtLeast(1f)
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

/**
 * Authentic Nothing OS dotted hairline divider (`· · · · · · · · ·`).
 */
@Composable
fun NothingDottedDivider(
    modifier: Modifier = Modifier,
    color: Color = NothingDottedLine,
    dotWidth: Dp = 1.5.dp,
    gapWidth: Dp = 3.5.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val cy = size.height / 2f
        drawLine(
            color = color,
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dotWidth.toPx(), gapWidth.toPx()),
                0f
            )
        )
    }
}

/**
 * Nothing OS uppercase monospace pill filter/tab row (solid white active pill + hairline dark pills)
 * with smooth color morphing and tactile spring press-scale.
 */
@Composable
fun NothingPillTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabTestTags: List<String> = emptyList(),
    fontSize: androidx.compose.ui.unit.TextUnit = HollowType.Caption
) {
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
                    .clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        role = Role.Tab
                    ) { onSelectTab(index) },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = containerColor,
                    border = BorderStroke(HollowStroke.Hairline, borderColor)
                ) {
                    Text(
                        text = title.uppercase(),
                        fontFamily = HollowSansFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = fontSize,
                        lineHeight = HollowLineHeight.Caption,
                        letterSpacing = HollowTracking.Caption,
                        color = contentColor,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Sm)
                    )
                }
            }
        }
    }
}

/** Small Glyph status dot that pulses crisply. */
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

/** Nothing OS segmented dot-matrix progress wave bar. */
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
