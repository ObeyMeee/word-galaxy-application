package ua.com.andromeda.wordgalaxy.home.presentation

import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.home.presentation.components.TimePeriodChartOptions
import ua.com.andromeda.wordgalaxy.study.flashcard.DEFAULT_TIME_PERIOD_OPTION

sealed interface HomeUiState {
    data object Default : HomeUiState
    data class Error(val message: String = "Unexpected error occurred") : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val amountWordsToLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val timePeriod: TimePeriodChartOptions = DEFAULT_TIME_PERIOD_OPTION,
        val listOfWordsCountOfStatus: List<Map<WordStatus, Int>> = listOf(),
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val showTimePeriodDialog: Boolean = false
    ) : HomeUiState
}