package com.hollow.vpn.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * App-wide icon set drawn in the SAME style as the bottom navigation bar icons
 * ([HollowLineIcon]): 24x24 viewport, 1.6 stroke, round caps/joins, outline only.
 * Use these everywhere instead of androidx.compose.material.icons.* so every screen
 * (Home, Servers, Settings, dialogs, menus) looks identical to the nav bar.
 * Tint is applied by Icon(...) as usual.
 */
object HollowIcons {
    private const val STROKE = 1.6f

    private fun icon(
        name: String,
        vararg strokePaths: String,
        filledPaths: List<String> = emptyList(),
        autoMirror: Boolean = false
    ): ImageVector {
        val b = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = autoMirror
        )
        filledPaths.forEach { d ->
            b.addPath(
                pathData = PathParser().parsePathString(d).toNodes(),
                fill = SolidColor(Color.Black)
            )
        }
        strokePaths.forEach { d ->
            b.addPath(
                pathData = PathParser().parsePathString(d).toNodes(),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
        return b.build()
    }

    private const val BOLT = "M13 3L5 13.5h6L10 21l8-10.5h-6z"
    private const val SHIELD = "M12 3l7 3v5c0 4.5-3 8.2-7 10-4-1.8-7-5.5-7-10V6z"
    private const val STAR = "M12 3.5l2.6 5.4 5.9.8-4.3 4.1 1 5.9L12 17l-5.2 2.7 1-5.9L3.5 9.7l5.9-.8z"

    // --- Same glyphs as the bottom bar ---
    val Home by lazy { icon("Home", "M4 11l8-7 8 7M6 10v9h12v-9") }
    val Gear by lazy { icon("Gear", "M10.11 4.95 L10.47 2.32 L13.53 2.32 L13.89 4.95 L15.65 5.68 L17.76 4.07 L19.93 6.24 L18.32 8.35 L19.05 10.11 L21.68 10.47 L21.68 13.53 L19.05 13.89 L18.32 15.65 L19.93 17.76 L17.76 19.93 L15.65 18.32 L13.89 19.05 L13.53 21.68 L10.47 21.68 L10.11 19.05 L8.35 18.32 L6.24 19.93 L4.07 17.76 L5.68 15.65 L4.95 13.89 L2.32 13.53 L2.32 10.47 L4.95 10.11 L5.68 8.35 L4.07 6.24 L6.24 4.07 L8.35 5.68z", "M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0") }
    val Settings get() = Gear

    // --- Navigation / actions ---
    val Add by lazy { icon("Add", "M12 5v14M5 12h14") }
    val Remove by lazy { icon("Remove", "M5 12h14") }
    val Close by lazy { icon("Close", "M6 6l12 12M18 6L6 18") }
    val Check by lazy { icon("Check", "M5 12.5l4.5 4.5L19 7.5") }
    val ChevronRight by lazy { icon("ChevronRight", "M9 6l6 6-6 6", autoMirror = true) }
    val ChevronLeft by lazy { icon("ChevronLeft", "M15 6l-6 6 6 6", autoMirror = true) }
    val ArrowForward by lazy { icon("ArrowForward", "M5 12h14M13 6l6 6-6 6", autoMirror = true) }
    val ArrowBack by lazy { icon("ArrowBack", "M19 12H5M11 6l-6 6 6 6", autoMirror = true) }
    val OpenInNew by lazy { icon("OpenInNew", "M14 4h6v6M20 4l-9 9M18 14v4a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4", autoMirror = true) }
    val Search by lazy { icon("Search", "M18 11a7 7 0 1 1-14 0 7 7 0 0 1 14 0", "M21 21l-4.5-4.5") }
    val Refresh by lazy { icon("Refresh", "M19.5 12a7.5 7.5 0 0 1-13 5.1M4.5 12a7.5 7.5 0 0 1 13-5.1", "M17.5 3.5v4h-4M6.5 20.5v-4h4") }
    val MoreHoriz by lazy {
        icon("MoreHoriz", "M6.2 12a1.2 1.2 0 1 1-2.4 0 1.2 1.2 0 0 1 2.4 0", "M13.2 12a1.2 1.2 0 1 1-2.4 0 1.2 1.2 0 0 1 2.4 0", "M20.2 12a1.2 1.2 0 1 1-2.4 0 1.2 1.2 0 0 1 2.4 0")
    }
    val Edit by lazy { icon("Edit", "M4 20l1-4L16.5 4.5a2.1 2.1 0 0 1 3 3L8 19z", "M14.5 6.5l3 3") }
    val Delete by lazy { icon("Delete", "M4 7h16M10 11v6M14 11v6M6 7l1 12a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2l1-12M9 7V4h6v3") }
    val Share by lazy { icon("Share", "M12 15V4M8 7.5L12 4l4 3.5", "M6 11H5a1 1 0 0 0-1 1v7a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-7a1 1 0 0 0-1-1h-1") }
    val AddCircle by lazy { icon("AddCircle", "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0", "M12 8v8M8 12h8") }
    val RemoveCircle by lazy { icon("RemoveCircle", "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0", "M8 12h8") }
    val Copy by lazy { icon("Copy", "M11 9h7a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2h-7a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z", "M5 15V6a2 2 0 0 1 2-2h9") }
    val Paste by lazy { icon("Paste", "M9 4h6v3H9z", "M9 5.5H7a2 2 0 0 0-2 2V19a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7.5a2 2 0 0 0-2-2h-2") }
    val FileOpen by lazy { icon("FileOpen", "M4 7a2 2 0 0 1 2-2h4l2 2.5h6a2 2 0 0 1 2 2V17a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2z") }
    val Link by lazy { icon("Link", "M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1", "M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1") }
    val QrScan by lazy {
        icon("QrScan", "M4 8V6a2 2 0 0 1 2-2h2M16 4h2a2 2 0 0 1 2 2v2M20 16v2a2 2 0 0 1-2 2h-2M8 20H6a2 2 0 0 1-2-2v-2",
            "M8 8h3v3H8zM13 13h3v3h-3zM13 8h3M8 13h0.5")
    }
    val Image by lazy { icon("Image", "M5 4h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z", "M4 16l4.5-4.5 4 4 2.5-2.5 5 5", "M10.2 8.5a1.2 1.2 0 1 1-2.4 0 1.2 1.2 0 0 1 2.4 0") }
    val FlashOn by lazy { icon("FlashOn", BOLT) }
    val FlashOff by lazy { icon("FlashOff", BOLT, "M4 4l16 16") }
    val Bolt by lazy { icon("Bolt", BOLT) }
    val Star by lazy { icon("Star", STAR) }
    val StarFilled by lazy { icon("StarFilled", STAR, filledPaths = listOf(STAR)) }

    // --- Settings / features ---
    val Shield by lazy { icon("Shield", SHIELD) }
    val Security by lazy { icon("Security", SHIELD, "M9 12l2.2 2.2L15 10.5") }
    val Lock by lazy { icon("Lock", "M7 11V8a5 5 0 0 1 10 0v3", "M6 11h12a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1v-8a1 1 0 0 1 1-1z") }
    val Dns by lazy {
        icon("Dns", "M5 4h14a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z",
            "M5 13h14a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-5a1 1 0 0 1 1-1z", "M8 7.5h1.5M8 16.5h1.5")
    }
    val Speed by lazy { icon("Speed", "M4 17a9 9 0 1 1 16 0", "M12 14l4-5") }
    val Tune by lazy {
        icon("Tune", "M4 7h9M17 7h3M4 17h3M11 17h9", "M17 7a2 2 0 1 1-4 0 2 2 0 0 1 4 0", "M11 17a2 2 0 1 1-4 0 2 2 0 0 1 4 0")
    }
    val CallSplit by lazy { icon("CallSplit", "M12 21v-8M12 13c0-4-6-4-6-9M12 13c0-4 6-4 6-9", "M3.5 7L6 4l2.5 3M15.5 7L18 4l2.5 3") }
    val Apps by lazy {
        icon("Apps", "M6 6h.5M12 6h.5M18 6h.5M6 12h.5M12 12h.5M18 12h.5M6 18h.5M12 18h.5M18 18h.5")
    }
    val Widgets by lazy { icon("Widgets", "M4 4h6v6H4zM14 4h6v6h-6zM4 14h6v6H4z", "M17 13v8M13 17h8") }
    val Cloud by lazy { icon("Cloud", "M7 18h10.5a4 4 0 0 0 .5-7.97A6 6 0 0 0 6.5 9.5 4.3 4.3 0 0 0 7 18z") }
    val Notifications by lazy { icon("Notifications", "M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6M10 20a2 2 0 0 0 4 0") }
    val ThemeAuto by lazy { icon("ThemeAuto", "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0", filledPaths = listOf("M12 3a9 9 0 0 1 0 18z")) }
    val DarkMode by lazy { icon("DarkMode", "M20 14.5A8 8 0 0 1 9.5 4a8 8 0 1 0 10.5 10.5z") }
    val LightMode by lazy {
        icon("LightMode", "M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0",
            "M12 2.5v2M12 19.5v2M2.5 12h2M19.5 12h2M5.3 5.3l1.4 1.4M17.3 17.3l1.4 1.4M18.7 5.3l-1.4 1.4M6.7 17.3l-1.4 1.4")
    }
    val Support by lazy {
        icon("Support", "M4 14v-2a8 8 0 0 1 16 0v2", "M4 14h2.5a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1z",
            "M20 14h-2.5a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1H19a1 1 0 0 0 1-1z", "M20 18v1a2 2 0 0 1-2 2h-4")
    }
    val Bug by lazy {
        icon("Bug", "M9 8a3 3 0 0 1 6 0", "M8 8h8v6a4 4 0 0 1-8 0z", "M12 11v7M4 10l4 1.5M20 10l-4 1.5M4 17l4-1.5M20 17l-4-1.5")
    }
    val Terminal by lazy { icon("Terminal", "M4 5h16a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1z", "M7 10l3 2.5L7 15M12 15h5") }
    val Info by lazy { icon("Info", "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0", "M12 11v5.5M12 7.5h.5") }
    val Stats by lazy { icon("Stats", "M4 4v16h16", "M8 15l3.5-4 3 2.5L19 8") }
}
