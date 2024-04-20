package ua.com.andromeda.wordgalaxy.study.learnwords.presentation

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
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.isNew
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.DropdownItemState
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.core.presentation.components.getCommonMenuItems
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.Flashcard
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardContent
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardState
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardUiState
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.SwipeDirection
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.components.FlashcardTopBar
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.flashcardTransitionSpec

@Composable
fun LearnWordsScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavController = rememberNavController(),
) {
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val amountWordsToReview = (uiState as? FlashcardUiState.Success)?.amountWordsToReview ?: 0

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
        is FlashcardUiState.Default -> CenteredLoadingSpinner()
        is FlashcardUiState.Error -> Message(state.message, modifier)
        is FlashcardUiState.Success -> {
            val learningWord = state.memorizingWordsQueue.firstOrNull() ?: return
            val coroutineScope = rememberCoroutineScope()
            var swipeDirection by remember { mutableStateOf(SwipeDirection.None) }

            DisposableEffect(learningWord) {
                swipeDirection = SwipeDirection.None
                onDispose { }
            }
            Column(modifier) {
                Header(
                    learnedWordsToday = state.learnedWordsToday,
                    amountWordsLearnPerDay = state.amountWordsLearnPerDay
                )
                AnimatedContent(
                    targetState = learningWord,
                    label = "FlashcardAnimation",
                    transitionSpec = { flashcardTransitionSpec(swipeDirection) },
                ) { targetState ->
                    val word = targetState.word
                    val flashcardState = if (word.isNew) {
                        FlashcardState.New(
                            onLeftClick = {
                                viewModel.updateWordStatus(WordStatus.AlreadyKnown)
                                swipeDirection = SwipeDirection.Left
                            },
                            onRightClick = {
                                viewModel.updateWordStatus(WordStatus.InProgress)
                                swipeDirection = SwipeDirection.Right
                            }
                        )
                    } else {
                        FlashcardState.InProgress(
                            onLeftClick = {
                                viewModel.memorizeWord()
                                swipeDirection = SwipeDirection.Left
                            },
                            onRightClick = {
                                viewModel.moveToNextWord()
                                swipeDirection = SwipeDirection.Right
                            }
                        )
                    }
                    val menuItems = getMenuItems(
                        word = word,
                        navigateTo = navigateTo,
                        viewModel = viewModel,
                    )
                    Flashcard(
                        cardMode = state.cardMode,
                        flashcardState = flashcardState,
                    ) { columnScope ->
                        FlashcardContent(
                            menuExpanded = state.menuExpanded,
                            isWrongInput = state.isWrongInput,
                            cardMode = state.cardMode,
                            userGuess = state.userGuess,
                            amountAttempts = state.amountAttempts,
                            viewModel = viewModel,
                            menuItems = menuItems,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            columnScope = columnScope,
                            embeddedWord = targetState,
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
    val firstMenuItem = if (word.isNew) {
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

