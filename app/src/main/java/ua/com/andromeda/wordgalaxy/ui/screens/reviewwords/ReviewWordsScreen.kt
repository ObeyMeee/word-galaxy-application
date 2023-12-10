package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.utils.playPronunciation
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardState
import ua.com.andromeda.wordgalaxy.ui.screens.common.ReviewMode
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

private const val TAG = "ReviewWordsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ReviewWordsTopAppBar(
                navigateUp = navigateUp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ReviewWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsTopAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { TopAppNavigationBar() },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
private fun TopAppNavigationBar(modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bulb_icon),
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text(text = stringResource(R.string.learn_new_words)) }
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.Green
                )
            },
            label = { Text(text = stringResource(R.string.review_words)) }
        )
    }
}

@Composable
fun ReviewWordsMain(modifier: Modifier = Modifier) {
    val viewModel: ReviewWordsViewModel = viewModel(factory = ReviewWordsViewModel.factory)
    val reviewWordsUiState by viewModel.uiState.collectAsState()
    val cardState = CardState.Review(
        onLeftClick = viewModel::repeatWord,
        onRightClick = viewModel::skipWord
    )

    when (val uiState = reviewWordsUiState) {
        is ReviewWordsUiState.Default -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ReviewWordsUiState.Error -> {
            ErrorMessage(message = uiState.message, modifier = Modifier.fillMaxSize())
        }

        is ReviewWordsUiState.Success -> {
            Column(modifier = modifier) {
                Text(
                    text = stringResource(R.string.words_reviewed, uiState.reviewedToday),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
                LinearProgressIndicator(
                    progress = .35f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                )
                ReviewCard(
                    cardState = cardState,
                    uiState = uiState,
                    updateReviewMode = viewModel::updateReviewMode,
                    updateInputValue = viewModel::updateUserGuess,
                    revealOneLetter = viewModel::revealOneLetter,
                    checkAnswer = viewModel::checkAnswer
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val shape = ShapeDefaults.Large
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
            Text(text = message)
        }
    }
}

@Composable
private fun ReviewCard(
    cardState: CardState,
    uiState: ReviewWordsUiState.Success,
    modifier: Modifier = Modifier,
    updateReviewMode: (ReviewMode) -> Unit = {},
    updateInputValue: (String) -> Unit = {},
    revealOneLetter: () -> Unit = {},
    checkAnswer: () -> Unit = {}
) {
    val wordToReview = uiState.wordToReview
    val word = wordToReview.word
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1

    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        ReviewCardHeader(
            squareColor = cardState.iconColor,
            label = stringResource(cardState.headerLabelRes, numberReview),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        Text(
            text = wordToReview.categories
                .map(Category::name)
                .joinToString(separator = ", "),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = word.translate,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_largest),
                bottom = dimensionResource(R.dimen.padding_medium)
            ),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )
        ReviewModeContent(
            uiState = uiState,
            updateInputValue = updateInputValue,
            checkAnswer = checkAnswer,
            revealOneLetter = revealOneLetter,
            updateReviewMode = updateReviewMode
        )
        Spacer(modifier = Modifier.weight(1f))
        CardAction(
            cardState = cardState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColumnScope.ReviewModeContent(
    uiState: ReviewWordsUiState.Success,
    updateInputValue: (String) -> Unit,
    checkAnswer: () -> Unit,
    revealOneLetter: () -> Unit,
    updateReviewMode: (ReviewMode) -> Unit
) {
    val wordToReview = uiState.wordToReview
    val word = wordToReview.word
    val phonetics = wordToReview.phonetics
    val focusRequester = remember { FocusRequester() }
    when (uiState.reviewMode) {
        ReviewMode.ShowAnswer -> {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
            )
            WordAnswer(
                answer = word.value,
                phonetics = phonetics,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_largest),
                    vertical = dimensionResource(R.dimen.padding_medium)
                )
            )
            ExampleList(wordToReview.examples)
        }

        ReviewMode.TypeAnswer -> {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_larger))
            ) {
                TextField(
                    value = uiState.userGuess,
                    onValueChange = updateInputValue,
                    modifier = Modifier.focusRequester(focusRequester),
                    placeholder = { Text(text = stringResource(R.string.type_here)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions { checkAnswer() },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )

                // autofocus the text field
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                RowWithWordControls(
                    revealOneLetter = revealOneLetter,
                    checkAnswer = checkAnswer,
                    amountAttempts = uiState.amountAttempts,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
            }
        }

        ReviewMode.Default -> {
            Spacer(modifier = Modifier.weight(1f))
            ReviewModeSelectorRow(
                updateReviewMode = updateReviewMode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RowWithWordControls(
    revealOneLetter: () -> Unit,
    checkAnswer: () -> Unit,
    amountAttempts: Int,
    modifier: Modifier = Modifier
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
private fun ReviewCardHeader(
    squareColor: Color,
    label: String,
    modifier: Modifier = Modifier
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
        Icon(
            imageVector = Icons.Filled.MoreHoriz,
            contentDescription = stringResource(R.string.show_more),
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.padding_small))
                .size(32.dp)
        )
    }
}

@Composable
private fun ReviewModeSelectorRow(
    updateReviewMode: (ReviewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReviewModeIconButton(Icons.Default.Keyboard) {
            updateReviewMode(ReviewMode.TypeAnswer)
        }
        ReviewModeIconButton(Icons.Default.RemoveRedEye) {
            updateReviewMode(ReviewMode.ShowAnswer)
        }
    }
}

@Composable
private fun ReviewModeIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_medium))
            )
            .padding(dimensionResource(R.dimen.padding_large))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun ExampleList(
    examples: List<Example>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier
    ) {
        items(examples, key = { it.id }) {
            ExampleItem(it)
        }
    }
}

@Composable
private fun ExampleItem(example: Example, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.expand)
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
}

@Composable
private fun WordAnswer(
    answer: String,
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
                text = answer,
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
fun CardAction(
    cardState: CardState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = cardState.onLeftClick,
            icon = Icons.Filled.KeyboardArrowLeft,
            labelRes = cardState.actionLabelResLeft,
            isTextBeforeIcon = true
        )
        ActionButton(
            onClick = cardState.onRightClick,
            icon = Icons.Filled.KeyboardArrowRight,
            labelRes = cardState.actionLabelResRight
        )
    }
}

@Composable
private fun RowScope.ActionButton(
    icon: ImageVector,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTextBeforeIcon: Boolean = false
) {
    val LabelText: @Composable () -> Unit = {
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isTextBeforeIcon) {
                LabelText()
            }
            Icon(imageVector = icon, contentDescription = null)
            if (!isTextBeforeIcon) {
                LabelText()
            }
        }
    }
}

@Preview
@Composable
fun ReviewWordsTopAppBarPreview() {
    WordGalaxyTheme {
        Surface {
            ReviewWordsTopAppBar(navigateUp = { })
        }
    }
}

@Preview
@Composable
fun ReviewWordsReviewCardDefaultModePreview() {
    WordGalaxyTheme {
        Surface {
            ReviewCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(DefaultStorage.embeddedWord)
            )
        }
    }
}

@Preview
@Composable
fun ReviewWordsReviewCardShowAnswerModePreview() {
    WordGalaxyTheme {
        Surface {
            ReviewCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(
                    wordToReview = DefaultStorage.embeddedWord,
                    reviewMode = ReviewMode.ShowAnswer
                )
            )
        }
    }
}

@Preview
@Composable
fun ReviewWordsReviewCardTypeAnswerModePreview() {
    WordGalaxyTheme {
        Surface {
            ReviewCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(
                    wordToReview = DefaultStorage.embeddedWord,
                    reviewMode = ReviewMode.TypeAnswer
                )
            )
        }
    }
}