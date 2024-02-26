package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.Divider
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

interface FlashcardScope {
    @Composable
    fun Header(
        menuExpanded: Boolean,
        onExpandMenu: (Boolean) -> Unit,
        squareColor: Color,
        label: String,
        dropdownItemStates: List<DropdownItemState>,
        modifier: Modifier,
    )

    @Composable
    fun TypeAnswerMode(
        textFieldValue: TextFieldValue,
        onValueChanged: (TextFieldValue) -> Unit,
        amountAttempts: Int,
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun DefaultMode(
        isWordNew: Boolean,
        updateCardMode: (CardMode) -> Unit,
        modifier: Modifier
    )

    @Composable
    fun ShowAnswerMode(
        embeddedWord: EmbeddedWord,
        modifier: Modifier,
    )

    @Composable
    fun CategoriesText(
        categories: List<Category>,
        modifier: Modifier
    )

    @Composable
    fun WordWithTranscriptionOrTranslation(
        word: Word,
        phonetics: List<Phonetic>,
        predicate: () -> Boolean,
    )
}

internal object FlashcardScopeInstance : FlashcardScope {
    @Composable
    override fun Header(
        menuExpanded: Boolean,
        onExpandMenu: (Boolean) -> Unit,
        squareColor: Color,
        label: String,
        dropdownItemStates: List<DropdownItemState>,
        modifier: Modifier
    ) {
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
            IconButton(onClick = { onExpandMenu(true) }) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = stringResource(R.string.show_more),
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_small))
                        .size(dimensionResource(R.dimen.icon_size_large))
                )
            }
            Menu(
                expanded = menuExpanded,
                onExpand = onExpandMenu,
                dropdownItemStates = dropdownItemStates
            )
        }
    }

    @Composable
    private fun Menu(
        expanded: Boolean,
        onExpand: (Boolean) -> Unit,
        dropdownItemStates: List<DropdownItemState>,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
            modifier = modifier,
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpand(false) }
            ) {
                dropdownItemStates.forEach { itemState ->
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(itemState.labelRes))
                        },
                        onClick = {
                            itemState.onClick()
                            onExpand(false)
                            if (itemState.showToast) {
                                // TODO: change to snackbar
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

    @Composable
    override fun TypeAnswerMode(
        textFieldValue: TextFieldValue,
        onValueChanged: (TextFieldValue) -> Unit,
        amountAttempts: Int,
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        modifier: Modifier,
    ) {
        val focusRequester = remember { FocusRequester() }
        val transparent = Color.Transparent

        Column(
            modifier = modifier.padding(
                dimensionResource(R.dimen.padding_larger)
            )
        ) {
            TextField(
                value = textFieldValue,
                onValueChange = onValueChanged,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text(text = stringResource(R.string.type_here)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions { checkAnswer() },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = transparent,
                    unfocusedContainerColor = transparent,
                    disabledContainerColor = transparent,
                )
            )

            // autofocus the text field
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            RowWithWordControls(
                revealOneLetter = revealOneLetter,
                checkAnswer = checkAnswer,
                amountAttempts = amountAttempts,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small)
                )
            )
        }
    }

    @Composable
    override fun DefaultMode(
        isWordNew: Boolean,
        updateCardMode: (CardMode) -> Unit,
        modifier: Modifier
    ) {
        val iconsToCardModes = mutableListOf(
            Icons.Default.RemoveRedEye to CardMode.ShowAnswer
        )
        if (!isWordNew) {
            iconsToCardModes.add(0, Icons.Default.Keyboard to CardMode.TypeAnswer)
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
        ) {
            ModesRow(
                iconsToCardModes = iconsToCardModes,
                onModeClicked = updateCardMode,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Composable
    private fun ModesRow(
        iconsToCardModes: List<Pair<ImageVector, CardMode>>,
        onModeClicked: (CardMode) -> Unit,
        modifier: Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconsToCardModes.forEach { (icon, cardMode) ->
                IconButton(
                    onClick = { onModeClicked(cardMode) },
                    modifier = Modifier
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            shape = MaterialTheme.shapes.large
                        )
                        .size(dimensionResource(R.dimen.card_mode_icon))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                    )
                }
            }
        }
    }

    @Composable
    override fun ShowAnswerMode(
        embeddedWord: EmbeddedWord,
        modifier: Modifier,
    ) {
        val (word, _, phonetics, examples) = embeddedWord
        Column(modifier) {
            Divider()
            WordWithTranscriptionOrTranslation(
                word = word,
                phonetics = phonetics,
                predicate = { word.status != WordStatus.New }
            )
            Divider()
            ExampleList(examples, Modifier)
        }
    }

    @Composable
    override fun WordWithTranscriptionOrTranslation(
        word: Word,
        phonetics: List<Phonetic>,
        predicate: () -> Boolean,
    ) {
        if (predicate()) {
            WordWithTranscription(
                value = word.value,
                phonetics = phonetics,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_largest),
                    vertical = dimensionResource(R.dimen.padding_small)
                )
            )
        } else {
            Text(
                text = word.translation,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .heightIn(min = 0.dp, max = 120.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_largest),
                        vertical = dimensionResource(R.dimen.padding_small)
                    )
            )
        }
    }

    @Composable
    private fun WordWithTranscription(
        value: String,
        phonetics: List<Phonetic>,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Row(
            modifier = modifier.clickable {
                context.playPronunciation(audioUrls = phonetics.map { it.audio })
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = phonetics.joinToString(separator = ", ") { it.text },
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun ExampleList(
        examples: List<Example>,
        modifier: Modifier
    ) {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
        ) {
            items(examples, key = Example::id) {
                ExampleItem(it)
            }
        }
    }

    @Composable
    private fun ExampleItem(
        example: Example,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        val rotationAngle by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "ExpandExampleAnimation"
        )

        Row(
            modifier = modifier.clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expand),
                modifier = Modifier.rotate(rotationAngle)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
            Text(
                text = example.text,
                modifier = Modifier.weight(.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Outlined.PlayCircleOutline,
                contentDescription = stringResource(R.string.play_example)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkOut() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                Text(
                    text = example.translation,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    @Composable
    private fun RowWithWordControls(
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier
    ) {
        Row(modifier = modifier) {
            RevealOneLetterOutlinedButton(onClick = revealOneLetter)
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
            CheckAnswerButton(
                onclick = checkAnswer,
                amountAttempts = amountAttempts,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun RevealOneLetterOutlinedButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_smaller))
        ) {
            Icon(
                imageVector = Icons.Default.QuestionMark,
                contentDescription = null
            )
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun CheckAnswerButton(
        onclick: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = onclick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            BadgedBox(badge = {
                Badge {
                    Text(
                        text = amountAttempts.toString(),
                        modifier = Modifier.semantics {
                            contentDescription = "$amountAttempts amount attempts left"
                        }
                    )
                }
            }) {
                Text(
                    text = stringResource(R.string.check),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    override fun CategoriesText(
        categories: List<Category>,
        modifier: Modifier
    ) {
        Text(
            text = categories
                .map(Category::name)
                .joinToString(),
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall
        )
    }

    @Composable
    fun FlashcardActionRow(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
        @StringRes actionLabelResLeft: Int,
        @StringRes actionLabelResRight: Int,
        cardOffset: Float,
        modifier: Modifier
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
}


//@Preview
//@Composable
//fun FlashcardNewPreview() {
//    Surface {
//        MaterialTheme {
//            Flashcard(
//                embeddedWord = DefaultStorage.embeddedWord,
//                flashcardState = FlashcardState.New({}, {}),
//                content = {}
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun FlashcardInProgressPreview() {
//    Surface {
//        MaterialTheme {
//            Flashcard(
//                embeddedWord = DefaultStorage.embeddedWord,
//                flashcardState = FlashcardState.InProgress({}, {}),
//                content = {}
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun FlashcardReviewPreview() {
//    Surface {
//        MaterialTheme {
//            Flashcard(
//                embeddedWord = DefaultStorage.embeddedWord,
//                flashcardState = FlashcardState.Review({}, {}),
//                content = {}
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun FlashcardHeaderPreview() {
//    Surface {
//        MaterialTheme {
//            FlashcardHeader(
//                squareColor = MaterialTheme.colorScheme.primary,
//                label = "Memorized word (review 1)"
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun FlashcardActionRowPreview() {
//    val flashcardState = FlashcardState.New({}, {})
//    Surface {
//        MaterialTheme {
//            FlashcardActionRow(
//                onLeftClick = flashcardState.onLeftClick,
//                onRightClick = flashcardState.onRightClick,
//                actionLabelResLeft = flashcardState.actionLabelResLeft,
//                actionLabelResRight = flashcardState.actionLabelResRight,
//                cardOffset = 0f,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//    }
//}

@Preview
@Composable
fun ModesRowPreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.DefaultMode(
                isWordNew = false,
                updateCardMode = { _ -> },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}