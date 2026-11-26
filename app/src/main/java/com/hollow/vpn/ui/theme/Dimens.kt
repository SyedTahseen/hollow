package com.hollow.vpn.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/**
 * Spacing / layout tokens. Mirrors the Outrun reference app so both apps share one 4dp grid.
 * Use these instead of raw `NN.dp` literals for margins, padding and gaps.
 */
object HollowSpace {
    /** Hairline gap between tightly stacked text lines. */
    val Xxs = 2.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Xxxl = 32.dp

    /** Left/right gutter shared by every screen so headers, lists and cards line up. */
    val Gutter = Lg
}

/** Fixed component dimensions (same values as Outrun's `Sizes`). */
object HollowSizes {
    /** Minimum touch target / standard icon-button and toolbar-row height. */
    val Control = 48.dp
    /** Compact chips, pills and segmented-selector rows. */
    val Chip = 40.dp
    /** Text inputs. */
    val Field = 56.dp
    /** Filled / outlined buttons. */
    val Button = 48.dp
    /** Screen header row (title + actions). */
    val TopBar = 64.dp
    /** Widest a screen's content column may grow on tablets / foldables. */
    val MaxContent = 800.dp
    /** Widest a centred, single-purpose screen (splash / fetching) may grow. */
    val FocusMaxWidth = 480.dp
}

/** Single source of truth for the left/right gutter of every screen. */
val HollowScreenPadding = HollowSpace.Gutter

/** Display size (percent) limits, same range as Outrun. */
object HollowDisplay {
    const val DEFAULT = 100
    const val MIN = 80
    const val MAX = 130
    const val STEP = 5
}

/**
 * Scales every dp / sp in [content] by [percent]/100 (Outrun's "Display size").
 * The system font scale is preserved; only the density is multiplied.
 */
@Composable
fun HollowDesignScale(percent: Int, content: @Composable () -> Unit) {
    val base = LocalDensity.current
    val scale = percent.coerceIn(HollowDisplay.MIN, HollowDisplay.MAX) / 100f
    val density = remember(base, scale) { Density(base.density * scale, base.fontScale) }
    CompositionLocalProvider(LocalDensity provides density, content = content)
}
