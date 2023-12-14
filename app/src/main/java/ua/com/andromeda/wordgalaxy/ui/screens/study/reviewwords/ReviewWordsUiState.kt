package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode


sealed interface ReviewWordsUiState {
    data object Default : ReviewWordsUiState

    data class Error(val message: String = "Unexpected error occurred") : ReviewWordsUiState

    data class Success(
        val wordToReview: EmbeddedWord,
        val reviewedToday: Int = 0,
        val cardMode: CardMode = CardMode.Default,
        val userGuess: String = "",
        val amountAttempts: Int = DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
    ) : ReviewWordsUiState
}