package ua.com.andromeda.wordgalaxy.utils.shake

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

fun Modifier.shake(shakeConfig: ShakeConfig) = composed {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(shakeConfig) {
        for (i in 0..shakeConfig.iterations) {
            val target = if (i % 2 == 0) 1f else -1f
            shake.animateTo(target, spring(stiffness = shakeConfig.intensity))
        }
        shake.animateTo(0f)
    }

    this
        .rotate(shake.value * shakeConfig.rotate)
        .graphicsLayer {
            rotationX = shake.value * shakeConfig.rotateX
            rotationY = shake.value * shakeConfig.rotateY
        }
        .scale(
            scaleX = 1f + (shake.value * shakeConfig.scaleX),
            scaleY = 1f + (shake.value * shakeConfig.scaleY),
        )
        .offset {
            IntOffset(
                (shake.value * shakeConfig.translateX).roundToInt(),
                (shake.value * shakeConfig.translateY).roundToInt(),
            )
        }
}