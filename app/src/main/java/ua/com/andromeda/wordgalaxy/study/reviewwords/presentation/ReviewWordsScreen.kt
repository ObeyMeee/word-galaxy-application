package ua.com.andromeda.wordgalaxy.study.reviewwords.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
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
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun ReviewWordsScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavController = rememberNavController()
) {
    val viewModel: ReviewWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navigateTo = { route: String -> navController.navigate(route) }

    Scaffold(
        topBar = {
            val homeRoute = Destination.Start.HomeScreen()
            FlashcardTopBar(
                amountWordsToReview = (uiState as? FlashcardUiState.Success)?.amountWordsToReview
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
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun ReviewWordsMain(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val viewModel: ReviewWordsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is FlashcardUiState.Default -> CenteredLoadingSpinner(modifier)
        is FlashcardUiState.Error -> {
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

        is FlashcardUiState.Success -> {
            val reviewedWord = state.memorizingWordsQueue.firstOrNull() ?: return
            val coroutineScope = rememberCoroutineScope()
            val amountReviewedWordsToday = viewModel.amountReviewedWordsToday.collectAsState()
            var swipeDirection by remember { mutableStateOf(SwipeDirection.None) }

            DisposableEffect(reviewedWord) {
                swipeDirection = SwipeDirection.None
                onDispose { }
            }
            Column(modifier) {
                Header(
                    reviewedWordsToday = amountReviewedWordsToday.value,
                    amountWordsToReview = state.amountWordsToReview
                )
                AnimatedContent(
                    targetState = reviewedWord,
                    label = "FlashcardAnimation",
                    transitionSpec = { flashcardTransitionSpec(swipeDirection) },
                ) {
                    val flashcardState = FlashcardState.Review(
                        onLeftClick = {
                            viewModel.repeatWord()
                            swipeDirection = SwipeDirection.Left
                        },
                        onRightClick = {
                            viewModel.moveToNextWord()
                            swipeDirection = SwipeDirection.Right
                        }
                    )

                    val menuItems = listOf(
                        DropdownItemState(
                            labelRes = R.string.reset_progress_for_this_word,
                            icon = rememberVectorPainter(Icons.Default.Undo),
                            snackbarMessage = stringResource(R.string.progress_has_been_reset_successfully),
                            onClick = viewModel::resetWord
                        ),
                        *getCommonMenuItems(
                            wordId = it.word.id,
                            navigateTo = navigateTo,
                            viewModel = viewModel,
                        ).toTypedArray()
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
                            columnScope = columnScope,
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            embeddedWord = it,
                        )
                    }
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