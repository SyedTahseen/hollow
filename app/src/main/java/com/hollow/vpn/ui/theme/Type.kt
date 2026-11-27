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

/*
 * Nothing type system — five roles, all bundled (SIL OFL 1.1, see /fonts-licenses):
 *   Dot Display  → Doto, ROND=100 (round dots), weight ≤ 500   : numerals, wordmark, clocks, %
 *   UI / Body    → Geist Regular                                : body copy
 *   Headline     → Geist SemiBold                               : dialog / card / section titles
 *   Mono / Data  → Geist Mono                                   : UPPERCASE labels, data, buttons, timestamps
 *   Editorial    → Newsreader Italic                            : page-level marketing only — NOT bundled in the app UI
 *
 * Scale: display 40 · heading-xl 32 · heading-lg 24 · heading-md 20 · body 16 · small 14 · caption/label 11
 */

/** Doto must stay ≤ 500 or neighbouring dots smear together. ROND is locked to 100 for round dots. */
@OptIn(ExperimentalTextApi::class)
private fun dotoFont(weight: FontWeight, wght: Int) = Font(
    resId = R.font.doto,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(wght),
        FontVariation.Setting("ROND", 100f)
    )
)

val DotoDisplayFamily = FontFamily(
    dotoFont(FontWeight.Normal, 400),
    dotoFont(FontWeight.Medium, 500),
    dotoFont(FontWeight.SemiBold, 500),
    dotoFont(FontWeight.Bold, 500),
    dotoFont(FontWeight.ExtraBold, 500)
)

val GeistFamily = FontFamily(
    Font(R.font.geist_regular, FontWeight.Normal),
    Font(R.font.geist_regular, FontWeight.Medium),
    Font(R.font.geist_semibold, FontWeight.SemiBold),
    Font(R.font.geist_semibold, FontWeight.Bold)
)

val GeistMonoFamily = FontFamily(
    Font(R.font.geist_mono_regular, FontWeight.Normal),
    Font(R.font.geist_mono_medium, FontWeight.Medium),
    Font(R.font.geist_mono_medium, FontWeight.SemiBold),
    Font(R.font.geist_mono_medium, FontWeight.Bold)
)

val HollowSansFamily: FontFamily = GeistFamily
val HollowMonoFamily: FontFamily = GeistMonoFamily

private fun style(
    family: FontFamily,
    size: Int,
    weight: FontWeight,
    line: Int,
    spacingEm: Float = 0f
) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    fontWeight = weight,
    lineHeight = line.sp,
    letterSpacing = spacingEm.em
)

object HollowText {
    // ── Dot display (numbers + wordmark only) ────────────────────────────────
    val Display = style(DotoDisplayFamily, 40, FontWeight.Medium, 44)
    val NumeralLg = style(DotoDisplayFamily, 32, FontWeight.Medium, 36)
    val NumeralMd = style(DotoDisplayFamily, 24, FontWeight.Medium, 28)
    val NumeralSm = style(DotoDisplayFamily, 20, FontWeight.Medium, 24)

    // ── Headlines — Geist SemiBold ──────────────────────────────────────────
    val Headline = style(GeistFamily, 32, FontWeight.SemiBold, 38)
    val Heading = style(GeistFamily, 20, FontWeight.SemiBold, 24)
    val Subheadline = style(GeistFamily, 20, FontWeight.SemiBold, 24)

    // ── Body — Geist Regular ────────────────────────────────────────────────
    val Body = style(GeistFamily, 16, FontWeight.Normal, 24)
    val BodyBold = style(GeistFamily, 16, FontWeight.SemiBold, 24)
    val BodyMedium = style(GeistFamily, 16, FontWeight.Normal, 24)
    val Small = style(GeistFamily, 14, FontWeight.Normal, 20)
    val SmallMedium = style(GeistFamily, 14, FontWeight.Normal, 20)

    // ── Mono / data / labels — Geist Mono (UPPERCASE the text at the call site) ─
    val Section = style(GeistMonoFamily, 11, FontWeight.Medium, 16, 0.10f)
    val Label = style(GeistMonoFamily, 11, FontWeight.Medium, 16, 0.08f)
    val Caption = style(GeistMonoFamily, 11, FontWeight.Normal, 16, 0.02f)
    val Data = style(GeistMonoFamily, 14, FontWeight.Normal, 20)
    val Button = style(GeistMonoFamily, 14, FontWeight.Medium, 20, 0.05f)
    val ButtonSmall = style(GeistMonoFamily, 11, FontWeight.Medium, 16, 0.06f)
}

val TabularMonoBody = HollowText.Caption.copy(fontFeatureSettings = "tnum")

val Typography = Typography(
    displayLarge = HollowText.Display, displayMedium = HollowText.NumeralLg, displaySmall = HollowText.NumeralMd,
    headlineLarge = HollowText.Headline, headlineMedium = HollowText.Heading, headlineSmall = HollowText.Heading,
    titleLarge = HollowText.Heading, titleMedium = HollowText.BodyBold, titleSmall = HollowText.Small,
    bodyLarge = HollowText.Body, bodyMedium = HollowText.Body, bodySmall = HollowText.Small,
    labelLarge = HollowText.Button, labelMedium = HollowText.Label, labelSmall = HollowText.Label
)
