package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
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

@Composable
fun LearnWordsScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavController = rememberNavController(),
) {
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val amountWordsToReview = (uiState as? LearnWordsUiState.Success)?.amountWordsToReview ?: 0

    Scaffold(
        topBar = {
            val homeRoute = Destination.Start.HomeScreen()
            FlashcardTopBar(
                amountWordsToReview = amountWordsToReview,
                currentRoute = Destination.Study.LearnWordsScreen(),
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
        LearnWordsMain(
            navigateTo = navController::navigate,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LearnWordsMain(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is LearnWordsUiState.Default -> CenteredLoadingSpinner()
        is LearnWordsUiState.Error -> Message(state.message, modifier)
        is LearnWordsUiState.Success -> {
            val embeddedWord = state.learningWordsQueue.first()
            val word = embeddedWord.word
            val wordId = word.id
            val status = word.status
            val isWordStatusNew = status == WordStatus.New
            val amountRepetition = word.amountRepetition ?: 0
            val numberReview = amountRepetition + 1
            val scope = rememberCoroutineScope()
            val flashcardMode = state.cardMode
            val flashcardState = if (isWordStatusNew) {
                FlashcardState.New(
                    onLeftClick = viewModel::alreadyKnowWord,
                    onRightClick = viewModel::startLearningWord
                )
            } else {
                FlashcardState.InProgress(
                    onLeftClick = viewModel::memorizeWord,
                    onRightClick = viewModel::moveToNextWord
                )
            }
            val firstMenuItem = if (isWordStatusNew) {
                DropdownItemState(
                    labelRes = R.string.show_this_word_later,
                    icon = rememberVectorPainter(Icons.Default.SkipNext),
                    onClick = viewModel::moveToNextWord
                )
            } else {
                DropdownItemState(
                    labelRes = R.string.reset_progress_for_this_word,
                    icon = rememberVectorPainter(Icons.Default.Undo),
                    snackbarMessage = stringResource(R.string.progress_has_been_reset_successfully),
                    onClick = viewModel::resetWord,
                    onActionPerformed = viewModel::recoverWord,
                    onDismissAction = viewModel::removeWordFromQueue,
                )
            }
            Log.d("LearnWordsScreen", state.wordsInProcessQueue.size.toString())
            val menuItems = mutableListOf(
                DropdownItemState(
                    labelRes = R.string.copy_to_my_category,
                    icon = rememberVectorPainter(Icons.Default.FolderCopy),
                    snackbarMessage = stringResource(R.string.word_has_been_copied_to_your_category),
                    onClick = viewModel::copyWordToMyCategory,
                    onActionPerformed = viewModel::removeWordFromMyCategory,
                    onDismissAction = viewModel::removeWordFromQueue,
                ),
                DropdownItemState(
                    labelRes = R.string.report_a_mistake,
                    icon = rememberVectorPainter(Icons.Default.Report),
                    onClick = {
                        navigateTo(Destination.ReportMistakeScreen(wordId))
                    },
                ),
                DropdownItemState(
                    labelRes = R.string.edit,
                    icon = rememberVectorPainter(Icons.Default.EditNote),
                    onClick = {
                        navigateTo(Destination.EditWord(wordId))
                    },
                ),
                DropdownItemState(
                    labelRes = R.string.remove,
                    icon = rememberVectorPainter(Icons.Default.Remove),
                    onClick = viewModel::addWordToQueue,
                    // SnackbarDuration.Long == 10 seconds
                    snackbarMessage = stringResource(R.string.word_will_be_removed_in_seconds, 10),
                    onActionPerformed = viewModel::removeWordFromQueue,
                    onDismissAction = viewModel::removeWord,
                )
            )
            menuItems.add(0, firstMenuItem)
            Column(modifier) {
                Header(
                    learnedWordsToday = state.learnedWordsToday,
                    amountWordsLearnPerDay = state.amountWordsLearnPerDay
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
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_medium)),
                        scope = scope,
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
    learnedWordsToday: Int,
    amountWordsLearnPerDay: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
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
}

