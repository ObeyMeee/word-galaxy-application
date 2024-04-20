package ua.com.andromeda.wordgalaxy.study.flashcard.presentation.scope

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import ua.com.andromeda.wordgalaxy.core.data.db.database.DefaultStorage
import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.presentation.components.DropdownItemState
import ua.com.andromeda.wordgalaxy.study.flashcard.domain.CardMode
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

interface FlashcardScope {
    @Composable
    fun Header(
        menuExpanded: Boolean,
        onExpandMenu: (Boolean) -> Unit,
        squareColor: Color,
        label: String,
        snackbarHostState: SnackbarHostState,
        dropdownItemStates: List<DropdownItemState>,
        modifier: Modifier,
        scope: CoroutineScope,
    )

    @Composable
    fun TypeAnswerMode(
        textFieldValue: TextFieldValue,
        isWrongInput: Boolean,
        onValueChanged: (TextFieldValue) -> Unit,
        amountAttempts: Int,
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun DefaultMode(
        modesOptions: List<CardMode>,
        onModeClicked: (CardMode) -> Unit,
        modifier: Modifier
    )

    @Composable
    fun ShowAnswerMode(
        embeddedWord: EmbeddedWord,
        modifier: Modifier,
    )

    @Composable
    fun CategoriesText(
        categories: List<Category>,
        modifier: Modifier
    )

    @Composable
    fun WordWithTranscriptionOrTranslation(
        word: Word,
        phonetics: List<Phonetic>,
        predicate: () -> Boolean,
        modifier: Modifier,
    )
}

@Composable
fun FlashcardScope.CardModeContent(
    embeddedWord: EmbeddedWord,
    isWrongInput: Boolean,
    flashcardMode: CardMode,
    updateCardMode: (CardMode) -> Unit,
    userGuess: TextFieldValue,
    updateUserGuess: (TextFieldValue) -> Unit,
    revealOneLetter: () -> Unit,
    checkAnswer: () -> Unit,
    amountAttempts: Int,
    modifier: Modifier = Modifier,
) {
    val isWordNew = embeddedWord.word.status == WordStatus.New

    AnimatedContent(
        targetState = flashcardMode,
        label = "CardModeAnimation",
        modifier = modifier,
        transitionSpec = {
            val enterTransition = fadeIn() + slideInVertically { -it }
            val exitTransition = fadeOut() + slideOutVertically { it }
            enterTransition togetherWith exitTransition
        },
    ) { cardMode ->
        val commonModifier = Modifier.fillMaxWidth()
        when (cardMode) {
            CardMode.ShowAnswer -> {
                ShowAnswerMode(embeddedWord, commonModifier)
            }

            CardMode.TypeAnswer -> {
                TypeAnswerMode(
                    textFieldValue = userGuess,
                    isWrongInput = isWrongInput,
                    amountAttempts = amountAttempts,
                    onValueChanged = updateUserGuess,
                    revealOneLetter = revealOneLetter,
                    checkAnswer = checkAnswer,
                    modifier = commonModifier,
                )
            }

            CardMode.Default -> {
                val modesOptions = mutableListOf(CardMode.ShowAnswer)
                if (!isWordNew) {
                    modesOptions.add(0, CardMode.TypeAnswer)
                }
                DefaultMode(
                    modesOptions = modesOptions,
                    onModeClicked = updateCardMode,
                    modifier = commonModifier,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultCardModeContentPreview() {
    WordGalaxyTheme {
        Surface {
            FlashcardScopeInstance.CardModeContent(
                embeddedWord = DefaultStorage.embeddedWord,
                isWrongInput = false,
                flashcardMode = CardMode.Default,
                updateCardMode = {},
                userGuess = TextFieldValue(""),
                updateUserGuess = {},
                revealOneLetter = {},
                checkAnswer = {},
                amountAttempts = 0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TypeAnswerCardModeContentPreview() {
    WordGalaxyTheme {
        Surface {
            FlashcardScopeInstance.CardModeContent(
                embeddedWord = DefaultStorage.embeddedWord,
                isWrongInput = false,
                flashcardMode = CardMode.TypeAnswer,
                updateCardMode = {},
                userGuess = TextFieldValue(""),
                updateUserGuess = {},
                revealOneLetter = {},
                checkAnswer = {},
                amountAttempts = 0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowAnswerCardModeContentPreview() {
    WordGalaxyTheme {
        Surface {
            FlashcardScopeInstance.CardModeContent(
                embeddedWord = DefaultStorage.embeddedWord,
                isWrongInput = false,
                flashcardMode = CardMode.ShowAnswer,
                updateCardMode = {},
                userGuess = TextFieldValue(""),
                updateUserGuess = {},
                revealOneLetter = {},
                checkAnswer = {},
                amountAttempts = 0
            )
        }
    }
}
