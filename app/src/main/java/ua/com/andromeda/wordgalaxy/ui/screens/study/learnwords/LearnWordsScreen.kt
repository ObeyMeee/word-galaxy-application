package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.Flashcard
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardScope
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardTopBar
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

@Composable
fun LearnWordsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val amountWordsToReview = getAmountWordsToReview(uiState)

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
            modifier = Modifier.padding(innerPadding)
        )
    }
}

fun getAmountWordsToReview(uiState: LearnWordsUiState) =
    when (uiState) {
        is LearnWordsUiState.Success -> uiState.amountWordsToReview
        else -> 0
    }


@Composable
fun LearnWordsMain(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is LearnWordsUiState.Default -> CenteredLoadingSpinner()
        is LearnWordsUiState.Error -> Message(state.message, modifier)
        is LearnWordsUiState.Success -> {
            val embeddedWord = state.embeddedWord
            val word = embeddedWord.word
            val wordId = word.id
            val status = word.status
            val isWordStatusNew = status == WordStatus.New
            val amountRepetition = word.amountRepetition ?: 0
            val numberReview = amountRepetition + 1
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
                    learnedWordsToday = state.learnedWordsToday,
                    amountWordsLearnPerDay = state.amountWordsLearnPerDay
                )
                Flashcard(
                    embeddedWord = embeddedWord,
                    cardMode = state.cardMode,
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
                    )
                    CardModeContent(
                        state = state,
                        viewModel = viewModel,
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

@Composable
private fun FlashcardScope.CardModeContent(
    state: LearnWordsUiState.Success,
    modifier: Modifier = Modifier,
    viewModel: LearnWordsViewModel = hiltViewModel(),
) {
    val wordToLearn = state.embeddedWord
    val isWordNew = wordToLearn.word.status == WordStatus.New

    AnimatedContent(
        targetState = state.cardMode,
        label = "CardModeAnimation",
        modifier = modifier,
        transitionSpec = {
            (fadeIn() + slideInVertically { -it }) togetherWith
                    (fadeOut() + slideOutVertically { it })
        },
    ) { cardMode ->
        val commonModifier = Modifier.fillMaxWidth()
        when (cardMode) {
            CardMode.ShowAnswer -> {
                ShowAnswerMode(wordToLearn, commonModifier)
            }

            CardMode.TypeAnswer -> {
                TypeAnswerMode(
                    textFieldValue = state.userGuess,
                    amountAttempts = state.amountAttempts,
                    onValueChanged = viewModel::updateUserGuess,
                    revealOneLetter = viewModel::revealOneLetter,
                    checkAnswer = viewModel::checkAnswer,
                    modifier = commonModifier,
                )
            }

            CardMode.Default -> {
                DefaultMode(
                    isWordNew = isWordNew,
                    updateCardMode = viewModel::updateCardMode,
                    modifier = commonModifier,
                )
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun CardModeContentPreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                CardModeContent(
                    state = LearnWordsUiState.Success(
                        embeddedWord = DefaultStorage.embeddedWord
                    )
                )
            }
        }
    }
}*/
