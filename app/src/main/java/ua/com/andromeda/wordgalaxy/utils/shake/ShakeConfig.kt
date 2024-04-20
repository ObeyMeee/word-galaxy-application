package ua.com.andromeda.wordgalaxy.utils.shake

import androidx.compose.animation.core.Spring

data class ShakeConfig(
    val iterations: Int,
    val intensity: Float = Spring.StiffnessMedium,
    val rotate: Float = 0f,
    val rotateX: Float = 0f,
    val rotateY: Float = 0f,
    val scaleX: Float = 0f,
    val scaleY: Float = 0f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
    val trigger: Long = System.currentTimeMillis(),
)