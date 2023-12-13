package ua.com.andromeda.wordgalaxy.ui.screens.learnwords

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode

sealed interface LearnWordsUiState {
    data object Default : LearnWordsUiState
    data class Error(val message: String = "Unexpected error occurred") : LearnWordsUiState
    data class Success(
        val embeddedWord: EmbeddedWord,
        val cardMode: CardMode = CardMode.Default,
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val userGuess: String = "",
        val amountAttempts: Int = DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
    ) : LearnWordsUiState
}