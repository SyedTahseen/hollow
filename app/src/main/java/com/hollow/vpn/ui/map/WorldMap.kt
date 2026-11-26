package com.hollow.vpn.ui.map

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hollow.vpn.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

data class GeoPoint(val lon: Double, val lat: Double)

enum class MapPin { Selected, Connecting, Connected }

/** Colors for the map layers; the caller picks them to match the surface the map sits on. */
data class MapColors(
    val background: Color = Color(0xFF121212),
    val sphere: Color = Color(0xFF1B1B1B),
    /** Land dots. */
    val dot: Color = Color(0x47FFFFFF),
    /** Land dots of the active country. */
    val dotActive: Color = Color.White,
)

private fun wrap(d: Double): Double { var x = d; while (x > 180) x -= 360; while (x < -180) x += 360; return x }

private const val VIEW_DEG = 30f

private val R_DEFAULT = (180.0 / Math.PI).toFloat() / VIEW_DEG
private const val ZOOM_MIN = 0.2f
private const val ZOOM_MAX = 6f

private fun smooth(x: Float): Float { val t = x.coerceIn(0f, 1f); return t * t * (3f - 2f * t) }

/** Reusable buffers for the dot pass so drawing doesn't allocate every frame. */
private class DotScratch(f: DotField) {
    val base = FloatArray(f.n * 2)
    val old = FloatArray(f.n * 2)
    val fresh = FloatArray(f.n * 2)
    val bucketOf = IntArray(f.codes.size)
}

private class Cam(val vLon: Double, val vLat: Double, val mLon: Double, val mLat: Double, val r: Float, val space: Float)

@Composable
fun WorldMap(
    target: GeoPoint,
    countryCode: String?,
    dotColor: Color,
    modifier: Modifier = Modifier,
    pin: MapPin = MapPin.Selected,
    centerY: Float = 0.5f,
    colors: MapColors = MapColors(),
) {
    var from by remember { mutableStateOf(target) }
    var to by remember { mutableStateOf(target) }
    val prog = remember { Animatable(1f) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var offLon by remember { mutableFloatStateOf(0f) }
    var offLat by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val job = remember { arrayOfNulls<Job>(1) }

    val ctx = LocalContext.current
    val geo by produceState<List<Country>?>(WorldGeo.cached, ctx) { value = WorldGeo.load(ctx) }
    val dots by produceState<DotField?>(DotGeo.cached, ctx) { value = DotGeo.load(ctx) }
    val activeNow = remember(geo, countryCode, target) {
        countryCode?.trim()?.uppercase()?.takeIf { it.length == 2 }
            ?: geo?.let { WorldGeo.codeAt(it, target.lon, target.lat) }
    }
    var oldCode by remember { mutableStateOf<String?>(null) }
    var newCode by remember { mutableStateOf(countryCode?.trim()?.uppercase()?.takeIf { it.length == 2 }) }

    LaunchedEffect(target, activeNow) {
        if (target == to && activeNow == newCode) return@LaunchedEffect
        oldCode = newCode
        newCode = activeNow
        val t = prog.value.toDouble()
        from = GeoPoint(from.lon + wrap(to.lon - from.lon) * t, from.lat + (to.lat - from.lat) * t)
        to = target

        val l0 = offLon; val a0 = offLat
        if (l0 != 0f || a0 != 0f) {
            job[0]?.cancel()
            job[0] = scope.launch {
                animate(0f, 1f, animationSpec = tween(2000, easing = FastOutSlowInEasing)) { v, _ ->
                    offLon = l0 * (1f - v); offLat = a0 * (1f - v)
                }
            }
        }
        prog.snapTo(0f)
        prog.animateTo(1f, tween(2000, easing = FastOutSlowInEasing))
    }

    val dot by animateColorAsState(dotColor, tween(600), label = "dot")

    val outerDisconnected = ImageBitmap.imageResource(R.drawable.map_pin_outer_disconnected)
    val outerProtected = ImageBitmap.imageResource(R.drawable.map_pin_outer_protected)
    val animateOn = remember {
        runCatching { Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f }.getOrDefault(true)
    }
    var elapsedS by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(pin, to, animateOn) {
        if (!animateOn) return@LaunchedEffect
        val t0 = withFrameNanos { it }
        elapsedS = 0f
        while (true) withFrameNanos { elapsedS = (it - t0) / 1_000_000_000f }
    }

    fun camera(w: Float): Cam {
        val t = prog.value.toDouble()
        val dLon = wrap(to.lon - from.lon)
        val dLat = to.lat - from.lat
        val mLon = from.lon + dLon * t
        val mLat = from.lat + dLat * t
        val dist = hypot(abs(dLon), abs(dLat)).toFloat()

        val dip = (dist / 100f).coerceAtMost(0.3f)
        val z = zoom * (1f - dip * sin(PI * t).toFloat())
        val r = w * R_DEFAULT * z
        val space = smooth((1.15f * w - r) / (0.6f * w))
        return Cam(mLon + offLon, (mLat + offLat).coerceIn(-85.0, 85.0), mLon, mLat, r, space)
    }

    val stars = remember {
        val rnd = Random(11)
        List(70) { floatArrayOf(rnd.nextFloat(), rnd.nextFloat(), 0.5f + rnd.nextFloat() * 1.1f, 0.25f + rnd.nextFloat() * 0.55f) }
    }
    val dotScratch = remember(dots) { dots?.let { DotScratch(it) } }
    val dotPaint = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
        }
    }

    Box(
        modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gz, _ ->
                    job[0]?.cancel()
                    val r = (size.width * R_DEFAULT * zoom).toDouble()
                    val lat = (to.lat + offLat).coerceIn(-85.0, 85.0)
                    val cosLat = max(0.15, cos(Math.toRadians(lat)))

                    offLon = wrap(offLon - Math.toDegrees(pan.x / (r * cosLat))).toFloat()
                    offLat = (offLat + Math.toDegrees(pan.y / r)).toFloat()
                        .coerceIn((-85.0 - to.lat).toFloat(), (85.0 - to.lat).toFloat())
                    zoom = (zoom * gz).coerceIn(ZOOM_MIN, ZOOM_MAX)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    job[0]?.cancel()
                    job[0] = scope.launch {
                        val z0 = zoom; val l0 = offLon; val a0 = offLat
                        animate(0f, 1f, animationSpec = tween(600, easing = FastOutSlowInEasing)) { v, _ ->
                            zoom = z0 + (1f - z0) * v; offLon = l0 * (1f - v); offLat = a0 * (1f - v)
                        }
                    }
                })
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cam = camera(size.width)
            val cx = size.width / 2f
            val cy = size.height * centerY
            val r = cam.r
            val space = cam.space
            val centre = Offset(cx, cy)

            if (space > 0f) {
                for (s in stars) drawCircle(Color.White.copy(alpha = s[3] * space), s[2].dp.toPx(), Offset(s[0] * size.width, s[1] * size.height))

                val outer = r * 1.2f
                drawCircle(
                    Brush.radialGradient(
                        0f to Color.Transparent, (r / outer) to dot.copy(alpha = 0.32f * space), 1f to Color.Transparent,
                        center = centre, radius = outer,
                    ),
                    outer, centre,
                )
                drawCircle(lerp(colors.background, colors.sphere, 0.9f).copy(alpha = space), r, centre)
            }

            val t = prog.value
            val fadeOut = (1f - t * 2f).coerceIn(0f, 1f)
            val fadeIn = ((t - 0.5f) * 2f).coerceIn(0f, 1f)
            fun lit(code: String): Float = when {
                code == "--" -> 0f
                code == newCode && code == oldCode -> 1f
                code == newCode -> fadeIn
                code == oldCode -> fadeOut
                else -> 0f
            }

            val field = dots
            val sc = dotScratch
            if (field != null && sc != null) {
                val lonR = Math.toRadians(cam.vLon); val latR = Math.toRadians(cam.vLat)
                val cl = cos(lonR).toFloat(); val sl = sin(lonR).toFloat()
                val cp = cos(latR).toFloat(); val sp = sin(latR).toFloat()

                // Dot size follows the spacing on screen, so the matrix stays even while zooming.
                val ppd = r * (Math.PI / 180.0).toFloat()
                val diameter = (ppd * field.spacingDeg * 0.42f).coerceIn(1.1.dp.toPx(), 4.2.dp.toPx())

                val bucketOf = sc.bucketOf
                for (k in bucketOf.indices) {
                    val code = field.codes[k]
                    bucketOf[k] = when {
                        code == "--" -> 0
                        code == newCode -> 2
                        code == oldCode -> 1
                        else -> 0
                    }
                }
                val litNew = newCode?.let { lit(it) } ?: 0f
                val litOld = if (oldCode != null && oldCode != newCode) lit(oldCode!!) else 0f

                val base = sc.base; val old = sc.old; val fresh = sc.fresh
                var nBase = 0; var nOld = 0; var nFresh = 0
                val w = size.width; val h = size.height
                val xyz = field.xyz; val owner = field.country
                for (i in 0 until field.n) {
                    val x = xyz[i * 3]; val y = xyz[i * 3 + 1]; val z = xyz[i * 3 + 2]
                    val a = x * cl + y * sl
                    if (a * cp + z * sp <= 0f) continue
                    val sx = cx + r * (-x * sl + y * cl)
                    if (sx < -diameter || sx > w + diameter) continue
                    val sy = cy - r * (-a * sp + z * cp)
                    if (sy < -diameter || sy > h + diameter) continue
                    when (bucketOf[owner[i].toInt()]) {
                        2 -> { fresh[nFresh++] = sx; fresh[nFresh++] = sy }
                        1 -> { old[nOld++] = sx; old[nOld++] = sy }
                        else -> { base[nBase++] = sx; base[nBase++] = sy }
                    }
                }

                val nc = drawContext.canvas.nativeCanvas
                dotPaint.strokeWidth = diameter
                dotPaint.color = colors.dot.toArgb()
                nc.drawPoints(base, 0, nBase, dotPaint)
                if (nOld > 0) {
                    dotPaint.color = lerp(colors.dot, colors.dotActive, litOld).toArgb()
                    nc.drawPoints(old, 0, nOld, dotPaint)
                }
                if (nFresh > 0) {
                    dotPaint.color = lerp(colors.dot, colors.dotActive, litNew).toArgb()
                    nc.drawPoints(fresh, 0, nFresh, dotPaint)
                }
            }

            if (space > 0f) {
                drawCircle(
                    Brush.radialGradient(
                        0f to Color.Transparent, 0.65f to Color.Transparent, 1f to colors.background.copy(alpha = 0.55f * space),
                        center = centre, radius = r,
                    ),
                    r, centre,
                )
                drawCircle(dot.copy(alpha = 0.45f * space), r, centre, style = Stroke(1.5.dp.toPx()))
            }
        }

        Canvas(Modifier.fillMaxSize()) {
            val cam = camera(size.width)
            val lonR = Math.toRadians(cam.vLon); val latR = Math.toRadians(cam.vLat)
            val mLon = Math.toRadians(cam.mLon); val mLat = Math.toRadians(cam.mLat.coerceIn(-85.0, 85.0))
            val x = cos(mLat) * cos(mLon); val y = cos(mLat) * sin(mLon); val z = sin(mLat)
            val a = x * cos(lonR) + y * sin(lonR)
            val east = -x * sin(lonR) + y * cos(lonR)
            val depth = a * cos(latR) + z * sin(latR)
            val north = -a * sin(latR) + z * cos(latR)
            if (depth <= 0.02) return@Canvas
            val c = Offset(size.width / 2f + cam.r * east.toFloat(), size.height * centerY - cam.r * north.toFloat())
            val animS = elapsedS
            val showAnim = animateOn
            val fullR = 48.dp.toPx(); val smallR = 32.dp.toPx(); val innerR = 12.dp.toPx()
            fun decel(x: Float): Float { val t = x.coerceIn(0f, 1f); return 1f - (1f - t) * (1f - t) * (1f - t) }

            val outerBmp = when (pin) { MapPin.Selected -> outerDisconnected; MapPin.Connected -> outerProtected; MapPin.Connecting -> null }
            if (outerBmp != null) {
                val outerR: Float? = if (!showAnim) fullR else {
                    val delay = if (pin == MapPin.Connected) 0f else 0.7f
                    if (animS < delay) null else {
                        val st = animS - delay
                        if (st < 1f) fullR * decel(st)
                        else {
                            val ps = (st - 1f) % 3f
                            val half = 1.5f
                            val f = if (ps > half) decel((ps - half) / half) else 1f - decel(ps / half)
                            smallR + (fullR - smallR) * f
                        }
                    }
                }
                if (outerR != null && outerR > 0f) {
                    drawImage(
                        outerBmp,
                        srcSize = IntSize(outerBmp.width, outerBmp.height),
                        dstOffset = IntOffset((c.x - outerR).roundToInt(), (c.y - outerR).roundToInt()),
                        dstSize = IntSize((outerR * 2).roundToInt(), (outerR * 2).roundToInt()),
                    )
                }
            }
            val r0: Float? = if (!showAnim || pin == MapPin.Connected) innerR else {
                if (animS < 0.2f) null else {
                    val st = animS - 0.2f
                    if (st < 0.4f) innerR * decel(st / 0.4f) else innerR
                }
            }
            if (r0 != null && r0 > 0f) {
                drawCircle(Color.White, r0, c)
                drawCircle(dot, r0 / 2f, c)
            }
        }
    }
}
