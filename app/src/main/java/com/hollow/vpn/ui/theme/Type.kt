package com.hollow.vpn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hollow.vpn.R

@OptIn(ExperimentalTextApi::class)
val DotoDisplayFamily = FontFamily(
    Font(
        resId = R.font.doto,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(550))
    ),
    Font(
        resId = R.font.doto,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(650))
    ),
    Font(
        resId = R.font.doto,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(750))
    ),
    Font(
        resId = R.font.doto,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(850))
    ),
    Font(
        resId = R.font.doto,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(900))
    )
)

/**
 * App-wide UI font: Fira Code (same typeface and weights as the Horus app).
 * Static Regular + Bold files: Normal/Medium use Regular, SemiBold and heavier use Bold.
 * The old family names stay as aliases so every existing screen picks up the new font.
 */
val FiraCodeFamily = FontFamily(
    Font(resId = R.font.fira_code, weight = FontWeight.Normal),
    Font(resId = R.font.fira_code, weight = FontWeight.Medium),
    Font(resId = R.font.fira_code_bold, weight = FontWeight.SemiBold),
    Font(resId = R.font.fira_code_bold, weight = FontWeight.Bold),
    Font(resId = R.font.fira_code_bold, weight = FontWeight.ExtraBold)
)

val SpaceGroteskFamily = FiraCodeFamily
val JetBrainsMonoFamily = FiraCodeFamily

val TabularMonoBody = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontFeatureSettings = "tnum"
)

val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DotoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.15).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.6.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.7.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FiraCodeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.5.sp
    )
)
