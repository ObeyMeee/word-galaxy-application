package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import androidx.compose.ui.text.input.TextFieldValue
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS
import ua.com.andromeda.wordgalaxy.ui.common.CardMode

sealed interface LearnWordsUiState {
    data object Default : LearnWordsUiState
    data class Error(val message: String = "Unexpected error occurred") : LearnWordsUiState
    data class Success(
        val learningWordsQueue: List<EmbeddedWord> = emptyList(),
        val cardMode: CardMode = CardMode.Default,
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val userGuess: TextFieldValue = TextFieldValue(),
        val amountAttempts: Int = DEFAULT_AMOUNT_USER_ATTEMPTS_TO_GUESS,
        val menuExpanded: Boolean = false,
        val wordsInProcessQueue: List<EmbeddedWord> = emptyList(),
    ) : LearnWordsUiState
}