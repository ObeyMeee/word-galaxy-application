package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.CardModeContent
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.Flashcard
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardTopBar
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.SwipeDirection
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.flashcardTransitionSpec
import ua.com.andromeda.wordgalaxy.ui.common.getCommonMenuItems
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
            val embeddedWord = state.learningWordsQueue.firstOrNull() ?: return
            val scope = rememberCoroutineScope()
            var swipeDirection by remember { mutableStateOf(SwipeDirection.None) }

            DisposableEffect(embeddedWord) {
                swipeDirection = SwipeDirection.None
                onDispose { }
            }
            Column(modifier) {
                Header(
                    learnedWordsToday = state.learnedWordsToday,
                    amountWordsLearnPerDay = state.amountWordsLearnPerDay
                )
                AnimatedContent(
                    targetState = embeddedWord,
                    label = "FlashcardAnimation",
                    transitionSpec = { flashcardTransitionSpec(swipeDirection) },
                ) {
                    val word = embeddedWord.word
                    val flashcardMode = state.cardMode
                    val status = it.word.status
                    val isWordStatusNew = status == WordStatus.New
                    val amountRepetition = word.amountRepetition ?: 0
                    val numberReview = amountRepetition + 1
                    val flashcardState = if (isWordStatusNew) {
                        FlashcardState.New(
                            onLeftClick = {
                                viewModel.alreadyKnowWord()
                                swipeDirection = SwipeDirection.Left
                            },
                            onRightClick = {
                                viewModel.startLearningWord()
                                swipeDirection = SwipeDirection.Right
                            }
                        )
                    } else {
                        FlashcardState.InProgress(
                            onLeftClick = viewModel::memorizeWord,
                            onRightClick = viewModel::moveToNextWord
                        )
                    }
                    val menuItems = getMenuItems(
                        word = word,
                        navigateTo = navigateTo,
                        viewModel = viewModel,
                    )
                    Flashcard(
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
                            categories = it.categories,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest))
                        )
                        WordWithTranscriptionOrTranslation(
                            word = it.word,
                            phonetics = embeddedWord.phonetics,
                            predicate = { isWordStatusNew },
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(R.dimen.padding_largest),
                                vertical = dimensionResource(R.dimen.padding_small)
                            ),
                        )
                        CardModeContent(
                            embeddedWord = it,
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
}

@Composable
private fun getMenuItems(
    word: Word,
    navigateTo: (String) -> Unit,
    viewModel: LearnWordsViewModel,
): List<DropdownItemState> {
    val firstMenuItem = if (word.status == WordStatus.New) {
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
    return listOf(
        firstMenuItem,
        *getCommonMenuItems(
            wordId = word.id,
            navigateTo = navigateTo,
            viewModel = viewModel,
        ).toTypedArray()
    )
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

