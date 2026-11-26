package com.hollow.vpn.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Land as a dot matrix. [xyz] holds one unit vector per dot (x, y, z), [country] the index of the
 * country each dot belongs to in [codes].
 */
class DotField(
    val xyz: FloatArray,
    val country: ShortArray,
    val n: Int,
    val codes: Array<String>,
    val spacingDeg: Float,
)

object DotGeo {
    /** Angular distance between neighbouring dots. Smaller = denser. */
    const val SPACING_DEG = 0.7f

    private const val MASK_W = 2048
    private const val MASK_H = 1024
    private const val LAT_MIN = -58.0
    private const val LAT_MAX = 84.0

    @Volatile var cached: DotField? = null

    suspend fun load(ctx: Context): DotField? {
        cached?.let { return it }
        val countries = WorldGeo.load(ctx)
        if (countries.isEmpty()) return null
        return withContext(Dispatchers.Default) {
            runCatching { build(countries) }.getOrNull()?.also { cached = it }
        }
    }

    private fun build(countries: List<Country>): DotField {
        // 1) Paint every country into an equirectangular mask, colour = country index + 1.
        val bmp = Bitmap.createBitmap(MASK_W, MASK_H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply { isAntiAlias = false; isDither = false; style = Paint.Style.FILL }
        val path = Path().apply { fillType = Path.FillType.EVEN_ODD }
        countries.forEachIndexed { idx, c ->
            paint.color = (0xFF shl 24) or (idx + 1)
            // Draw three copies so shapes that cross the antimeridian wrap around correctly.
            for (shift in -1..1) {
                path.rewind()
                for (ring in c.rings) addRing(path, ring, shift * MASK_W.toFloat())
                canvas.drawPath(path, paint)
            }
        }
        val px = IntArray(MASK_W * MASK_H)
        bmp.getPixels(px, 0, MASK_W, 0, 0, MASK_W, MASK_H)
        bmp.recycle()

        // 2) Lay an even dot grid over the sphere (rows of latitude, fewer dots toward the poles)
        //    and keep the dots that land on a country.
        val s = SPACING_DEG.toDouble()
        val rows = (180.0 / s).toInt()
        var capacity = 0
        for (i in 0 until rows) {
            val lat = -90.0 + (i + 0.5) * s
            capacity += max(1, (360.0 * cos(Math.toRadians(lat)) / s).roundToInt())
        }
        capacity += countries.size
        val xyz = FloatArray(capacity * 3)
        val owner = ShortArray(capacity)
        val hasDot = BooleanArray(countries.size)
        var n = 0

        fun put(lonDeg: Double, latDeg: Double, countryIdx: Int) {
            val lon = Math.toRadians(lonDeg); val lat = Math.toRadians(latDeg)
            xyz[n * 3] = (cos(lat) * cos(lon)).toFloat()
            xyz[n * 3 + 1] = (cos(lat) * sin(lon)).toFloat()
            xyz[n * 3 + 2] = sin(lat).toFloat()
            owner[n] = countryIdx.toShort()
            hasDot[countryIdx] = true
            n++
        }

        for (i in 0 until rows) {
            val lat = -90.0 + (i + 0.5) * s
            if (lat < LAT_MIN || lat > LAT_MAX) continue
            val nLon = max(1, (360.0 * cos(Math.toRadians(lat)) / s).roundToInt())
            val stagger = if ((i and 1) == 1) 0.5 else 0.0
            val my = (((90.0 - lat) / 180.0) * MASK_H).toInt().coerceIn(0, MASK_H - 1)
            for (j in 0 until nLon) {
                var lon = -180.0 + (j + 0.5 + stagger) * 360.0 / nLon
                if (lon > 180.0) lon -= 360.0
                val mx = (((lon + 180.0) / 360.0) * MASK_W).toInt().coerceIn(0, MASK_W - 1)
                val v = px[my * MASK_W + mx] and 0xFFFFFF
                if (v == 0) continue
                put(lon, lat, v - 1)
            }
        }

        // 3) Tiny countries and islands can fall between grid dots. Give each one a dot at its centre
        //    so it can still light up when it's the active server.
        countries.forEachIndexed { idx, c ->
            if (hasDot[idx] || c.code == "--") return@forEachIndexed
            val ring = c.rings.maxByOrNull { it.size } ?: return@forEachIndexed
            var x0 = Float.MAX_VALUE; var x1 = -Float.MAX_VALUE
            var y0 = Float.MAX_VALUE; var y1 = -Float.MAX_VALUE
            var k = 0
            while (k < ring.size) {
                if (ring[k] < x0) x0 = ring[k]; if (ring[k] > x1) x1 = ring[k]
                if (ring[k + 1] < y0) y0 = ring[k + 1]; if (ring[k + 1] > y1) y1 = ring[k + 1]
                k += 2
            }
            put(((x0 + x1) / 2f).toDouble(), ((y0 + y1) / 2f).toDouble(), idx)
        }

        return DotField(
            xyz = xyz.copyOf(n * 3),
            country = owner.copyOf(n),
            n = n,
            codes = Array(countries.size) { countries[it].code },
            spacingDeg = SPACING_DEG,
        )
    }

    /** Adds [ring] (lon/lat pairs) to [path] in mask pixels, unwrapping jumps across the antimeridian. */
    private fun addRing(path: Path, ring: FloatArray, shiftPx: Float) {
        val count = ring.size / 2
        if (count < 3) return
        var prev = ring[0]
        for (i in 0 until count) {
            var lon = ring[i * 2]
            while (lon - prev > 180f) lon -= 360f
            while (lon - prev < -180f) lon += 360f
            prev = lon
            val x = (lon + 180f) / 360f * MASK_W + shiftPx
            val y = (90f - ring[i * 2 + 1]) / 180f * MASK_H
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }
}
