package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.Category
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
        title = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
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
        },
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
            Box(
                modifier = Modifier.fillMaxSize(),
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
                    Text(text = uiState.message)
                }
            }
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
                    progress = .35f, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                )
                EnglishCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnglishCard(
    cardState: CardState,
    uiState: ReviewWordsUiState.Success,
    modifier: Modifier = Modifier,
    updateReviewMode: (ReviewMode) -> Unit = {},
    updateInputValue: (String) -> Unit = {},
    revealOneLetter: () -> Unit = {},
    checkAnswer: () -> Unit = {}
) {
    val context = LocalContext.current
    val wordToReview = uiState.wordToReview
    val word = wordToReview.word
    val phonetics = wordToReview.phonetics
    val focusRequester = remember { FocusRequester() }

    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_small))
            ) {
                Icon(
                    imageVector = Icons.Filled.Square,
                    contentDescription = null,
                    modifier = Modifier.padding(
                        end = dimensionResource(R.dimen.padding_small)
                    ),
                    tint = cardState.iconColor
                )
                val amountRepetition = word.amountRepetition ?: 0
                Text(text = stringResource(cardState.headerLabelRes, amountRepetition + 1))
            }
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = stringResource(R.string.show_more),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .size(32.dp)
            )
        }
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
        when (uiState.reviewMode) {
            ReviewMode.ShowAnswer -> {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                )
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_largest),
                            vertical = dimensionResource(R.dimen.padding_medium)
                        )
                        .clickable {
                            context.playPronunciation(audioUrls = phonetics.map { it.audio })
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(.8f)) {
                        Text(
                            text = word.value,
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
                LazyColumn(
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    items(wordToReview.examples, key = { it.id }) { example ->
                        Row(
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
                }
                Spacer(modifier = Modifier.weight(1f))
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
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    Row(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                    ) {
                        OutlinedButton(
                            onClick = revealOneLetter,
                            shape = MaterialTheme.shapes.small,
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_smaller))
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionMark,
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                        Button(
                            onClick = { checkAnswer() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Text(text = stringResource(R.string.check, uiState.amountAttempts))
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            ReviewMode.Default -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            updateReviewMode(ReviewMode.TypeAnswer)
                        },
                        modifier = Modifier
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
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            updateReviewMode(ReviewMode.ShowAnswer)
                        },
                        modifier = Modifier
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
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        CardAction(
            cardState = cardState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CardAction(
    cardState: CardState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = cardState.onLeftClick,
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_small)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(cardState.actionLabelResLeft))
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Button(
            onClick = cardState.onRightClick,
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_small)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(cardState.actionLabelResRight),
                    )
                }
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
fun ReviewWordsEnglishCardDefaultModePreview() {
    WordGalaxyTheme {
        Surface {
            EnglishCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(DefaultStorage.embeddedWord)
            )
        }
    }
}

@Preview
@Composable
fun ReviewWordsEnglishCardShowAnswerModePreview() {
    WordGalaxyTheme {
        Surface {
            EnglishCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(
                    wordToReview = DefaultStorage.embeddedWord,
                    reviewMode = ReviewMode.ShowAnswer
                )
            )
        }
    }
}