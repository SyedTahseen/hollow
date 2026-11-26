package com.hollow.vpn.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Dotted wireframe globe: land drawn as a dot grid, coastlines as hairlines and a lat/lon graticule.
 * Slowly rotates, tilted towards the northern hemisphere. Cropped by its parent bounds so only
 * part of the sphere is visible (the globe is deliberately larger than the space it sits in).
 */
@Composable
fun HollowGlobe(
    color: Color,
    modifier: Modifier = Modifier,
    /** Globe radius as a fraction of the shorter side of the canvas. */
    radiusFraction: Float = 0.95f,
    /** Horizontal center of the globe as a fraction of the canvas width. */
    centerXFraction: Float = 0.62f,
    centerYFraction: Float = 0.62f,
    tiltDegrees: Float = 52f,
    startLongitude: Float = -150f
) {
    val data by produceState<GlobeData?>(initialValue = GlobeDataCache.cached) {
        value = GlobeDataCache.cached ?: withContext(Dispatchers.Default) { GlobeDataCache.get() }
    }
    val transition = rememberInfiniteTransition(label = "globe_spin")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "globe_spin_deg"
    )

    val dotPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = android.graphics.Paint.Cap.ROUND
    } }
    val dimDotPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = android.graphics.Paint.Cap.ROUND
    } }
    val coastPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
    } }
    val gridPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
    } }

    Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
        val g = data ?: return@Canvas
        val r = min(size.width, size.height) * radiusFraction
        val cx = size.width * centerXFraction
        val cy = size.height * centerYFraction
        val lon0 = Math.toRadians((startLongitude + spin).toDouble())
        val lat0 = Math.toRadians(tiltDegrees.toDouble())
        val sinLat0 = sin(lat0).toFloat()
        val cosLat0 = cos(lat0).toFloat()
        val px = 1.dp.toPx()

        dotPaint.color = color.copy(alpha = 0.95f).toArgb()
        dotPaint.strokeWidth = 1.7f * px
        dimDotPaint.color = color.copy(alpha = 0.42f).toArgb()
        dimDotPaint.strokeWidth = 1.2f * px
        coastPaint.color = color.copy(alpha = 0.95f).toArgb()
        coastPaint.strokeWidth = 1.1f * px
        gridPaint.color = color.copy(alpha = 0.28f).toArgb()
        gridPaint.strokeWidth = 0.8f * px

        // Projects (latitude sin/cos, longitude) -> screen; returns cos(angular distance) or -1 if hidden.
        val out = FloatArray(2)
        fun project(sinLat: Float, cosLat: Float, lon: Float): Float {
            val dl = lon - lon0.toFloat()
            val cosDl = cos(dl)
            val cosC = sinLat0 * sinLat + cosLat0 * cosLat * cosDl
            if (cosC <= 0f) return -1f
            val x = cosLat * sin(dl)
            val y = cosLat0 * sinLat - sinLat0 * cosLat * cosDl
            out[0] = cx + r * x
            out[1] = cy - r * y
            return cosC
        }

        drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas

            // Graticule
            nc.drawCircle(cx, cy, r, gridPaint)
            for (path in g.graticule) {
                var prevVisible = false
                var prevX = 0f
                var prevY = 0f
                for (i in 0 until path.size step 3) {
                    val vis = project(path[i], path[i + 1], path[i + 2]) > 0f
                    if (vis && prevVisible) nc.drawLine(prevX, prevY, out[0], out[1], gridPaint)
                    prevVisible = vis
                    prevX = out[0]
                    prevY = out[1]
                }
            }

            // Land dots
            val pts = g.landPoints
            val bright = g.scratchBright
            val dim = g.scratchDim
            var nb = 0
            var nd = 0
            var i = 0
            while (i < pts.size) {
                val c = project(pts[i], pts[i + 1], pts[i + 2])
                if (c > 0f) {
                    if (c > 0.32f) {
                        bright[nb++] = out[0]; bright[nb++] = out[1]
                    } else {
                        dim[nd++] = out[0]; dim[nd++] = out[1]
                    }
                }
                i += 3
            }
            nc.drawPoints(dim, 0, nd, dimDotPaint)
            nc.drawPoints(bright, 0, nb, dotPaint)

            // Coastlines
            for (path in g.coastlines) {
                var prevVisible = false
                var prevX = 0f
                var prevY = 0f
                for (k in 0 until path.size step 3) {
                    val vis = project(path[k], path[k + 1], path[k + 2]) > 0.02f
                    if (vis && prevVisible) nc.drawLine(prevX, prevY, out[0], out[1], coastPaint)
                    prevVisible = vis
                    prevX = out[0]
                    prevY = out[1]
                }
            }
        }
    }
}

// ───────────────────────────── data ─────────────────────────────

/** Flat float triples: (sin(lat), cos(lat), lon[rad]) for fast per-frame projection. */
internal class GlobeData(
    val landPoints: FloatArray,
    val coastlines: List<FloatArray>,
    val graticule: List<FloatArray>
) {
    val scratchBright = FloatArray((landPoints.size / 3) * 2 + 2)
    val scratchDim = FloatArray((landPoints.size / 3) * 2 + 2)
}

internal object GlobeDataCache {
    @Volatile var cached: GlobeData? = null
    fun get(): GlobeData = cached ?: synchronized(this) { cached ?: buildGlobeData().also { cached = it } }
}

private fun triple(latDeg: Double, lonDeg: Double, into: MutableList<Float>) {
    val lat = Math.toRadians(latDeg)
    into.add(sin(lat).toFloat())
    into.add(cos(lat).toFloat())
    into.add(Math.toRadians(lonDeg).toFloat())
}

private fun buildGlobeData(): GlobeData {
    val polys = GlobePolygons.land
    val lakes = GlobePolygons.water
    val all = polys + lakes

    // Land dot grid, even-odd across land + inland water rings.
    val land = ArrayList<Float>(30000)
    val stepDeg = 2.4
    var lat = -88.0
    while (lat <= 88.0) {
        val n = max(1, (360.0 * cos(Math.toRadians(lat)) / stepDeg).roundToInt())
        for (j in 0 until n) {
            val lon = -180.0 + 360.0 * j / n
            val isLand = lat < -72.0 || insideEvenOdd(lon, lat, all)
            if (isLand) triple(lat, lon, land)
        }
        lat += stepDeg
    }

    // Densified coastlines (so long edges follow the sphere).
    val coasts = (polys + lakes).map { ring ->
        val list = ArrayList<Float>()
        for (idx in ring.indices) {
            val a = ring[idx]
            val b = ring[(idx + 1) % ring.size]
            val dist = max(abs(b[0] - a[0]), abs(b[1] - a[1]))
            val steps = max(1, (dist / 2.5).toInt())
            for (s in 0 until steps) {
                val t = s.toDouble() / steps
                triple(a[1] + (b[1] - a[1]) * t, a[0] + (b[0] - a[0]) * t, list)
            }
        }
        // close ring
        triple(ring[0][1], ring[0][0], list)
        list.toFloatArray()
    }

    // Graticule every 20°.
    val grid = ArrayList<FloatArray>()
    var mer = -180
    while (mer < 180) {
        val l = ArrayList<Float>()
        var la = -90
        while (la <= 90) { triple(la.toDouble().coerceIn(-89.5, 89.5), mer.toDouble(), l); la += 5 }
        grid.add(l.toFloatArray())
        mer += 20
    }
    var par = -80
    while (par <= 80) {
        val l = ArrayList<Float>()
        var lo = -180
        while (lo <= 180) { triple(par.toDouble(), lo.toDouble(), l); lo += 5 }
        grid.add(l.toFloatArray())
        par += 20
    }

    return GlobeData(land.toFloatArray(), coasts, grid)
}

private fun insideEvenOdd(lon: Double, lat: Double, rings: List<Array<DoubleArray>>): Boolean {
    var inside = false
    for (ring in rings) {
        var j = ring.size - 1
        for (i in ring.indices) {
            val xi = ring[i][0]; val yi = ring[i][1]
            val xj = ring[j][0]; val yj = ring[j][1]
            if ((yi > lat) != (yj > lat) && lon < (xj - xi) * (lat - yi) / (yj - yi) + xi) {
                inside = !inside
            }
            j = i
        }
    }
    return inside
}

/** Simplified continent outlines as (lon, lat) rings. Good enough for a decorative globe. */
private object GlobePolygons {
    private fun ring(vararg v: Double): Array<DoubleArray> =
        Array(v.size / 2) { doubleArrayOf(v[it * 2], v[it * 2 + 1]) }

    val land: List<Array<DoubleArray>> = listOf(
        // North America
        ring(-168.0,66.0, -162.0,70.0, -156.0,71.5, -141.0,70.0, -128.0,70.0, -115.0,68.0, -95.0,72.0, -85.0,70.0,
            -82.0,66.0, -90.0,63.0, -94.0,59.0, -92.0,57.0, -82.0,55.0, -79.0,52.0, -78.0,58.0, -77.0,62.0, -70.0,61.0,
            -65.0,60.0, -61.0,56.0, -56.0,52.0, -60.0,47.0, -66.0,45.0, -70.0,43.0, -70.0,41.5, -74.0,40.0, -76.0,37.0,
            -76.0,35.0, -81.0,31.0, -80.0,26.0, -82.0,27.0, -84.0,30.0, -89.0,30.0, -94.0,29.5, -97.0,27.0, -97.5,22.0,
            -95.0,18.5, -91.0,18.5, -90.0,21.0, -87.0,21.0, -88.0,16.0, -84.0,15.5, -83.0,11.0, -79.5,9.0, -77.5,8.5,
            -80.0,7.5, -84.0,8.0, -87.5,13.0, -92.0,14.5, -96.0,16.0, -105.0,20.0, -105.5,23.0, -109.0,26.0, -112.5,30.0,
            -114.5,31.0, -110.0,23.0, -112.0,25.0, -115.0,29.0, -117.0,32.5, -120.5,34.5, -124.0,40.0, -124.0,47.0,
            -123.0,49.0, -128.0,51.0, -131.0,55.0, -136.0,58.0, -141.0,60.0, -148.0,60.5, -152.0,59.0, -158.0,57.0,
            -164.0,55.0, -158.0,58.5, -162.0,60.0, -165.0,62.0, -168.0,65.0),
        // Greenland
        ring(-73.0,78.0, -60.0,82.0, -30.0,83.5, -20.0,80.0, -20.0,72.0, -25.0,69.0, -40.0,65.0, -43.0,60.0, -50.0,63.0,
            -54.0,67.0, -58.0,75.0, -68.0,76.5),
        // Baffin, Victoria, Ellesmere
        ring(-80.0,73.0, -68.0,70.0, -62.0,66.5, -66.0,62.5, -74.0,64.0, -78.0,65.0, -90.0,70.0),
        ring(-118.0,70.5, -105.0,69.0, -101.0,70.0, -110.0,73.0, -118.0,72.0),
        ring(-90.0,76.5, -75.0,78.0, -62.0,82.0, -80.0,83.0, -95.0,80.0),
        // South America
        ring(-77.5,8.5, -72.0,12.0, -64.0,10.5, -60.0,8.5, -52.0,5.0, -50.0,0.0, -44.0,-2.5, -35.0,-5.5, -37.0,-12.0,
            -40.0,-20.0, -45.0,-23.5, -49.0,-28.0, -53.0,-34.0, -58.0,-35.0, -57.0,-38.5, -62.0,-39.0, -65.0,-41.0,
            -65.0,-45.0, -68.0,-50.0, -69.0,-52.5, -72.0,-53.5, -75.0,-50.0, -74.0,-42.0, -73.0,-37.0, -71.5,-30.0,
            -70.5,-18.0, -76.0,-14.0, -81.0,-5.0, -80.0,-1.0, -78.5,2.0, -77.5,7.0),
        // Eurasia
        ring(-9.0,43.0, -9.0,37.0, -5.5,36.0, 0.0,38.5, 3.0,42.0, 6.0,43.0, 9.0,44.5, 12.5,42.0, 16.0,38.0, 18.0,40.0,
            13.0,45.5, 19.0,42.0, 23.0,38.0, 26.0,40.5, 29.0,41.0, 28.0,37.0, 36.0,36.5, 35.0,31.0, 35.0,28.0, 39.0,21.0,
            43.0,13.0, 45.0,12.8, 52.0,16.0, 57.0,19.0, 59.5,22.5, 56.0,26.0, 51.5,24.5, 50.0,26.5, 48.0,29.5, 50.0,30.0,
            56.5,27.0, 61.0,25.0, 67.0,24.5, 70.0,21.0, 73.0,17.0, 77.0,8.0, 80.0,15.0, 80.0,20.0, 87.0,21.5, 92.0,21.5,
            94.0,17.0, 98.0,16.0, 98.5,10.0, 100.0,6.0, 103.5,1.5, 104.0,10.0, 109.0,12.0, 106.0,20.0, 108.0,21.5,
            112.0,22.0, 117.0,24.0, 121.0,28.0, 122.0,31.0, 119.0,35.0, 122.0,37.0, 119.0,38.0, 121.0,40.0, 125.0,39.5,
            126.0,35.0, 129.0,35.0, 129.5,38.0, 128.0,41.0, 133.0,43.0, 138.0,48.0, 141.0,53.0, 138.0,56.0, 143.0,59.0,
            151.0,59.0, 155.0,59.5, 156.0,51.0, 160.0,53.0, 163.0,58.0, 170.0,60.0, 178.0,64.0, 180.0,68.5, 170.0,70.0,
            160.0,69.5, 150.0,71.0, 140.0,72.5, 130.0,71.0, 113.0,74.0, 105.0,77.5, 95.0,76.0, 87.0,75.0, 80.0,73.0,
            72.0,72.5, 68.0,68.5, 60.0,69.0, 54.0,68.5, 44.0,68.0, 41.0,66.5, 35.0,66.5, 33.0,69.3, 25.0,71.0, 18.0,70.0,
            14.0,67.0, 8.0,63.0, 5.0,60.0, 5.0,58.5, 10.0,59.0, 10.0,57.5, 8.2,56.5, 8.5,54.5, 7.0,53.5, 4.5,52.5,
            2.0,51.0, 1.5,50.5, -1.5,49.7, -4.5,48.5, -2.0,47.0, -1.2,46.0, -1.5,43.5),
        // Africa
        ring(-17.0,21.0, -13.0,27.5, -9.5,30.0, -6.0,35.7, 10.0,37.0, 11.0,33.0, 20.0,30.5, 25.0,32.0, 32.0,31.2,
            34.0,28.0, 35.0,24.0, 37.0,19.0, 39.0,15.5, 43.0,12.0, 51.0,12.0, 51.0,10.0, 46.0,2.0, 41.0,-2.0, 39.5,-6.0,
            40.5,-15.0, 35.0,-20.0, 35.0,-24.0, 32.5,-26.0, 30.0,-31.0, 27.0,-34.0, 20.0,-34.8, 18.0,-32.0, 15.0,-27.0,
            12.0,-17.0, 13.5,-12.0, 12.0,-5.0, 9.0,-1.0, 9.5,4.0, 6.0,4.5, 2.0,6.3, -4.0,5.0, -8.0,4.5, -13.0,8.0,
            -16.0,12.5, -17.5,15.0, -16.0,19.0),
        ring(44.0,-25.0, 47.0,-25.0, 50.0,-15.0, 49.5,-12.0, 47.0,-15.0, 44.0,-20.0),
        // Australia + New Zealand
        ring(114.0,-22.0, 114.0,-34.0, 118.0,-35.0, 124.0,-33.5, 131.0,-31.5, 135.0,-35.0, 138.0,-35.0, 140.0,-38.0,
            146.0,-39.0, 150.0,-37.0, 153.0,-31.0, 153.0,-25.0, 146.0,-19.0, 142.0,-10.5, 141.0,-17.0, 136.0,-15.0,
            136.0,-12.0, 131.0,-11.5, 127.0,-14.0, 122.0,-17.0, 118.0,-20.5),
        ring(172.5,-34.5, 178.0,-37.5, 175.0,-41.5, 173.0,-39.0),
        ring(172.5,-40.5, 174.0,-41.5, 171.0,-44.5, 168.0,-46.5, 166.5,-45.5, 170.0,-43.0),
        // Islands
        ring(95.0,5.5, 98.0,4.0, 104.0,-2.0, 106.0,-6.0, 102.0,-4.0, 98.0,0.0, 95.5,3.0),
        ring(109.0,1.0, 111.0,2.0, 115.0,5.0, 119.0,5.0, 118.0,1.0, 116.0,-4.0, 110.0,-3.0),
        ring(105.0,-6.0, 114.0,-7.5, 114.0,-8.5, 106.0,-7.0),
        ring(131.0,-1.0, 138.0,-1.5, 145.0,-5.0, 150.0,-10.0, 143.0,-9.0, 138.0,-8.0, 133.0,-4.0),
        ring(130.5,31.5, 131.0,34.0, 135.0,34.5, 140.0,35.0, 141.0,38.0, 142.0,41.0, 140.0,40.0, 139.5,38.0, 136.0,36.5,
            133.0,35.5, 130.0,33.5),
        ring(140.0,42.0, 143.0,42.0, 145.0,43.5, 142.0,45.5, 141.0,43.5),
        ring(-5.5,50.0, 1.5,51.0, 1.5,53.0, -2.0,56.0, -2.0,57.5, -5.0,58.5, -6.0,56.0, -3.5,54.5, -5.0,52.0, -3.5,51.5),
        ring(-10.0,52.0, -6.0,52.0, -6.0,55.0, -8.0,55.0, -10.0,54.0),
        ring(-24.0,65.5, -18.0,66.3, -13.5,65.0, -18.0,63.5, -22.0,64.0),
        ring(80.0,6.0, 82.0,7.5, 80.0,9.5),
        ring(-85.0,22.0, -80.0,23.0, -74.5,20.0, -78.0,20.0),
        ring(-74.0,18.5, -71.0,19.8, -68.5,18.5, -71.0,17.8),
        ring(120.0,18.5, 122.0,18.5, 122.0,14.0, 124.0,13.0, 121.0,13.5, 120.0,16.0),
        ring(122.0,7.0, 126.0,9.0, 126.0,6.5, 124.0,6.0),
        ring(142.0,46.0, 143.5,50.0, 143.0,54.0, 142.0,53.0, 142.0,49.0)
    )

    /** Inland seas: flip the even-odd parity back to "water". */
    val water: List<Array<DoubleArray>> = listOf(
        ring(28.0,41.5, 28.0,44.5, 30.0,46.0, 33.0,46.0, 35.0,45.0, 38.0,47.0, 40.0,43.5, 41.5,41.5, 36.0,41.5, 32.0,41.5),
        ring(47.0,45.0, 53.0,46.5, 54.0,41.0, 54.0,37.0, 50.0,37.0, 49.0,40.0, 47.5,43.0),
        ring(10.5,54.2, 14.0,54.2, 19.0,54.5, 21.0,55.5, 21.0,57.5, 23.5,57.5, 24.0,59.2, 28.0,59.8, 29.5,60.2, 24.0,60.2,
            22.0,60.4, 21.4,62.5, 25.0,65.0, 25.3,65.6, 22.0,65.8, 18.2,62.8, 17.3,60.8, 18.8,59.3, 16.8,57.5, 16.3,56.2,
            14.3,55.5, 12.8,55.4, 12.4,56.2, 11.0,55.8)
    )
}
