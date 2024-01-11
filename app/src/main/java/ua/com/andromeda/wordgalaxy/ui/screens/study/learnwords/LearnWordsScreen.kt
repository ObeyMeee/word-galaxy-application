package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.Flashcard
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.CardModeSelectorRow
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.ExampleList
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.RowWithWordControls
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.WordWithTranscriptionOrTranslation
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardState
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnWordsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val viewModel: LearnWordsViewModel = viewModel(factory = LearnWordsViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()
    val amountWordsToReview = getAmountWordsToReview(uiState)

    Scaffold(
        topBar = {
            val homeRoute = Destination.Start.HomeScreen()
            LearnWordsTopAppBar(
                amountWordsToReview = amountWordsToReview,
                navigateUp = {
                    navController.navigate(homeRoute) {
                        popUpTo(homeRoute) {
                            inclusive = true
                        }
                    }
                },
                navigateToReviewWords = {
                    navController.navigate(Destination.Study.ReviewWordsScreen())
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LearnWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

fun getAmountWordsToReview(uiState: LearnWordsUiState) =
    when (uiState) {
        is LearnWordsUiState.Success -> uiState.amountWordsToReview
        else -> 0
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearnWordsTopAppBar(
    amountWordsToReview: Int,
    navigateUp: () -> Unit,
    navigateToReviewWords: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            TopAppNavigationBar(navigateToReviewWords, amountWordsToReview)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppNavigationBar(
    navigateToReviewWords: () -> Unit,
    amountWordsToReview: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = true,
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
            selected = false,
            onClick = navigateToReviewWords,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.Green
                )
            },
            label = {
                BadgedBox(badge = {
                    Badge {
                        Text(text = amountWordsToReview.toString())
                    }
                }) {
                    Text(text = stringResource(R.string.review_words))
                }
            }
        )
    }
}

@Composable
fun LearnWordsMain(modifier: Modifier = Modifier) {
    val viewModel: LearnWordsViewModel = viewModel(factory = LearnWordsViewModel.factory)
    val learnWordsUiState by viewModel.uiState.collectAsState()

    when (val uiState = learnWordsUiState) {
        is LearnWordsUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is LearnWordsUiState.Error -> {
            Message(
                message = uiState.message,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        is LearnWordsUiState.Success -> {
            val isWordStatusNew = uiState.embeddedWord.word.status == WordStatus.New
            val flashcardState = if (isWordStatusNew) {
                FlashcardState.New(
                    onLeftClick = viewModel::alreadyKnowWord,
                    onRightClick = viewModel::startLearningWord
                )
            } else {
                FlashcardState.InProgress(
                    onLeftClick = viewModel::memorizeWord,
                    onRightClick = viewModel::skipWord
                )
            }
            Flashcard(
                embeddedWord = uiState.embeddedWord,
                flashcardState = flashcardState,
                screenHeader = {
                    ScreenHeader(
                        learnedWordsToday = uiState.learnedWordsToday,
                        amountWordsLearnPerDay = uiState.amountWordsLearnPerDay
                    )
                },
                content = {
                    CardModeContent(uiState, viewModel)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ScreenHeader(
    learnedWordsToday: Int,
    amountWordsLearnPerDay: Int
) {
    Text(
        text = pluralStringResource(
            R.plurals.new_words_memorized,
            learnedWordsToday,
            learnedWordsToday
        ),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.padding_small))
    ) {
        (1..learnedWordsToday).forEach { _ ->
            Icon(imageVector = Icons.Filled.Rectangle, contentDescription = null)
        }

        (learnedWordsToday..<amountWordsLearnPerDay).forEach { _ ->
            Icon(imageVector = Icons.Outlined.Rectangle, contentDescription = null)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColumnScope.CardModeContent(
    uiState: LearnWordsUiState.Success,
    viewModel: LearnWordsViewModel,
) {
    val wordToReview = uiState.embeddedWord
    val word = wordToReview.word
    val phonetics = wordToReview.phonetics
    val isWordNew = word.status == WordStatus.New
    val focusRequester = remember { FocusRequester() }

    // TODO:
//    AnimatedContent(
//        targetState = uiState.cardMode,
//        label = "CardModeAnimation",
//        transitionSpec = {
//            (fadeIn() + slideInVertically { -it }) togetherWith
//                    (fadeOut() + slideOutVertically { it })
//        }
//    ) { cardMode ->
    when (uiState.cardMode) {
        CardMode.ShowAnswer -> {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
            )
            WordWithTranscriptionOrTranslation(
                word = word,
                phonetics = phonetics,
                predicate = { !isWordNew }
            )
            ExampleList(
                wordToReview.examples,
                modifier = Modifier.weight(1f)
            )
        }

        CardMode.TypeAnswer -> {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_larger))
            ) {
                TextField(
                    value = uiState.userGuess,
                    onValueChange = viewModel::updateUserGuess,
                    modifier = Modifier.focusRequester(focusRequester),
                    placeholder = { Text(text = stringResource(R.string.type_here)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions { viewModel.checkAnswer() },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )

                // autofocus the text field
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                RowWithWordControls(
                    revealOneLetter = viewModel::revealOneLetter,
                    checkAnswer = viewModel::checkAnswer,
                    amountAttempts = uiState.amountAttempts,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
            }
        }

        CardMode.Default -> {
            val iconsToCardModes = mutableListOf(
                Icons.Default.RemoveRedEye to CardMode.ShowAnswer
            )
            if (!isWordNew) {
                iconsToCardModes.add(0, Icons.Default.Keyboard to CardMode.TypeAnswer)
            }
            Spacer(modifier = Modifier.weight(1f))
            CardModeSelectorRow(
                iconsToCardModes = iconsToCardModes,
                updateCardMode = viewModel::updateCardMode,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
//}

@Preview(showBackground = true)
@Composable
fun BrowseCardsScreenPreview() {
    WordGalaxyTheme {
        LearnWordsScreen(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}