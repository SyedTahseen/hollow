package com.hollow.vpn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
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

/** Family used for body / UI text. Doto everywhere; set to FontFamily.SansSerif to get Outrun's system font back. */
val HollowSansFamily: FontFamily = FontFamily.SansSerif

/**
 * Doto is used for all text. Sizes, line heights and tracking follow Outrun's scale
 * (Headline 28 · Heading/Subheadline 20 · Body 16 · Small 14 · Caption/Label 12) so both apps read the same.
 */

private fun outrun(size: Int, weight: FontWeight, line: Int, spacingEm: Float = 0f, family: FontFamily = HollowSansFamily) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    fontWeight = weight,
    lineHeight = line.sp,
    letterSpacing = spacingEm.em
)

/** Named styles mirroring Outrun's `Type` object. Use these (or the Material slots below) in screens. */
object HollowText {
    val Headline = TextStyle(
        fontFamily = DotoDisplayFamily, fontSize = 28.sp, fontWeight = FontWeight.Bold,
        lineHeight = 34.sp, letterSpacing = 0.45.sp
    )
    val Heading = outrun(20, FontWeight.Bold, 24, 0.01f, DotoDisplayFamily)
    val Subheadline = outrun(20, FontWeight.Normal, 24, 0.01f, DotoDisplayFamily)
    val Body = outrun(16, FontWeight.Normal, 24, 0.03f)
    val BodyBold = outrun(16, FontWeight.Bold, 24, 0.03f)
    val BodyMedium = outrun(16, FontWeight.Medium, 24, 0.03f)
    val Small = outrun(14, FontWeight.Normal, 20, 0.02f)
    val SmallMedium = outrun(14, FontWeight.Medium, 20, 0.02f)
    val Section = outrun(14, FontWeight.Medium, 20, 0.02f)
    val Caption = outrun(12, FontWeight.Normal, 16, 0.03f)
    val Label = outrun(12, FontWeight.Medium, 16, 0.03f)
    val Button = outrun(16, FontWeight.Normal, 24, 0.03f)
    val ButtonSmall = outrun(16, FontWeight.Medium, 24, 0.03f)
}

val TabularMonoBody = HollowText.Caption.copy(fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum")

/** Same slot mapping as Outrun's Theme.kt so Material components pick up identical text styles. */
val Typography = Typography(
    displayLarge = HollowText.Heading, displayMedium = HollowText.Heading, displaySmall = HollowText.Heading,
    headlineLarge = HollowText.Subheadline, headlineMedium = HollowText.Subheadline, headlineSmall = HollowText.Subheadline,
    titleLarge = HollowText.BodyMedium, titleMedium = HollowText.BodyMedium, titleSmall = HollowText.BodyMedium,
    bodyLarge = HollowText.Body, bodyMedium = HollowText.Body, bodySmall = HollowText.Body,
    labelLarge = HollowText.Label, labelMedium = HollowText.Label, labelSmall = HollowText.Label
)
