package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QuestionMark
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import androidx.lifecycle.asFlow
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.chillibits.simplesettings.tool.getPreferenceLiveData
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.Divider
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

internal object FlashcardScopeInstance : FlashcardScope {
    @Composable
    override fun Header(
        menuExpanded: Boolean,
        onExpandMenu: (Boolean) -> Unit,
        squareColor: Color,
        label: String,
        snackbarHostState: SnackbarHostState,
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
                snackbarHostState = snackbarHostState,
                dropdownItemStates = dropdownItemStates,
            )
        }
    }

    @Composable
    private fun Menu(
        expanded: Boolean,
        onExpand: (Boolean) -> Unit,
        dropdownItemStates: List<DropdownItemState>,
        snackbarHostState: SnackbarHostState,
        modifier: Modifier = Modifier,
    ) {
        val actionLabel = stringResource(R.string.undo)
        val scope = rememberCoroutineScope()

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
                            itemState.snackbarMessage?.let { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = actionLabel,
                                        duration = SnackbarDuration.Long
                                    )
                                }
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
    override fun DefaultMode(
        modesOptions: List<CardMode>,
        onModeClicked: (CardMode) -> Unit,
        modifier: Modifier,
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            modesOptions.forEach {
                ModeItem(
                    cardMode = it,
                    onClick = { onModeClicked(it) },
                    modifier = Modifier
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = MaterialTheme.shapes.large
                        )
                        .size(dimensionResource(R.dimen.card_mode_icon)),
                )
            }
        }
    }

    @Composable
    private fun ModeItem(
        cardMode: CardMode,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
        ) {
            cardMode.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(cardMode.labelRes),
                    modifier = Modifier.size(
                        dimensionResource(R.dimen.icon_size_large)
                    ),
                )
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
                predicate = { word.status != WordStatus.New },
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_largest),
                    vertical = dimensionResource(R.dimen.padding_small)
                )
            )
            Divider()
            ExampleList(
                items = examples,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Composable
    override fun WordWithTranscriptionOrTranslation(
        word: Word,
        phonetics: List<Phonetic>,
        predicate: () -> Boolean,
        modifier: Modifier
    ) {
        if (predicate()) {
            val context = LocalContext.current
            WordWithTranscription(
                value = word.value,
                transcription = phonetics.joinToString { it.text },
                playPronunciation = {
                    context.playPronunciation(phonetics)
                },
                modifier = modifier,
            )
        } else {
            Text(
                text = word.translation,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
                    .heightIn(min = 0.dp, max = 120.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }

    @Composable
    private fun WordWithTranscription(
        value: String,
        transcription: String,
        playPronunciation: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current
        val isAutomaticallyPronounceEnglishWords =
            getPreferenceLiveData(context, "automatically_pronounce_english_words", true).asFlow()
        LaunchedEffect(Unit) {
            isAutomaticallyPronounceEnglishWords.collect {
                if (it) {
                    playPronunciation()
                }
            }
        }

        val transcriptionEnabled = context.getPrefBooleanValue("transcriptions_enabled")
        Row(
            modifier = modifier.clickable(onClick = playPronunciation),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge
                )
                if (transcriptionEnabled) {
                    Text(
                        text = transcription,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = stringResource(R.string.play_pronunciation),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun ExampleList(
        items: List<Example>,
        modifier: Modifier
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = dimensionResource(R.dimen.padding_medium)
            ),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            ),
            modifier = modifier
        ) {
            items(items, key = Example::id) {
                ExampleItem(it, modifier = Modifier.fillMaxWidth())
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
            animationSpec = spring(),
            label = "ExpandExampleAnimation"
        )
        Column(modifier) {
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand),
                    modifier = Modifier.rotate(rotationAngle)
                )
                HorizontalSpacer(R.dimen.padding_small)
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
            ExampleTranslation(
                visible = expanded,
                text = example.translation,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        vertical = dimensionResource(R.dimen.padding_mediumish),
                        horizontal = dimensionResource(R.dimen.padding_large)
                    ),
            )
        }
    }

    @Composable
    private fun ColumnScope.ExampleTranslation(
        visible: Boolean,
        text: String,
        modifier: Modifier = Modifier,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkOut() + fadeOut(),
            modifier = modifier,
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
            )
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
    private fun RowWithWordControls(
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier
    ) {
        Row(modifier = modifier) {
            RevealLetterOutlinedButton(onClick = revealOneLetter)
            HorizontalSpacer(R.dimen.padding_large)
            CheckAnswerButton(
                onClick = checkAnswer,
                amountAttempts = amountAttempts,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun RevealLetterOutlinedButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        shape: Shape = MaterialTheme.shapes.small,
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
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
        onClick: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier = Modifier,
        shape: Shape = MaterialTheme.shapes.small,
    ) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = shape
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
        modifier: Modifier,
        cardOffset: Float = 0f,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                onClick = onLeftClick,
                active = cardOffset < 0,
            ) {
                ActionButtonText(actionLabelResLeft)
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = null)
            }
            ActionButton(
                onClick = onRightClick,
                active = cardOffset > 0,
            ) {
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
                ActionButtonText(actionLabelResRight)
            }
        }
    }

    @Composable
    private fun RowScope.ActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        active: Boolean,
        content: @Composable RowScope.() -> Unit
    ) {
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
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_smaller)),
            content = content
        )
    }

    @Composable
    private fun RowScope.ActionButtonText(@StringRes textRes: Int) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun HeaderPreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.Header(
                menuExpanded = false,
                onExpandMenu = {},
                squareColor = MaterialTheme.colorScheme.primary,
                label = stringResource(R.string.learning_new_word),
                dropdownItemStates = emptyList(),
                snackbarHostState = SnackbarHostState(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun ModesRowPreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.DefaultMode(
                modesOptions = listOf(CardMode.ShowAnswer, CardMode.TypeAnswer),
                onModeClicked = { _ -> },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun ShowAnswerModePreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.ShowAnswerMode(
                embeddedWord = DefaultStorage.embeddedWord,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun FlashcardActionRowPreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.FlashcardActionRow(
                onLeftClick = {},
                onRightClick = {},
                actionLabelResLeft = R.string.i_have_memorized_this_word,
                actionLabelResRight = R.string.keep_learning_this_word,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun TypeAnswerModePreview() {
    MaterialTheme {
        Surface {
            FlashcardScopeInstance.TypeAnswerMode(
                textFieldValue = TextFieldValue("check"),
                onValueChanged = {},
                amountAttempts = 1,
                revealOneLetter = {},
                checkAnswer = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}