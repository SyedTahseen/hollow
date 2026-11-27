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

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  HOLLOW DESIGN SYSTEM
 *  Single source of truth for everything visual. Screens must use these tokens
 *  instead of raw dp / sp / Color / ms literals.
 *
 *    Space       → HollowSpace           (Dimens.kt, 4dp grid)
 *    Sizes       → HollowSizes           (Dimens.kt, control heights)
 *    Colour      → HollowPalette + HollowPhaseColors (Color.kt / below)
 *    Radius      → HollowRadius / HollowShapes
 *    Stroke      → HollowStroke
 *    Type        → HollowType            (+ Typography in Type.kt)
 *    Motion      → HollowMotion / HollowTransitions
 *    Containers  → HollowModalSheet, HollowDialog
 * ─────────────────────────────────────────────────────────────────────────────
 */

/** Corner-radius scale. Six steps only; pick the step that matches the element's role. */
object HollowRadius {
    /** Progress bars, thin indicators. */
    val Bar = 2.dp
    /** Small icon tiles, badges, inner list joints. */
    val Xs = 8.dp
    /** Icon boxes, chips, compact surfaces. */
    val Sm = 12.dp
    /** Controls: buttons, inputs, segmented selectors, list rows. */
    val Md = 16.dp
    /** Cards and content surfaces. */
    val Lg = 20.dp
    /** Dialogs, bottom sheets, hero surfaces. */
    val Xl = 24.dp
    /** Capsules: pills, slide-to-connect track, status chips. */
    val Pill = 999.dp
}

/** Ready-made shapes built from [HollowRadius]. */
object HollowShapes {
    val Control: Shape = RoundedCornerShape(HollowRadius.Md)
    val Dialog: Shape = RoundedCornerShape(HollowRadius.Xl)
    val Sheet: Shape = RoundedCornerShape(topStart = HollowRadius.Xl, topEnd = HollowRadius.Xl)
    val Pill: Shape = RoundedCornerShape(HollowRadius.Pill)
}

/** Border widths. Every surface is flat + hairline-bordered; no drop shadows. */
object HollowStroke {
    val Hairline = 1.dp
    val Emphasis = 1.5.dp
    val Heavy = 2.dp
}

/**
 * Type scale (sp), identical to Outrun's: 12 · 14 · 16 · 20 · 28.
 * Legacy names are kept so call sites compile; several names intentionally share a size.
 */
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

/** Line heights paired with [HollowType] (Outrun: 12/16 · 14/20 · 16/24 · 20/24 · 28/34). */
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

/** Letter spacing paired with [HollowType] (Outrun: em-based tracking). */
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

/** Motion tokens: durations (ms) and press-feedback scales. */
object HollowMotion {
    /** Micro feedback: ripples, fades out, small toggles. */
    const val Quick = 120
    /** Exits, small reveals, chevrons. */
    const val Fast = 180
    /** Default for expand/collapse, colour changes, fade ins. */
    const val Standard = 240
    /** Screen transitions and large movements. */
    const val Emphasized = 320

    /** Pressed scale for small controls (icon buttons, segments, chips). */
    const val PressControl = 0.94f
    /** Pressed scale for cards, rows and full-width buttons. */
    const val PressCard = 0.97f
}

/** The one set of screen transitions used by the navigation host. */
object HollowTransitions {
    private val ease = NothingMotion.FluidEase

    /** Forward navigation (drill in). */
    val Push: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { (it * 0.12f).toInt() } +
            fadeIn(tween(HollowMotion.Standard, easing = ease)))
            .togetherWith(
                slideOutHorizontally(tween(HollowMotion.Standard, easing = ease)) { -(it * 0.08f).toInt() } +
                    fadeOut(tween(HollowMotion.Fast, easing = ease))
            )

    /** Back navigation (drill out). */
    val Pop: ContentTransform
        get() = (slideInHorizontally(tween(HollowMotion.Emphasized, easing = ease)) { -(it * 0.08f).toInt() } +
            fadeIn(tween(HollowMotion.Standard, easing = ease)))
            .togetherWith(
                slideOutHorizontally(tween(HollowMotion.Standard, easing = ease)) { (it * 0.12f).toInt() } +
                    fadeOut(tween(HollowMotion.Fast, easing = ease))
            )

    /** Full-screen flows (onboarding, fetching). */
    val Modal: ContentTransform
        get() = (fadeIn(tween(HollowMotion.Standard, easing = ease)) +
            slideInVertically(tween(HollowMotion.Emphasized, easing = ease)) { it / 16 } +
            scaleIn(tween(HollowMotion.Emphasized, easing = ease), initialScale = 0.97f))
            .togetherWith(
                fadeOut(tween(HollowMotion.Fast, easing = ease)) +
                    scaleOut(tween(HollowMotion.Fast, easing = ease), targetScale = 0.98f)
            )

    /** Same-level swaps. */
    val Fade: ContentTransform
        get() = (fadeIn(tween(HollowMotion.Standard, easing = ease)) +
            scaleIn(tween(HollowMotion.Standard, easing = ease), initialScale = 0.98f))
            .togetherWith(fadeOut(tween(HollowMotion.Fast, easing = ease)))
}

/**
 * Colours for the three home-screen phases (0 = disconnected, 1 = connecting, 2 = protected).
 * Cards, titles, dividers and accents all read from here so the phases stay in step.
 */
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

/**
 * Shared look for the three "bar" components (connected stats card, slide-to-connect pill, bottom nav)
 * so they always match in fill, border, height and corner radius — in every connection phase.
 */
object HollowBar {
    val Fill = androidx.compose.ui.graphics.Color(0xFF0C0B0E)
    val Border = androidx.compose.ui.graphics.Color(0xFF1C1C20)
    val Height = 60.dp
    val Shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
}

/** Upload-speed accent (soft rose) paired with the lime download accent. */
val HollowUploadAccent = Color(0xFFF28B9B)

/** Shared defaults for every AlertDialog so dialogs look identical app-wide. */
object HollowDialog {
    val Shape: Shape = HollowShapes.Dialog
    val containerColor: Color
        @Composable @ReadOnlyComposable get() = NothingSurfaceElevated
}

/**
 * The only bottom sheet used in the app: same surface, top radius, and drag handle everywhere.
 */
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
