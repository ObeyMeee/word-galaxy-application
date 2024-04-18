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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.isNew
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ROTATE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SCALE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SLIDE_OUT_Y_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.RETURN_CARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.SWIPE_CARD_BOUND
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScopeInstance.FlashcardActionRow
import kotlin.math.abs

@Composable
fun Flashcard(
    flashcardState: FlashcardState,
    cardMode: CardMode,
    modifier: Modifier = Modifier,
    content: @Composable (FlashcardScope.(ColumnScope) -> Unit),
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
            .graphicsLayer {
                val scale =
                    1 - (abs(cardXOffset.value * FLASHCARD_SCALE_COEFFICIENT) / SWIPE_CARD_BOUND)
                rotationZ = cardXOffset.value * FLASHCARD_ROTATE_COEFFICIENT
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        content(FlashcardScopeInstance, this)
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

@Composable
fun FlashcardScope.FlashcardContent(
    menuExpanded: Boolean,
    cardMode: CardMode,
    userGuess: TextFieldValue,
    amountAttempts: Int,
    viewModel: FlashcardViewModel,
    menuItems: List<DropdownItemState>,
    embeddedWord: EmbeddedWord,
    columnScope: ColumnScope,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val (word, categories, phonetics) = embeddedWord
    val status = word.status
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1
    with(columnScope) {
        Header(
            menuExpanded = menuExpanded,
            onExpandMenu = viewModel::updateMenuExpanded,
            squareColor = status.iconColor,
            label = stringResource(status.labelRes, numberReview),
            dropdownItemStates = menuItems,
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            scope = coroutineScope,
        )
        CategoriesText(
            categories = categories,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest))
        )
        WordWithTranscriptionOrTranslation(
            word = word,
            phonetics = phonetics,
            predicate = { word.isNew },
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_largest),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        )
        CardModeContent(
            embeddedWord = embeddedWord,
            flashcardMode = cardMode,
            updateCardMode = viewModel::updateCardMode,
            userGuess = userGuess,
            updateUserGuess = viewModel::updateUserGuess,
            amountAttempts = amountAttempts,
            checkAnswer = viewModel::checkAnswer,
            revealOneLetter = viewModel::revealOneLetter,
            modifier = Modifier.weight(1f)
        )
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