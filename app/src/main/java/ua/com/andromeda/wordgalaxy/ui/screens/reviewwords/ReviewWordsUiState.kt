package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.screens.common.ReviewMode

sealed interface ReviewWordsUiState {

    data object Default : ReviewWordsUiState

    data class Error(val message: String) : ReviewWordsUiState

    data class Success(
        val wordToReview: EmbeddedWord,
        val reviewedToday: Int = 0,
        val reviewMode: ReviewMode = ReviewMode.Default,
        val userGuess: String = ""
    ) : ReviewWordsUiState
}