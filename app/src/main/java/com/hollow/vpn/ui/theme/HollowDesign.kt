package com.hollow.vpn.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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

object HollowRadius {
    val Bar = 2.dp
    val Xs = 8.dp
    val Sm = 12.dp
    val Md = 16.dp
    val Lg = 20.dp
    val Xl = 24.dp
    val Pill = 999.dp
}

object HollowShapes {
    val Control: Shape = RoundedCornerShape(HollowRadius.Md)
    val Dialog: Shape = RoundedCornerShape(HollowRadius.Xl)
    val Sheet: Shape = RoundedCornerShape(topStart = HollowRadius.Xl, topEnd = HollowRadius.Xl)
    val Pill: Shape = RoundedCornerShape(HollowRadius.Pill)
}

object HollowStroke {
    val Hairline = 1.dp
    val Emphasis = 1.5.dp
    val Heavy = 2.dp
}

object HollowType {
    val Micro = 12.sp
    val Caption = 12.sp
    val Small = 14.sp
    val Body = 16.sp
    val BodyStrong = 16.sp
    val Title = 20.sp
    val Heading = 20.sp
    val Headline = 28.sp
    val Display = 28.sp
}

object HollowLineHeight {
    val Micro = 16.sp
    val Caption = 16.sp
    val Small = 20.sp
    val Body = 24.sp
    val BodyStrong = 24.sp
    val Title = 24.sp
    val Heading = 24.sp
    val Headline = 34.sp
    val Display = 34.sp
}

object HollowTracking {
    val Micro = 0.03.em
    val Caption = 0.03.em
    val Small = 0.02.em
    val Body = 0.03.em
    val BodyStrong = 0.03.em
    val Title = 0.01.em
    val Heading = 0.01.em
    val Headline = 0.45.sp
    val Display = 0.45.sp
}

object HollowMotion {
    const val Quick = 120
    const val Fast = 180
    const val Standard = 240
    const val Emphasized = 320

    const val PressControl = 0.94f
    const val PressCard = 0.97f
}

object HollowTransitions {
    private val ease = NothingMotion.FluidEase

    val Push: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { (it * 0.12f).toInt() } +
            fadeIn(tween(HollowMotion.Standard, easing = ease)))
            .togetherWith(
                slideOutHorizontally(tween(HollowMotion.Standard, easing = ease)) { -(it * 0.08f).toInt() } +
                    fadeOut(tween(HollowMotion.Fast, easing = ease))
            )

    val Pop: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { -(it * 0.08f).toInt() } +
            fadeIn(tween(HollowMotion.Standard, easing = ease)))
            .togetherWith(
                slideOutHorizontally(tween(HollowMotion.Standard, easing = ease)) { (it * 0.12f).toInt() } +
                    fadeOut(tween(HollowMotion.Fast, easing = ease))
            )

    val Modal: ContentTransform
        get() = (fadeIn(tween(HollowMotion.Standard, easing = ease)) +
            slideInVertically(tween(HollowMotion.Emphasized, easing = ease)) { it / 16 } +
            scaleIn(tween(HollowMotion.Emphasized, easing = ease), initialScale = 0.97f))
            .togetherWith(
                fadeOut(tween(HollowMotion.Fast, easing = ease)) +
                    scaleOut(tween(HollowMotion.Fast, easing = ease), targetScale = 0.98f)
            )

    val Fade: ContentTransform
        get() = (fadeIn(tween(HollowMotion.Standard, easing = ease)) +
            scaleIn(tween(HollowMotion.Standard, easing = ease), initialScale = 0.98f))
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

@Composable
@ReadOnlyComposable
fun hollowPhaseColors(phase: Int): HollowPhaseColors = when (phase) {
    2 -> HollowPhaseColors(
        canvas = HollowForest, surface = HollowForestCard, border = HollowForestDivider,
        ink = HollowDarkInk, muted = HollowForestMuted, accent = HollowLime, onAccent = NothingBlack
    )
    1 -> HollowPhaseColors(
        canvas = HollowObsidian, surface = HollowObsidianSurface, border = HollowObsidianDivider,
        ink = HollowDarkInk, muted = HollowObsidianMuted, accent = HollowAmber, onAccent = NothingBlack
    )
    else -> HollowPhaseColors(
        canvas = HollowCream, surface = HollowCreamSurface, border = HollowCreamDivider,
        ink = HollowInk, muted = HollowInkSecondary, accent = HollowOrange, onAccent = Color.White
    )
}

object HollowBar {
    val Fill = androidx.compose.ui.graphics.Color(0xFF0C0B0E)
    val Border = androidx.compose.ui.graphics.Color(0xFF1C1C20)
    val Height = 60.dp
    val Shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
}

val HollowUploadAccent = Color(0xFFF28B9B)

object HollowDialog {
    val Shape: Shape = HollowShapes.Dialog
    val containerColor: Color
        @Composable @ReadOnlyComposable get() = NothingSurfaceElevated
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
        containerColor = NothingSurfaceElevated,
        shape = HollowShapes.Sheet,
        dragHandle = { BottomSheetDefaults.DragHandle(color = NothingDotInactive) },
        content = content
    )
}
