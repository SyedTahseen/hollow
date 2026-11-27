package com.hollow.vpn.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Static brand & phase constants (safe to use inside non-@Composable contexts)
val NothingBlack = Color(0xFF000000)
val NothingRed = Color(0xFFE5252D)
val HollowOrange = NothingRed
val HollowDarkInk = Color(0xFFF5F5F7)

// Static palette base colors for Material 3 schemes
private val DarkCanvas = Color(0xFF000000)
private val DarkSurface = Color(0xFF121214)
private val DarkSurfaceElevated = Color(0xFF1A1A1E)
private val DarkBorder = Color(0xFF27272A)
private val DarkInk = Color(0xFFF5F5F7)
private val DarkInkSecondary = Color(0xFF98989F)
private val DarkInkMuted = Color(0xFF636369)
private val DarkMatrixGreen = Color(0xFF2EE59D)
private val DarkOrangeContainer = Color(0xFF2D0B0D)

// Nothing OS neutral achromatic matte dark palette for non-OLED (LCD/IPS) displays (zero blue tint)
private val CarbonCanvas = Color(0xFF121212)
private val CarbonSurface = Color(0xFF1B1B1B)
private val CarbonSurfaceElevated = Color(0xFF242424)
private val CarbonBorder = Color(0xFF2E2E2E)
private val CarbonInk = Color(0xFFF4F4F4)
private val CarbonInkSecondary = Color(0xFF9E9E9E)
private val CarbonInkMuted = Color(0xFF6E6E6E)
private val CarbonOrangeContainer = Color(0xFF331416)

private val LightCanvas = Color(0xFFF4F4F2)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceElevated = Color(0xFFEAEAE6)
private val LightBorder = Color(0xFFD8D8D3)
private val LightInk = Color(0xFF0E0E10)
private val LightInkSecondary = Color(0xFF5D5D65)
private val LightInkMuted = Color(0xFF8A8A92)
private val LightMatrixGreen = Color(0xFF0F9D58)
private val LightOrangeContainer = Color(0xFFFFE5E7)

@Immutable
data class HollowPalette(
    val canvas: Color,
    val onCanvas: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val ink: Color,
    val inkSecondary: Color,
    val inkMuted: Color,
    val matrixGreen: Color,
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
    canvas = DarkCanvas,
    onCanvas = Color.White,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    border = DarkBorder,
    ink = DarkInk,
    inkSecondary = DarkInkSecondary,
    inkMuted = DarkInkMuted,
    matrixGreen = DarkMatrixGreen,
    invertedSurface = Color.White,
    onInvertedSurface = NothingBlack,
    pillNavFill = Color(0xFF141417),
    pillNavBorder = Color(0xFF26262C),
    pillActiveBg = Color.White,
    pillActiveIcon = NothingBlack,
    topBtnFill = Color(0xFF0A0A0C),
    topBtnBorder = Color(0xFF2E2E34),
    dottedLine = Color(0xFF2B2B31),
    dotInactive = Color(0xFF2E2E34),
    switchOffTrack = NothingBlack,
    switchOffBorder = Color(0xFF424249),
    switchOffThumb = Color(0xFF8A8A94),
    backdropDot = Color.White.copy(alpha = 0.028f)
)

val CarbonDarkHollowPalette = HollowPalette(
    canvas = CarbonCanvas,
    onCanvas = CarbonInk,
    surface = CarbonSurface,
    surfaceElevated = CarbonSurfaceElevated,
    border = CarbonBorder,
    ink = CarbonInk,
    inkSecondary = CarbonInkSecondary,
    inkMuted = CarbonInkMuted,
    matrixGreen = DarkMatrixGreen,
    invertedSurface = Color(0xFFF4F4F4),
    onInvertedSurface = CarbonCanvas,
    pillNavFill = Color(0xFF1D1D1D),
    pillNavBorder = Color(0xFF303030),
    pillActiveBg = Color(0xFFF4F4F4),
    pillActiveIcon = CarbonCanvas,
    topBtnFill = Color(0xFF1A1A1A),
    topBtnBorder = Color(0xFF303030),
    dottedLine = Color(0xFF303030),
    dotInactive = Color(0xFF383838),
    switchOffTrack = CarbonSurface,
    switchOffBorder = Color(0xFF474747),
    switchOffThumb = CarbonInkSecondary,
    backdropDot = Color.White.copy(alpha = 0.030f)
)

val LightHollowPalette = HollowPalette(
    canvas = LightCanvas,
    onCanvas = LightInk,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    border = LightBorder,
    ink = LightInk,
    inkSecondary = LightInkSecondary,
    inkMuted = LightInkMuted,
    matrixGreen = LightMatrixGreen,
    invertedSurface = LightInk,
    onInvertedSurface = Color.White,
    pillNavFill = Color.White,
    pillNavBorder = LightBorder,
    pillActiveBg = LightInk,
    pillActiveIcon = Color.White,
    topBtnFill = Color.White,
    topBtnBorder = Color(0xFFD2D2CC),
    dottedLine = Color(0xFFCBCBC5),
    dotInactive = Color(0xFFD2D2CC),
    switchOffTrack = Color(0xFFE6E6E2),
    switchOffBorder = Color(0xFFB5B5AF),
    switchOffThumb = Color(0xFF6B6B73),
    backdropDot = Color.Black.copy(alpha = 0.045f)
)

val LocalHollowPalette = staticCompositionLocalOf { DarkHollowPalette }
val LocalHollowIsDark = staticCompositionLocalOf { true }
val LocalHollowIsOled = staticCompositionLocalOf { true }

// Reactive Nothing OS semantic tokens (automatically adapt to Dark & Light Mode)
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

val NothingMatrixGreen: Color
    @Composable @ReadOnlyComposable get() = LocalHollowPalette.current.matrixGreen

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

// Semantic accessors mapped dynamically to the active HollowPalette
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

// Connecting Phase (.busy — Pure OLED Black + Glyph Amber)
val HollowObsidian = Color(0xFF000000)
val HollowObsidianSurface = Color(0xFF141416)
val HollowObsidianDivider = Color(0xFF29292E)
val HollowObsidianMuted = Color(0xFF94949C)
val HollowAmber = Color(0xFFFFB020)

// Connected Phase — neutral black surfaces + the same green as the connected map pin / "fastest" flag.
// (Names kept for compatibility with existing call sites.)
val HollowForest = Color(0xFF050506)
val HollowForestCard = Color(0xFF0C0B0E)
val HollowForestDivider = Color(0xFF1C1C20)
val HollowForestMuted = Color(0xFF8E8E92)
val HollowLime = DarkMatrixGreen

val ExpressiveLightColorScheme = lightColorScheme(
    primary = LightInk,
    onPrimary = Color.White,
    primaryContainer = LightSurface,
    onPrimaryContainer = LightInk,
    secondary = NothingRed,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightInk,
    tertiary = LightMatrixGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDFF6E8),
    onTertiaryContainer = Color(0xFF0A3D22),
    error = NothingRed,
    onError = Color.White,
    errorContainer = LightOrangeContainer,
    onErrorContainer = Color(0xFF7D0E14),
    background = LightCanvas,
    onBackground = LightInk,
    surface = LightCanvas,
    onSurface = LightInk,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightInkSecondary,
    outline = Color(0xFFB5B5AF),
    outlineVariant = LightBorder,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAF9F7),
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurfaceElevated,
    surfaceContainerHighest = Color(0xFFDFDFDA),
    inverseSurface = LightInk,
    inverseOnSurface = Color.White
)

val ExpressiveDarkColorScheme = darkColorScheme(
    primary = DarkInk,
    onPrimary = NothingBlack,
    primaryContainer = DarkSurface,
    onPrimaryContainer = DarkInk,
    secondary = NothingRed,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkInk,
    tertiary = HollowLime,
    onTertiary = NothingBlack,
    tertiaryContainer = HollowForestCard,
    onTertiaryContainer = DarkInk,
    error = NothingRed,
    onError = Color.White,
    errorContainer = DarkOrangeContainer,
    onErrorContainer = Color(0xFFFFD8CF),
    background = DarkCanvas,
    onBackground = DarkInk,
    surface = DarkCanvas,
    onSurface = DarkInk,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkInkSecondary,
    outline = Color(0xFF3A3A40),
    outlineVariant = DarkBorder,
    surfaceContainerLowest = DarkCanvas,
    surfaceContainerLow = Color(0xFF0A0A0B),
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceElevated,
    surfaceContainerHighest = Color(0xFF242428),
    inverseSurface = DarkInk,
    inverseOnSurface = NothingBlack
)

val ExpressiveCarbonDarkColorScheme = darkColorScheme(
    primary = CarbonInk,
    onPrimary = CarbonCanvas,
    primaryContainer = CarbonSurface,
    onPrimaryContainer = CarbonInk,
    secondary = NothingRed,
    onSecondary = Color.White,
    secondaryContainer = CarbonSurfaceElevated,
    onSecondaryContainer = CarbonInk,
    tertiary = HollowLime,
    onTertiary = CarbonCanvas,
    tertiaryContainer = HollowForestCard,
    onTertiaryContainer = CarbonInk,
    error = NothingRed,
    onError = Color.White,
    errorContainer = CarbonOrangeContainer,
    onErrorContainer = Color(0xFFFFD8CF),
    background = CarbonCanvas,
    onBackground = CarbonInk,
    surface = CarbonCanvas,
    onSurface = CarbonInk,
    surfaceVariant = CarbonSurface,
    onSurfaceVariant = CarbonInkSecondary,
    outline = Color(0xFF424242),
    outlineVariant = CarbonBorder,
    surfaceContainerLowest = CarbonCanvas,
    surfaceContainerLow = Color(0xFF171717),
    surfaceContainer = CarbonSurface,
    surfaceContainerHigh = CarbonSurfaceElevated,
    surfaceContainerHighest = Color(0xFF2C2C2C),
    inverseSurface = CarbonInk,
    inverseOnSurface = CarbonCanvas
)
