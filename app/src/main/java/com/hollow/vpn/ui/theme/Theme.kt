package com.hollow.vpn.ui.theme

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

fun detectOledDisplay(context: Context): Boolean {
    return try {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
        } ?: return false

        val hasHdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            display.hdrCapabilities?.supportedHdrTypes?.isNotEmpty() == true
        } else {
            false
        }
        val hasWideGamut = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            display.isWideColorGamut
        } else {
            false
        }
        hasHdr || hasWideGamut
    } catch (_: Exception) {
        false
    }
}

@Composable
fun HollowVpnTheme(
    darkTheme: Boolean = true,
    oledDark: Boolean = true,
    dynamicColor: Boolean = false,
    displayScalePercent: Int = HollowDisplay.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        !darkTheme -> ExpressiveLightColorScheme
        oledDark -> ExpressiveDarkColorScheme
        else -> ExpressiveCarbonDarkColorScheme
    }
    val hollowPalette = when {
        !darkTheme -> LightHollowPalette
        oledDark -> DarkHollowPalette
        else -> CarbonDarkHollowPalette
    }

    HollowDesignScale(displayScalePercent) {
        CompositionLocalProvider(
            LocalHollowPalette provides hollowPalette,
            LocalHollowIsDark provides darkTheme,
            LocalHollowIsOled provides (darkTheme && oledDark)
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                shapes = ExpressiveShapes,
                content = content
            )
        }
    }
}
