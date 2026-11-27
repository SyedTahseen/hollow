package com.hollow.vpn.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

object HollowSpace {
    val Xxs = 4.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Xxxl = 32.dp

    val Gutter = Lg
}

object HollowSizes {
    val Control = 48.dp
    val Chip = 40.dp
    val Field = 56.dp
    val Button = 48.dp
    val TopBar = 64.dp
    val ServerRow = 64.dp
    val TopBarExpanded = 112.dp
    val Dock = 80.dp
    val Dot = 8.dp
    val IconSm = 16.dp
    val Icon = 24.dp
    val IconLg = 32.dp
    val Tile = 64.dp
    val FlagWidth = 30.dp
    val FlagHeight = 20.dp
    val CardRowMin = 40.dp
    val SettingRowMin = 56.dp
    val Border = 1.dp
    val Spinner = 2.dp
    val SwitchWidth = 44.dp
    val SwitchHeight = 24.dp
    val SwitchThumb = 18.dp
    val MaxContent = 800.dp
    val FocusMaxWidth = 480.dp
}

val HollowScreenPadding = HollowSpace.Gutter

object HollowDisplay {
    const val DEFAULT = 100
    const val MIN = 80
    const val MAX = 130
    const val STEP = 5
}

@Composable
fun HollowDesignScale(percent: Int, content: @Composable () -> Unit) {
    val base = LocalDensity.current
    val scale = percent.coerceIn(HollowDisplay.MIN, HollowDisplay.MAX) / 100f
    val density = remember(base, scale) { Density(base.density * scale, base.fontScale) }
    CompositionLocalProvider(LocalDensity provides density, content = content)
}
