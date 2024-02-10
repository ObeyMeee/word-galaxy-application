package ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_OFFSET_X_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_ROTATE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SCALE_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.FLASHCARD_SLIDE_OUT_Y_COEFFICIENT
import ua.com.andromeda.wordgalaxy.ui.RETURN_CARD_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.SWIPE_CARD_BOUND
import ua.com.andromeda.wordgalaxy.ui.screens.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.WordWithTranscriptionOrTranslation
import kotlin.math.abs

private const val TAG = "Flashcard"

@Composable
fun Flashcard(
    embeddedWord: EmbeddedWord,
    flashcardState: FlashcardState,
    screenHeader: @Composable () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
    modifier: Modifier = Modifier,
    menuItems: List<DropdownItemState> = listOf()
) {
    val coroutineScope = rememberCoroutineScope()

    val word = embeddedWord.word
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1

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
        screenHeader()
        AnimatedContent(
            targetState = word,
            label = "FlashcardAnimation",
            transitionSpec = {
                val animationDurationMillis = 800
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
                    ) + slideOut(
                        animationSpec = tween(animationDurationMillis),
                        targetOffset = { fullSize ->
                            val width = fullSize.width
                            val xOffset = if (leftActionClicked) -width else width
                            IntOffset(xOffset, fullSize.height / FLASHCARD_SLIDE_OUT_Y_COEFFICIENT)
                        })
                enterTransition togetherWith exitTransition
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
            )
            {
                FlashcardHeader(
                    squareColor = word.status.iconColor,
                    label = stringResource(word.status.labelRes, numberReview),
                    dropdownItemStates = menuItems,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
                CategoriesText(
                    categories = embeddedWord.categories,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest))
                )
                WordWithTranscriptionOrTranslation(
                    word = it,
                    phonetics = embeddedWord.phonetics,
                    predicate = { it.status == WordStatus.New }
                )
                content()
                FlashcardActionRow(
                    onLeftClick = onLeftClick,
                    onRightClick = onRightClick,
                    actionLabelResLeft = flashcardState.actionLabelResLeft,
                    actionLabelResRight = flashcardState.actionLabelResRight,
                    cardOffset = cardXOffset.value,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CategoriesText(
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    Text(
        text = categories
            .map(Category::name)
            .joinToString(separator = ", "),
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun FlashcardHeader(
    squareColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    dropdownItemStates: List<DropdownItemState> = listOf()
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Square,
            contentDescription = null,
            modifier = Modifier.padding(
                end = dimensionResource(R.dimen.padding_small)
            ),
            tint = squareColor
        )
        Text(text = label, modifier = Modifier.weight(1f))
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = stringResource(R.string.show_more),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .size(32.dp)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(expandFrom = Alignment.Top) { 20 },
            exit = shrinkVertically(animationSpec = tween())
        ) {
            Box {
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    dropdownItemStates.forEach { itemState ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(itemState.labelRes)) },
                            onClick = {
                                itemState.onClick()
                                expanded = false
                                if (itemState.showToast) {
                                    Toast.makeText(
                                        context,
                                        itemState.toastMessageRes,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    painter = itemState.icon,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashcardActionRow(
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    @StringRes actionLabelResLeft: Int,
    @StringRes actionLabelResRight: Int,
    cardOffset: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = onLeftClick,
            icon = Icons.Filled.KeyboardArrowLeft,
            labelRes = actionLabelResLeft,
            active = cardOffset < 0,
            isTextBeforeIcon = true
        )
        ActionButton(
            onClick = onRightClick,
            icon = Icons.Filled.KeyboardArrowRight,
            active = cardOffset > 0,
            labelRes = actionLabelResRight
        )
    }
}

@Composable
private fun RowScope.ActionButton(
    icon: ImageVector,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean,
    isTextBeforeIcon: Boolean = false
) {
    val labelText: @Composable () -> Unit = {
        Text(
            text = stringResource(labelRes),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .height(dimensionResource(R.dimen.action_button_height))
            .weight(1f),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_smaller))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isTextBeforeIcon) {
                labelText()
            }
            Icon(imageVector = icon, contentDescription = null)
            if (!isTextBeforeIcon) {
                labelText()
            }
        }
    }
}

@Preview
@Composable
fun FlashcardNewPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.New({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardInProgressPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.InProgress({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardReviewPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.Review({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardHeaderPreview() {
    Surface {
        MaterialTheme {
            FlashcardHeader(
                squareColor = MaterialTheme.colorScheme.primary,
                label = "Memorized word (review 1)"
            )
        }
    }
}

@Preview
@Composable
fun FlashcardActionRowPreview() {
    val flashcardState = FlashcardState.New({}, {})
    Surface {
        MaterialTheme {
            FlashcardActionRow(
                onLeftClick = flashcardState.onLeftClick,
                onRightClick = flashcardState.onRightClick,
                actionLabelResLeft = flashcardState.actionLabelResLeft,
                actionLabelResRight = flashcardState.actionLabelResRight,
                cardOffset = 0f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}