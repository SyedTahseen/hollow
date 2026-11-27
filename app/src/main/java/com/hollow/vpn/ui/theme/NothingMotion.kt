package com.hollow.vpn.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

object NothingMotion {
    val FluidEase = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    fun <T> snappySpring() = spring<T>(
        dampingRatio = 0.80f,
        stiffness = 500f
    )

    fun <T> tactileSpring() = spring<T>(
        dampingRatio = 0.62f,
        stiffness = 560f
    )
}

fun Modifier.nothingPressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.965f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = if (isPressed) {
            tween(durationMillis = 85, easing = NothingMotion.FluidEase)
        } else {
            NothingMotion.tactileSpring()
        },
        label = "nothing_press_scale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
