package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

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
            // TODO:
            val embeddedWord = state.wordToReview
            val scope = rememberCoroutineScope()
            var swipeDirection by remember { mutableStateOf(SwipeDirection.None) }

            DisposableEffect(embeddedWord) {
                swipeDirection = SwipeDirection.None
                onDispose { }
            }

            Column(modifier) {
                Header(
                    reviewedWordsToday = state.reviewedToday,
                    amountWordsToReview = state.amountWordsToReview
                )
                AnimatedContent(
                    targetState = embeddedWord,
                    label = "FlashcardAnimation",
                    transitionSpec = { flashcardTransitionSpec(swipeDirection) },
                ) {
                    val word = it.word
                    val flashcardMode = state.cardMode
                    val status = word.status
                    val isWordStatusNew = status == WordStatus.New
                    val amountRepetition = word.amountRepetition ?: 0
                    val numberReview = amountRepetition + 1
                    val flashcardState = FlashcardState.Review(
                        onLeftClick = {
                            viewModel.repeatWord()
                            swipeDirection = SwipeDirection.Left
                        },
                        onRightClick = {
                            viewModel.skipWord()
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
                            wordId = word.id,
                            navigateTo = navigateTo,
                            viewModel = viewModel,
                        ).toTypedArray()
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
                            word = word,
                            phonetics = it.phonetics,
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
                            modifier = Modifier.weight(1f),
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