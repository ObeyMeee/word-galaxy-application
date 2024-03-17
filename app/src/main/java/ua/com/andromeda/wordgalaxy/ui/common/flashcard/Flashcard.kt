package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ROTATE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SCALE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SLIDE_OUT_Y_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.RETURN_CARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.SWIPE_CARD_BOUND
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScopeInstance.FlashcardActionRow
import kotlin.math.abs

@Composable
fun Flashcard(
    flashcardState: FlashcardState,
    cardMode: CardMode,
    modifier: Modifier = Modifier,
    content: @Composable (FlashcardScope.() -> Unit),
) {
    val scope = rememberCoroutineScope()
    val cardXOffset = remember { Animatable(0f) }
    val cardYOffset = remember { Animatable(0f) }
    var lastDragX = 0f

    Card(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    lastDragX = delta
                    scope.launch {
                        cardXOffset.snapTo(cardXOffset.value + delta)
                    }
                },
                onDragStopped = {
                    scope.launch {
                        when {
                            lastDragX > SWIPE_CARD_BOUND && cardXOffset.value > SWIPE_CARD_BOUND ->
                                flashcardState.onRightClick()

                            lastDragX < -SWIPE_CARD_BOUND && cardXOffset.value < -SWIPE_CARD_BOUND ->
                                flashcardState.onLeftClick()
                        }
                        cardXOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(RETURN_CARD_ANIMATION_DURATION_MILLIS)
                        )
                        cardYOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(RETURN_CARD_ANIMATION_DURATION_MILLIS)
                        )
                    }
                }
            )
            .offset { IntOffset(cardXOffset.value.toInt(), cardYOffset.value.toInt()) }
            .scale(1 - (abs(cardXOffset.value * FLASHCARD_SCALE_COEFFICIENT) / SWIPE_CARD_BOUND))
            .rotate(cardXOffset.value * FLASHCARD_ROTATE_COEFFICIENT),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        content(FlashcardScopeInstance)
        AnimatedVisibility(
            visible = cardMode != CardMode.TypeAnswer,
        ) {
            FlashcardActionRow(
                onLeftClick = flashcardState.onLeftClick,
                onRightClick = flashcardState.onRightClick,
                actionLabelResLeft = flashcardState.actionLabelResLeft,
                actionLabelResRight = flashcardState.actionLabelResRight,
                cardOffset = cardXOffset.value,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}

enum class SwipeDirection {
    None, Left, Right
}

fun flashcardTransitionSpec(swipeTo: SwipeDirection): ContentTransform {
    val floatAnimationSpec = tween<Float>(durationMillis = FLASHCARD_ANIMATION_DURATION_MILLIS)
    val intOffsetAnimationSpec =
        tween<IntOffset>(durationMillis = FLASHCARD_ANIMATION_DURATION_MILLIS)
    val enterTransition = scaleIn(floatAnimationSpec, initialScale = .3f) +
            slideInVertically(intOffsetAnimationSpec) { -it }

    val exitTransition = scaleOut(floatAnimationSpec, targetScale = .3f) +
            slideOut(intOffsetAnimationSpec) { fullSize ->
                val width = fullSize.width
                val xOffset = when (swipeTo) {
                    SwipeDirection.None -> 0
                    SwipeDirection.Left -> -width
                    SwipeDirection.Right -> width
                }
                IntOffset(xOffset, fullSize.height / FLASHCARD_SLIDE_OUT_Y_COEFFICIENT)
            }
    return enterTransition togetherWith exitTransition
}