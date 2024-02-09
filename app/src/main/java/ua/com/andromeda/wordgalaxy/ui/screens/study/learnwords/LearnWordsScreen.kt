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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.DropdownItemState
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.Flashcard
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.CardModeSelectorRow
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.ExampleList
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.RowWithWordControls
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardScope.WordWithTranscriptionOrTranslation
import ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard.FlashcardState
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

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
    val viewModel: LearnWordsViewModel = hiltViewModel()
    val learnWordsUiState by viewModel.uiState.collectAsState()
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
            onClick = viewModel::reportMistake
        ),
        DropdownItemState(
            labelRes = R.string.edit,
            onClick = viewModel::edit,
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
            val word = uiState.embeddedWord.word
            val isWordStatusNew = word.status == WordStatus.New
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
            Flashcard(
                embeddedWord = uiState.embeddedWord,
                flashcardState = flashcardState,
                menuItems = menuItems,
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
private fun ColumnScope.CardModeContent(
    uiState: LearnWordsUiState.Success,
    viewModel: LearnWordsViewModel = hiltViewModel(),
) {
    val wordToLearn = uiState.embeddedWord
    val isWordNew = wordToLearn.word.status == WordStatus.New

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
                ShowAnswerCardMode(wordToLearn)
            }

            CardMode.TypeAnswer -> {
                val checkAnswer = viewModel::checkAnswer
                TypeAnswerCardMode(
                    textFieldValue = uiState.userGuess,
                    amountAttempts = uiState.amountAttempts,
                    onValueChanged = viewModel::updateUserGuess,
                    keyboardAction = checkAnswer,
                    revealOneLetter = viewModel::revealOneLetter,
                    checkAnswer = checkAnswer,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_larger))
                )
            }

            CardMode.Default -> {
                DefaultCardMode(
                    isWordNew = isWordNew,
                    updateCardMode = viewModel::updateCardMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
//}

@Composable
private fun ColumnScope.ShowAnswerCardMode(embeddedWord: EmbeddedWord) {
    val (word, _, phonetics, examples) = embeddedWord
    Divider()
    WordWithTranscriptionOrTranslation(
        word = word,
        phonetics = phonetics,
        predicate = { word.status != WordStatus.New }
    )
    Divider()
    ExampleList(
        examples = examples,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun TypeAnswerCardMode(
    textFieldValue: TextFieldValue,
    amountAttempts: Int,
    onValueChanged: (TextFieldValue) -> Unit,
    keyboardAction: () -> Unit,
    revealOneLetter: () -> Unit,
    checkAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        TextField(
            value = textFieldValue,
            onValueChange = onValueChanged,
            modifier = Modifier.focusRequester(focusRequester),
            placeholder = { Text(text = stringResource(R.string.type_here)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { keyboardAction() },
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

@Composable
private fun ColumnScope.DefaultCardMode(
    isWordNew: Boolean,
    updateCardMode: (CardMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconsToCardModes = mutableListOf(
        Icons.Default.RemoveRedEye to CardMode.ShowAnswer
    )
    if (!isWordNew) {
        iconsToCardModes.add(0, Icons.Default.Keyboard to CardMode.TypeAnswer)
    }
    Spacer(modifier = Modifier.weight(1f))
    CardModeSelectorRow(
        iconsToCardModes = iconsToCardModes,
        updateCardMode = updateCardMode,
        modifier = modifier
    )
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
private fun Divider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    )
}

@Preview(showBackground = true)
@Composable
fun CardModeContentPreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                CardModeContent(
                    uiState = LearnWordsUiState.Success(
                        embeddedWord = DefaultStorage.embeddedWord
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun TypeAnswerCardModePreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                TypeAnswerCardMode(
                    textFieldValue = TextFieldValue(),
                    amountAttempts = 3,
                    onValueChanged = { _ -> },
                    keyboardAction = {},
                    revealOneLetter = {},
                    checkAnswer = {}
                )
            }
        }
    }
}

@Preview
@Composable
fun DefaultCardModePreview() {
    WordGalaxyTheme {
        Surface {
            Column {
                DefaultCardMode(
                    isWordNew = false,
                    updateCardMode = { _ -> },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}