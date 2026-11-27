package com.hollow.vpn.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Nothing design tokens (source of truth: vibe-nothing-ui-design / css/tokens.css)
// Black / grey / white carry the UI. The accent (signal red) is for genuine
// signals only. Active controls invert display <-> bg, they never colorize.
// ─────────────────────────────────────────────────────────────────────────────
val NothingBlack = Color(0xFF000000)

/** Signal red — FILL only (white `accent-ink` on top). Foreground uses [NothingAccentTextDark]/Light via palette. */
val NothingRed = Color(0xFFD71921)
val NothingAccentInk = Color(0xFFFFFFFF)
/** Legacy alias kept so older call sites compile; prefer HollowAccentText / HollowInk. */
val HollowOrange = NothingRed
val HollowDarkInk = Color(0xFFEDEDED)

// dark
private val DarkBg = Color(0xFF000000)
private val DarkSurface = Color(0xFF0C0C0C)
private val DarkRaised = Color(0xFF171717)
private val DarkLine = Color(0xFF262626)
private val DarkLine2 = Color(0xFF3A3A3A)
private val DarkMuted = Color(0xFF5A5A5A)
private val DarkSecondary = Color(0xFF8C8C8C)
private val DarkPrimary = Color(0xFFEDEDED)
private val DarkDisplay = Color(0xFFFFFFFF)
private val DarkAccentText = Color(0xFFFF4438)
private val DarkSuccess = Color(0xFF7BE38A)
private val DarkWarning = Color(0xFFF2C94C)
private val DarkError = Color(0xFFFF5247)
private val DarkGlass = Color(0xE6101010)
private val DarkErrorContainer = Color(0xFF1A0A0A)

// light
private val LightBg = Color(0xFFF2F2F2)
private val LightSurface = Color(0xFFFFFFFF)
private val LightRaised = Color(0xFFEDEDED)
private val LightLine = Color(0xFFE2E2E2)
private val LightLine2 = Color(0xFFCFCFCF)
private val LightMuted = Color(0xFF9A9A9A)
private val LightSecondary = Color(0xFF585A5A)
private val LightPrimary = Color(0xFF1C1C1C)
private val LightDisplay = Color(0xFF000000)
private val LightAccentText = Color(0xFFC2141C)
private val LightSuccess = Color(0xFF3D8B4A)
private val LightWarning = Color(0xFF9C6B00)
private val LightError = Color(0xFFD23B30)
private val LightGlass = Color(0xF5FFFFFF)
private val LightErrorContainer = Color(0xFFFBEAE8)

@Immutable
data class HollowPalette(
    val canvas: Color,
    val onCanvas: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val border2: Color,
    val ink: Color,
    val inkSecondary: Color,
    val inkMuted: Color,
    val display: Color,
    val accentText: Color,
    val matrixGreen: Color,
    val warning: Color,
    val error: Color,
    val glass: Color,
    val invertedSurface: Color,
    val onInvertedSurface: Color,
    val pillNavFill: Color,
    val pillNavBorder: Color,
    val pillActiveBg: Color,
    val pillActiveIcon: Color,
    val topBtnFill: Color,
    val topBtnBorder: Color,
    val dottedLine: Color,
    val dotInactive: Color,
    val switchOffTrack: Color,
    val switchOffBorder: Color,
    val switchOffThumb: Color,
    val backdropDot: Color
)

val DarkHollowPalette = HollowPalette(
    canvas = DarkBg,
    onCanvas = DarkDisplay,
    surface = DarkSurface,
    surfaceElevated = DarkRaised,
    border = DarkLine,
    border2 = DarkLine2,
    ink = DarkPrimary,
    inkSecondary = DarkSecondary,
    inkMuted = DarkMuted,
    display = DarkDisplay,
    accentText = DarkAccentText,
    matrixGreen = DarkSuccess,
    warning = DarkWarning,
    error = DarkError,
    glass = DarkGlass,
    invertedSurface = DarkDisplay,
    onInvertedSurface = DarkBg,
    pillNavFill = DarkSurface,
    pillNavBorder = DarkLine2,
    pillActiveBg = DarkDisplay,
    pillActiveIcon = DarkBg,
    topBtnFill = Color.Transparent,
    topBtnBorder = DarkLine2,
    dottedLine = DarkLine2,
    dotInactive = DarkLine2,
    switchOffTrack = DarkLine2,
    switchOffBorder = DarkLine2,
    switchOffThumb = DarkMuted,
    backdropDot = Color.White.copy(alpha = 0.34f)
)

/** The spec forbids a third theme: "Carbon" is kept only as a setting value and resolves to the dark tokens. */
val CarbonDarkHollowPalette = DarkHollowPalette

val LightHollowPalette = HollowPalette(
    canvas = LightBg,
    onCanvas = LightDisplay,
    surface = LightSurface,
    surfaceElevated = LightRaised,
    border = LightLine,
    border2 = LightLine2,
    ink = LightPrimary,
    inkSecondary = LightSecondary,
    inkMuted = LightMuted,
    display = LightDisplay,
    accentText = LightAccentText,
    matrixGreen = LightSuccess,
    warning = LightWarning,
    error = LightError,
    glass = LightGlass,
    invertedSurface = LightDisplay,
    onInvertedSurface = LightBg,
    pillNavFill = LightSurface,
    pillNavBorder = LightLine2,
    pillActiveBg = LightDisplay,
    pillActiveIcon = LightBg,
    topBtnFill = Color.Transparent,
    topBtnBorder = LightLine2,
    dottedLine = LightLine2,
    dotInactive = LightLine2,
    switchOffTrack = LightLine2,
    switchOffBorder = LightLine2,
    switchOffThumb = LightMuted,
    backdropDot = Color.Black.copy(alpha = 0.30f)
)

val LocalHollowPalette = staticCompositionLocalOf { DarkHollowPalette }
val LocalHollowIsDark = staticCompositionLocalOf { true }
val LocalHollowIsOled = staticCompositionLocalOf { true }

val NothingCanvas: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.canvas

val NothingOnCanvas: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.onCanvas

val NothingSurface: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.surface

val NothingSurfaceElevated: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.surfaceElevated

val NothingBorder: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.border


val NothingInvertedSurface: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.invertedSurface

val NothingOnInvertedSurface: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.onInvertedSurface

val NothingPillNavFill: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillNavFill

val NothingPillNavBorder: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillNavBorder

val NothingPillActiveBg: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillActiveBg

val NothingPillActiveIcon: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.pillActiveIcon

val NothingTopBtnFill: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.topBtnFill

val NothingTopBtnBorder: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.topBtnBorder

val NothingDottedLine: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.dottedLine

val NothingDotInactive: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.dotInactive

val NothingSwitchOffTrack: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.switchOffTrack

val NothingSwitchOffBorder: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.switchOffBorder

val NothingSwitchOffThumb: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.switchOffThumb

val NothingBackdropDot: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.backdropDot

val HollowCream: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.canvas

val HollowCreamSurface: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.surface

val HollowCreamDivider: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.border

val HollowInk: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.ink

val HollowInkSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.inkSecondary

val HollowInkMuted: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.inkMuted


val HollowDisplayInk: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.display

val HollowLine2: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.border2

val HollowAccentText: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.accentText

val HollowSuccess: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.matrixGreen

val HollowWarning: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.warning

val HollowError: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.error

val HollowGlass: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.glass

val NothingMatrixGreen: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.matrixGreen

// legacy phase constants (non-themed) — now mapped onto the same tokens
val HollowObsidian = DarkBg
val HollowObsidianSurface = DarkSurface
val HollowObsidianDivider = DarkLine
val HollowObsidianMuted = DarkSecondary
val HollowAmber = DarkWarning

val HollowForest = DarkBg
val HollowForestCard = DarkSurface
val HollowForestDivider = DarkLine
val HollowForestMuted = DarkSecondary
val HollowLime = DarkSuccess

private fun nothingScheme(
    dark: Boolean, bg: Color, surface: Color, raised: Color, line: Color, line2: Color,
    secondary: Color, primary: Color, display: Color, error: Color, errorContainer: Color, success: Color
) = if (dark) darkColorScheme(
    primary = display, onPrimary = bg,
    primaryContainer = surface, onPrimaryContainer = primary,
    secondary = primary, onSecondary = bg,
    secondaryContainer = raised, onSecondaryContainer = primary,
    tertiary = success, onTertiary = bg,
    tertiaryContainer = surface, onTertiaryContainer = primary,
    error = error, onError = NothingAccentInk,
    errorContainer = errorContainer, onErrorContainer = error,
    background = bg, onBackground = primary,
    surface = bg, onSurface = primary,
    surfaceVariant = surface, onSurfaceVariant = secondary,
    outline = line2, outlineVariant = line,
    surfaceContainerLowest = bg, surfaceContainerLow = bg,
    surfaceContainer = surface, surfaceContainerHigh = raised, surfaceContainerHighest = raised,
    inverseSurface = display, inverseOnSurface = bg
) else lightColorScheme(
    primary = display, onPrimary = bg,
    primaryContainer = surface, onPrimaryContainer = primary,
    secondary = primary, onSecondary = bg,
    secondaryContainer = raised, onSecondaryContainer = primary,
    tertiary = success, onTertiary = Color.White,
    tertiaryContainer = surface, onTertiaryContainer = primary,
    error = error, onError = NothingAccentInk,
    errorContainer = errorContainer, onErrorContainer = error,
    background = bg, onBackground = primary,
    surface = bg, onSurface = primary,
    surfaceVariant = surface, onSurfaceVariant = secondary,
    outline = line2, outlineVariant = line,
    surfaceContainerLowest = surface, surfaceContainerLow = surface,
    surfaceContainer = surface, surfaceContainerHigh = raised, surfaceContainerHighest = raised,
    inverseSurface = display, inverseOnSurface = bg
)

val ExpressiveLightColorScheme = nothingScheme(
    false, LightBg, LightSurface, LightRaised, LightLine, LightLine2,
    LightSecondary, LightPrimary, LightDisplay, LightError, LightErrorContainer, LightSuccess
)
val ExpressiveDarkColorScheme = nothingScheme(
    true, DarkBg, DarkSurface, DarkRaised, DarkLine, DarkLine2,
    DarkSecondary, DarkPrimary, DarkDisplay, DarkError, DarkErrorContainer, DarkSuccess
)
/** No third theme in the spec — Carbon resolves to the dark scheme. */
val ExpressiveCarbonDarkColorScheme = ExpressiveDarkColorScheme
