package com.hollow.vpn.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut


import androidx.compose.animation.slideInHorizontally


import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Radius: controls 6 · cards / dialogs 8 · pill. Segmented-bar cells are square (0). */
object HollowRadius {
    val Bar = 0.dp
    val Xs = 6.dp
    val Sm = 6.dp
    val Md = 6.dp
    val Lg = 8.dp
    val Xl = 8.dp
    val Pill = 999.dp
}

object HollowShapes {
    val Control: Shape = RoundedCornerShape(HollowRadius.Md)
    val Card: Shape = RoundedCornerShape(HollowRadius.Lg)
    val Dialog: Shape = RoundedCornerShape(HollowRadius.Xl)
    val Sheet: Shape = RoundedCornerShape(topStart = HollowRadius.Xl, topEnd = HollowRadius.Xl)
    val Pill: Shape = RoundedCornerShape(HollowRadius.Pill)
}

/** Hairlines are always 1dp. */
object HollowStroke {
    val Hairline = 1.dp
    val Emphasis = 1.dp
    val Heavy = 2.dp
}

object HollowType {
    val Micro = 11.sp
    val Caption = 11.sp
    val Small = 14.sp
    val Body = 16.sp
    val BodyStrong = 16.sp
    val Title = 20.sp
    val Heading = 24.sp
    val Headline = 32.sp
    val Display = 40.sp
}

object HollowLineHeight {
    val Micro = 16.sp
    val Caption = 16.sp
    val Small = 20.sp
    val Body = 24.sp
    val BodyStrong = 24.sp
    val Title = 24.sp
    val Heading = 28.sp
    val Headline = 38.sp
    val Display = 44.sp
}

object HollowTracking {
    val Micro = 0.02.em
    val Caption = 0.02.em
    val Small = 0.sp
    val Body = 0.sp
    val BodyStrong = 0.sp
    val Title = 0.sp
    val Heading = 0.sp
    val Headline = 0.sp
    val Display = 0.sp
}

/** ~200ms controls / ~400ms transitions, ease-in-out, no spring. */
object HollowMotion {
    const val Quick = 150
    const val Fast = 200
    const val Standard = 200
    const val Emphasized = 400

    const val PressControl = 1f
    const val PressCard = 1f
}

object HollowTransitions {
    private val ease = NothingMotion.FluidEase

    // Opacity-first; a small horizontal drift only where direction matters. No scale, no rebound.
    val Push: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { (it * 0.06f).toInt() } +
            fadeIn(tween(HollowMotion.Emphasized, easing = ease)))
            .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))

    val Pop: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { -(it * 0.06f).toInt() } +
            fadeIn(tween(HollowMotion.Emphasized, easing = ease)))
            .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))

    val Modal: ContentTransform
        get() = fadeIn(tween(HollowMotion.Emphasized, easing = ease))
            .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))

    val Fade: ContentTransform
        get() = fadeIn(tween(HollowMotion.Standard, easing = ease))
            .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))
}

@Immutable
data class HollowPhaseColors(
    val canvas: Color,
    val surface: Color,
    val border: Color,
    val ink: Color,
    val muted: Color,
    val accent: Color,
    val onAccent: Color
)

/**
 * Phase → unified state machine.
 *  0 idle        grey dot, monochrome
 *  1 connecting  warning value colour (data state)
 *  2 connected   success value colour (data state)
 * The signal-red accent is NOT a phase colour; it is reserved for needs-input / errors.
 */
@Composable
@ReadOnlyComposable
fun hollowPhaseColors(phase: Int): HollowPhaseColors {
    val p = LocalHollowPalette.current
    return HollowPhaseColors(
        canvas = p.canvas, surface = p.surface, border = p.border, ink = p.ink, muted = p.inkSecondary,
        accent = when (phase) { 2 -> p.matrixGreen; 1 -> p.warning; else -> p.inkSecondary },
        onAccent = p.onInvertedSurface
    )
}

object HollowBar {
    val Fill: Color
        @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillNavFill
    val Border: Color
        @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillNavBorder
    val Height = 60.dp
    val Shape: Shape = RoundedCornerShape(HollowRadius.Pill)
}

/** Upload series colour on the usage chart — a grey step, never a hue. */
val HollowUploadAccent: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.inkSecondary

object HollowDialog {
    val Shape: Shape = HollowShapes.Dialog
    val containerColor: Color
        @Composable @ReadOnlyComposable get() = NothingSurface
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HollowModalSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = NothingSurface,
        shape = HollowShapes.Sheet,
        dragHandle = { BottomSheetDefaults.DragHandle(color = HollowLine2) },
        content = content
    )
}
