package com.hollow.vpn.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Nothing motion: `ease-in-out`, ~200ms for controls, ~400ms for transitions.
 * No spring, no rebound, no scale-pops. Press feedback = opacity drop (.8 hover / .6 pressed).
 */
object NothingMotion {
    /** CSS ease-in-out. */
    val FluidEase = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

    const val ControlMs = 200
    const val TransitionMs = 400

    fun <T> control(): AnimationSpec<T> = tween(ControlMs, easing = FluidEase)
    fun <T> transition(): AnimationSpec<T> = tween(TransitionMs, easing = FluidEase)

    // The old spring entry points now resolve to the same tween so every call site stays rebound-free.
    fun <T> snappySpring(): AnimationSpec<T> = control()
    fun <T> tactileSpring(): AnimationSpec<T> = control()
}

/** Pressed = opacity .6, idle = 1. (Replaces the old scale-bounce press effect.) */
fun Modifier.nothingPressScale(
    interactionSource: InteractionSource,
    @Suppress("UNUSED_PARAMETER") pressedScale: Float = 1f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val a by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 1f,
        animationSpec = NothingMotion.control(),
        label = "nothing_press_opacity"
    )
    this.graphicsLayer { alpha = a }
}
