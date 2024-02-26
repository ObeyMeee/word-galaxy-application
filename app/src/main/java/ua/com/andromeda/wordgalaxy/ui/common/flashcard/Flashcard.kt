package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_OFFSET_X_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ROTATE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SCALE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SLIDE_OUT_Y_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.RETURN_CARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.SWIPE_CARD_BOUND
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScopeInstance.FlashcardActionRow
import kotlin.math.abs

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun Flashcard(
    embeddedWord: EmbeddedWord,
    flashcardState: FlashcardState,
    cardMode: CardMode,
    modifier: Modifier = Modifier,
    content: @Composable (FlashcardScope.() -> Unit),
) {
    val coroutineScope = rememberCoroutineScope()
    var leftActionClicked by remember { mutableStateOf(false) }
    val cardXOffset = remember { Animatable(0f) }
    val cardYOffset = remember { Animatable(0f) }
    val onLeftClick = {
        leftActionClicked = true
        flashcardState.onLeftClick()
    }
    val onRightClick = {
        leftActionClicked = false
        flashcardState.onRightClick()
    }

    Column(modifier = modifier) {
        AnimatedContent(
            targetState = embeddedWord.word.id,
            label = "FlashcardAnimation",
            transitionSpec = {
                flashcardTransitionSpec(leftActionClicked)
            }
        ) {
            var lastDragX = 0f
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(onDragEnd = {
                            when {
                                // Swipe right
                                lastDragX > SWIPE_CARD_BOUND && cardXOffset.value > SWIPE_CARD_BOUND ->
                                    onRightClick()

                                // Swipe left
                                lastDragX < -SWIPE_CARD_BOUND && cardXOffset.value < -SWIPE_CARD_BOUND ->
                                    onLeftClick()

                            }
                            coroutineScope.launch {
                                cardXOffset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(RETURN_CARD_ANIMATION_DURATION_MILLIS)
                                )
                                cardYOffset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(RETURN_CARD_ANIMATION_DURATION_MILLIS)
                                )
                            }
                        }) { change, dragAmount ->
                            change.consume()
                            lastDragX = dragAmount.x
                            coroutineScope.launch {
                                cardXOffset.snapTo(cardXOffset.value + lastDragX * FLASHCARD_OFFSET_X_COEFFICIENT)
                            }
                        }
                    }
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
    }
}

private fun flashcardTransitionSpec(leftActionClicked: Boolean): ContentTransform {
    val animationDurationMillis = 700
    val enterTransition = scaleIn(
        animationSpec = tween(animationDurationMillis),
        initialScale = .3f
    ) + slideInVertically(
        animationSpec = tween(animationDurationMillis),
        initialOffsetY = { -it }
    )
    val exitTransition =
        scaleOut(
            animationSpec = tween(animationDurationMillis),
            targetScale = .3f
        ) + slideOut(
            animationSpec = tween(animationDurationMillis),
            targetOffset = { fullSize ->
                val width = fullSize.width
                val xOffset = if (leftActionClicked) -width else width
                IntOffset(xOffset, fullSize.height / FLASHCARD_SLIDE_OUT_Y_COEFFICIENT)
            })
    return enterTransition togetherWith exitTransition
}
