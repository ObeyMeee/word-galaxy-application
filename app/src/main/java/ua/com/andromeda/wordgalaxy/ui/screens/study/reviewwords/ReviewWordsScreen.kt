package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.Flashcard
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScope.CardModeSelectorRow
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScope.ExampleList
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScope.RowWithWordControls
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScope.WordWithTranscription
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardTopBar
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun ReviewWordsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val viewModel: ReviewWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            val homeRoute = Destination.Start.HomeScreen()
            FlashcardTopBar(
                amountWordsToReview = (uiState as? ReviewWordsUiState.Success)?.amountWordsToReview
                    ?: 0,
                currentRoute = Destination.Study.ReviewWordsScreen(),
                navigateUp = {
                    navController.navigate(homeRoute) {
                        popUpTo(homeRoute) {
                            inclusive = true
                        }
                    }
                },
                navigateTo = navController::navigate,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ReviewWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ReviewWordsMain(modifier: Modifier = Modifier) {
    val viewModel: ReviewWordsViewModel = hiltViewModel()
    val reviewWordsUiState by viewModel.uiState.collectAsState()

    when (val uiState = reviewWordsUiState) {
        is ReviewWordsUiState.Default -> CenteredLoadingSpinner(modifier)
        is ReviewWordsUiState.Error -> {
            Message(
                message = uiState.message,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        is ReviewWordsUiState.Success -> {
            val reviewWordCard = FlashcardState.Review(
                onLeftClick = viewModel::repeatWord,
                onRightClick = viewModel::skipWord
            )
            Column(modifier) {
                Header(uiState.reviewedToday)
                Flashcard(
                    embeddedWord = uiState.wordToReview,
                    flashcardState = reviewWordCard,
                ) {
                    CardModeContent(uiState, viewModel)
                }
            }
        }
    }
}

@Composable
private fun Header(
    reviewedWordsToday: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = pluralStringResource(
                R.plurals.words_reviewed,
                reviewedWordsToday,
                reviewedWordsToday
            ),
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
    }
}

@Composable
private fun ColumnScope.CardModeContent(
    uiState: ReviewWordsUiState.Success,
    viewModel: ReviewWordsViewModel
) {
    when (uiState.cardMode) {
        CardMode.ShowAnswer -> {
            ShowAnswerContent(uiState.wordToReview)
        }

        CardMode.TypeAnswer -> {
            TextFieldWithControls(
                userGuess = uiState.userGuess,
                amountAttempts = uiState.amountAttempts,
                updateUserGuess = viewModel::updateUserGuess,
                checkAnswer = viewModel::checkAnswer,
                revealOneLetter = viewModel::revealOneLetter,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }

        CardMode.Default -> {
            Spacer(modifier = Modifier.weight(1f))
            CardModeSelectorRow(
                iconsToCardModes = listOf(
                    Icons.Default.Keyboard to CardMode.TypeAnswer,
                    Icons.Default.RemoveRedEye to CardMode.ShowAnswer
                ),
                updateCardMode = viewModel::updateCardMode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShowAnswerContent(wordToReview: EmbeddedWord) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    )
    WordWithTranscription(
        value = wordToReview.word.value,
        phonetics = wordToReview.phonetics,
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.padding_largest),
            vertical = dimensionResource(R.dimen.padding_medium)
        )
    )
    ExampleList(wordToReview.examples)
}

@Composable
private fun TextFieldWithControls(
    userGuess: String,
    amountAttempts: Int,
    updateUserGuess: (String) -> Unit,
    revealOneLetter: () -> Unit,
    checkAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        TextField(
            value = userGuess,
            onValueChange = updateUserGuess,
            modifier = Modifier.focusRequester(focusRequester),
            placeholder = { Text(text = stringResource(R.string.type_here)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { checkAnswer() },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
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
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Preview
@Composable
fun ScreenHeaderPreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                Header(0)
            }
        }
    }
}

@Preview
@Composable
fun TextFieldWithControlsPreview() {
    WordGalaxyTheme {
        Surface {
            TextFieldWithControls(userGuess = "captain", amountAttempts = 2, {}, {}, {})
        }
    }
}

@Preview
@Composable
fun ShowAnswerContentPreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                ShowAnswerContent(DefaultStorage.embeddedWord)
            }
        }
    }
}
