package com.hollow.vpn.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hollow.vpn.ui.theme.HollowInk

/**
 * Nothing 9×9 dot-matrix icons — ported 1:1 from the design system's `G` dictionary
 * (js/nothing-ui.js). Every lit cell is one COMPLETE round dot (drawCircle, never a mask/clip).
 * Size is driven by [size]; the gap between dots is size/20, exactly like `--gs`.
 */
object NothingGlyphs {
    val bitmaps: Map<String, List<String>> = mapOf(
        "signal" to listOf(".......#.", ".....#.#.", ".....#.#.", "...#.#.#.", "...#.#.#.", "...#.#.#.", ".#.#.#.#.", ".#.#.#.#.", ".#.#.#.#."),
        "wifi" to listOf("..#####..", ".#.....#.", "#.......#", "...###...", "..#...#..", ".........", "....#....", ".........", "........."),
        "battery" to listOf(".........", ".#######.", "#.......#", "#.#####.#", "#.#####.#", "#.#####.#", "#.......#", ".#######.", "........."),
        "bell" to listOf("....#....", "...###...", "..#...#..", "..#...#..", ".#.....#.", ".#.....#.", "#########", ".........", "...###..."),
        "search" to listOf(".#####...", "#.....#..", "#.....#..", "#.....#..", ".#####...", "....##...", ".....##..", "......##.", "........."),
        "gear" to listOf("...#.#...", ".#######.", ".#.....#.", ".#.###.#.", "##.#.#.##", ".#.###.#.", ".#.....#.", ".#######.", "...#.#..."),
        "heart" to listOf(".##...##.", "####.####", "#########", "#########", "#########", ".#######.", "..#####..", "...###...", "....#...."),
        "star" to listOf("....#....", "...###...", "...###...", "#########", ".#######.", "..#####..", ".##.#.##.", ".#.....#.", "........."),
        "play" to listOf("..#......", "..##.....", "..###....", "..####...", "..#####..", "..####...", "..###....", "..##.....", "..#......"),
        "pause" to listOf(".##...##.", ".##...##.", ".##...##.", ".##...##.", ".##...##.", ".##...##.", ".##...##.", ".##...##.", ".##...##."),
        "lock" to listOf("..#####..", ".#.....#.", ".#.....#.", "#########", "####.####", "###.#.###", "###.#.###", "####.####", "#########"),
        "clock" to listOf(".#######.", "#.......#", "#...#...#", "#...#...#", "#...####.", "#.......#", "#.......#", "#.......#", ".#######."),
        "home" to listOf("....#....", "...###...", "..#####..", ".#######.", "#########", ".#.....#.", ".#.###.#.", ".#.#.#.#.", ".#.#.#.#."),
        "user" to listOf("...###...", "..#...#..", "..#...#..", "..#...#..", "...###...", ".#######.", "#########", "#########", "#########"),
        "music" to listOf(".....###.", "....####.", "....#..#.", "....#..#.", "....#..#.", ".##.#..#.", "###.#..#.", ".#...##..", "........."),
        "camera" to listOf(".........", "..##.####", "#########", "#..###..#", "#.#####.#", "#.#####.#", "#..###..#", "#########", "........."),
        "grid" to listOf("####.####", "####.####", "####.####", "####.####", ".........", "####.####", "####.####", "####.####", "####.####"),
        "plus" to listOf("....#....", "....#....", "....#....", "....#....", "#########", "....#....", "....#....", "....#....", "....#...."),
        "check" to listOf(".........", "........#", ".......##", "......##.", "#....##..", "##..##...", ".####....", "..##.....", "........."),
        "close" to listOf("#.......#", "##.....##", ".##...##.", "..##.##..", "...###...", "..##.##..", ".##...##.", "##.....##", "#.......#"),
        "power" to listOf("....#....", "..#.#.#..", ".#..#..#.", "#...#...#", "#.......#", "#.......#", ".#.....#.", "..#...#..", "...###..."),
        "more" to listOf(".........", ".........", ".........", ".........", "#...#...#", ".........", ".........", ".........", "........."),
        "folder" to listOf(".........", ".###.....", "#...#....", "#########", "#.......#", "#.......#", "#.......#", "#########", "........."),
        "up" to listOf("....#....", "...###...", "..#.#.#..", ".#..#..#.", "#...#...#", "....#....", "....#....", "....#....", "....#...."),
        "sun" to listOf("....#....", "#...#...#", "..#####..", ".#######.", "#######.#", ".#######.", "..#####..", "#...#...#", "....#...."),
        "moon" to listOf("...###...", "..##.....", ".##......", ".#.......", ".#.......", ".#.......", ".##......", "..##.....", "...###..."),
        "mail" to listOf(".........", "#########", "##.....##", "#.#...#.#", "#..#.#..#", "#...#...#", "#.......#", "#########", "........."),
        "calendar" to listOf(".#.....#.", "#########", "#.......#", "#.#.#.#.#", "#.......#", "#.#.#.#.#", "#.......#", "#########", "........."),
        "trash" to listOf("..#####..", ".#.....#.", "#########", ".#.#.#.#.", ".#.#.#.#.", ".#.#.#.#.", ".#.#.#.#.", "..#####..", "........."),
        "eye" to listOf(".........", "..#####..", ".#.....#.", "#..###..#", "#.#####.#", "#..###..#", ".#.....#.", "..#####..", "........."),
        "bolt" to listOf("...###...", "..##.....", ".##......", "#######..", "....##...", "...##....", "..##.....", ".##......", "#........"),
        "download" to listOf("....#....", "....#....", "....#....", "#...#...#", ".#..#..#.", "..#.#.#..", "...###...", "....#....", "#########"),
        "mappin" to listOf("...###...", "..#...#..", ".#.....#.", ".#.....#.", "..#...#..", "...#.#...", "....#....", "....#....", "....#...."),
        "chat" to listOf("#########", "#.......#", "#.#.#.#.#", "#.......#", "#########", "...##....", "..##.....", ".#.......", ".........")
    )
}

@Composable
fun NothingDotGlyph(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    color: Color = HollowInk
) {
    val rows = NothingGlyphs.bitmaps[name] ?: return
    Canvas(modifier = modifier.size(size).clearAndSetSemantics {}) {
        val gap = this.size.width / 20f
        val cell = (this.size.width - gap * 8f) / 9f
        val r = cell / 2f
        for (y in 0 until 9) for (x in 0 until 9) {
            if (rows[y][x] == '#') {
                drawCircle(
                    color = color,
                    radius = r,
                    center = Offset(x * (cell + gap) + r, y * (cell + gap) + r)
                )
            }
        }
    }
}


/**
 * Nothing line icons — the design system's `<symbol viewBox="0 0 24 24">` set, drawn 1:1
 * (stroke 1.6, round caps/joins, no fill). Used for the nav bar (.navbar .ico).
 */
@Composable
fun HollowLineIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = HollowInk
) {
    Canvas(modifier = modifier.size(size).clearAndSetSemantics {}) {
        val k = this.size.minDimension / 24f
        scale(k, pivot = Offset.Zero) {
            val st = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            fun path(d: String) = drawPath(PathParser().parsePathString(d).toPath(), color, style = st)
            when (name) {
                "home" -> path("M4 11l8-7 8 7M6 10v9h12v-9")
                "grid" -> listOf(4f to 4f, 14f to 4f, 4f to 14f, 14f to 14f).forEach { (x, y) ->
                    drawRoundRect(color, Offset(x, y), Size(6f, 6f), CornerRadius(1f, 1f), style = st)
                }
                "gear" -> {
                    path("M10.11 4.95 L10.47 2.32 L13.53 2.32 L13.89 4.95 L15.65 5.68 L17.76 4.07 L19.93 6.24 L18.32 8.35 L19.05 10.11 L21.68 10.47 L21.68 13.53 L19.05 13.89 L18.32 15.65 L19.93 17.76 L17.76 19.93 L15.65 18.32 L13.89 19.05 L13.53 21.68 L10.47 21.68 L10.11 19.05 L8.35 18.32 L6.24 19.93 L4.07 17.76 L5.68 15.65 L4.95 13.89 L2.32 13.53 L2.32 10.47 L4.95 10.11 L5.68 8.35 L4.07 6.24 L6.24 4.07 L8.35 5.68z")
                    drawCircle(color, 3f, Offset(12f, 12f), style = st)
                }
                "user" -> {
                    drawCircle(color, 4f, Offset(12f, 8f), style = st)
                    path("M4 21c0-4 4-6 8-6s8 2 8 6")
                }
                "plus" -> path("M12 5v14M5 12h14")
                "bell" -> path("M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6M10 20a2 2 0 0 0 4 0")
                "search" -> { drawCircle(color, 7f, Offset(11f, 11f), style = st); path("M21 21l-4.5-4.5") }
            }
        }
    }
}
