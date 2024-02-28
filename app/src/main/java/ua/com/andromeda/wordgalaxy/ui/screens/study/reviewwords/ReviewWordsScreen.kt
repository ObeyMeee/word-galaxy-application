package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.CardModeContent
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.Flashcard
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
    val navigateTo = { route: String -> navController.navigate(route) }

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
                navigateTo = navigateTo,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ReviewWordsMain(
            navigateTo = navigateTo,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun ReviewWordsMain(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReviewWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ReviewWordsUiState.Default -> CenteredLoadingSpinner(modifier)
        is ReviewWordsUiState.Error -> {
            Message(
                message = state.message,
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
            val embeddedWord = state.wordToReview
            val word = embeddedWord.word
            val wordId = word.id
            val status = word.status
            val isWordStatusNew = status == WordStatus.New
            val amountRepetition = word.amountRepetition ?: 0
            val numberReview = amountRepetition + 1
            val flashcardMode = state.cardMode
            val flashcardState = FlashcardState.Review(
                onLeftClick = viewModel::repeatWord,
                onRightClick = viewModel::skipWord
            )
            val menuItems = listOf(
                DropdownItemState(
                    labelRes = R.string.reset_progress_for_this_word,
                    icon = rememberVectorPainter(Icons.Default.Undo),
                    toastMessageRes = R.string.progress_has_been_reset_successfully,
                    onClick = viewModel::resetWord
                ),
                DropdownItemState(
                    labelRes = R.string.copy_to_my_category,
                    icon = rememberVectorPainter(Icons.Default.FolderCopy),
                    toastMessageRes = R.string.word_has_been_copied_to_your_category,
                    onClick = viewModel::copyWordToMyCategory
                ),
                DropdownItemState(
                    labelRes = R.string.report_a_mistake,
                    icon = rememberVectorPainter(Icons.Default.Report),
                    showToast = false,
                    onClick = {
                        navigateTo(Destination.ReportMistakeScreen(wordId))
                    },
                ),
                DropdownItemState(
                    labelRes = R.string.edit,
                    onClick = {
                        navigateTo(Destination.EditWord(wordId))
                    },
                    showToast = false,
                    icon = rememberVectorPainter(Icons.Default.EditNote),
                ),
                DropdownItemState(
                    labelRes = R.string.remove,
                    onClick = viewModel::removeWord,
                    toastMessageRes = R.string.word_has_been_successfully_removed,
                    icon = rememberVectorPainter(Icons.Default.Remove),
                )
            )
            Column(modifier) {
                Header(
                    reviewedWordsToday = state.reviewedToday,
                    amountWordsToReview = state.amountWordsToReview
                )
                Flashcard(
                    embeddedWord = embeddedWord,
                    cardMode = flashcardMode,
                    flashcardState = flashcardState,
                ) {
                    Header(
                        menuExpanded = state.menuExpanded,
                        onExpandMenu = viewModel::updateMenuExpanded,
                        squareColor = status.iconColor,
                        label = stringResource(status.labelRes, numberReview),
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
                        word = word,
                        phonetics = embeddedWord.phonetics,
                        predicate = { isWordStatusNew },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.padding_largest),
                            vertical = dimensionResource(R.dimen.padding_small)
                        ),
                    )
                    CardModeContent(
                        embeddedWord = embeddedWord,
                        flashcardMode = flashcardMode,
                        updateCardMode = viewModel::updateCardMode,
                        userGuess = state.userGuess,
                        updateUserGuess = viewModel::updateUserGuess,
                        amountAttempts = state.amountAttempts,
                        checkAnswer = viewModel::checkAnswer,
                        revealOneLetter = viewModel::revealOneLetter,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    reviewedWordsToday: Int,
    amountWordsToReview: Int,
    modifier: Modifier = Modifier,
) {
    val progress = reviewedWordsToday.toFloat() / (amountWordsToReview + reviewedWordsToday)

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
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Preview
@Composable
fun HeaderPreview() {
    WordGalaxyTheme {
        Surface {
            Header(4, 2)
        }
    }
}