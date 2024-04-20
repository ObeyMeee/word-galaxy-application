package ua.com.andromeda.wordgalaxy.study.flashcard.presentation

import androidx.compose.ui.text.input.TextFieldValue
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.study.flashcard.DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
import ua.com.andromeda.wordgalaxy.study.flashcard.domain.CardMode

sealed interface FlashcardUiState {
    data object Default : FlashcardUiState
    data class Error(val message: String = "Unexpected error occurred") : FlashcardUiState

    data class Success(
        val memorizingWordsQueue: List<EmbeddedWord> = emptyList(),
        val cardMode: CardMode = CardMode.Default,
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val userGuess: TextFieldValue = TextFieldValue(),
        val amountAttempts: Int = DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS,
        val menuExpanded: Boolean = false,
        val wordsInProcessQueue: List<EmbeddedWord> = emptyList(),
    ) : FlashcardUiState
}

fun FlashcardUiState.Success.correctAnswer() =
    this.copy(
        cardMode = CardMode.ShowAnswer,
        amountAttempts = DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS,
        userGuess = TextFieldValue(),
    )