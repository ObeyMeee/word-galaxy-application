package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
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
//                Flashcard(
//                    embeddedWord = uiState.wordToReview,
//                    flashcardState = reviewWordCard,
//                ) {
//                    CardModeContent(uiState, viewModel)
//                }
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
//    when (uiState.cardMode) {
//        CardMode.ShowAnswer -> ShowAnswerMode(uiState.wordToReview)
//        CardMode.TypeAnswer -> {
//            TypeAnswerMode(
//                textFieldValue = uiState.userGuess,
//                amountAttempts = uiState.amountAttempts,
//                onValueChanged = viewModel::updateUserGuess,
//                revealOneLetter = viewModel::revealOneLetter,
//                checkAnswer = viewModel::checkAnswer,
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_larger))
//            )
//        }
//
//        CardMode.Default -> {
//            Spacer(modifier = Modifier.weight(1f))
//            CardModeSelectorRow(
//                iconsToCardModes = listOf(
//                    Icons.Default.Keyboard to CardMode.TypeAnswer,
//                    Icons.Default.RemoveRedEye to CardMode.ShowAnswer
//                ),
//                updateCardMode = viewModel::updateCardMode,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//    }
}

@Preview
@Composable
fun HeaderPreview() {
    WordGalaxyTheme {
        Surface {
            Header(0)
        }
    }
}