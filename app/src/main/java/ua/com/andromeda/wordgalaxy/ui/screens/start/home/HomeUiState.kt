package ua.com.andromeda.wordgalaxy.ui.screens.start.home

import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_TIME_PERIOD

sealed interface HomeUiState {
    data object Default : HomeUiState
    data class Error(val message: String = "Unexpected error occurred") : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val amountWordsToLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val timePeriod: Int = DEFAULT_TIME_PERIOD,
        val listOfWordsCountOfStatus: List<Map<WordStatus, Int>> = listOf()
    ) : HomeUiState
}