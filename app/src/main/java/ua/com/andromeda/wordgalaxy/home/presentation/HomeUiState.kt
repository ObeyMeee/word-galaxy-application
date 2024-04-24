package ua.com.andromeda.wordgalaxy.home.presentation

import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.home.presentation.components.ChartPeriodRangeStart
import java.time.LocalDate

val START_PERIOD_DEFAULT = ChartPeriodRangeStart.LAST_WEEK
val END_PERIOD_DEFAULT: LocalDate
    get() = LocalDate.now()

sealed interface HomeUiState {
    data object Default : HomeUiState
    data class Error(val message: String = "Unexpected error occurred") : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val amountWordsToLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0,
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val startPeriod: LocalDate = START_PERIOD_DEFAULT,
        val endPeriod: LocalDate = END_PERIOD_DEFAULT,
        val chartData: Map<LocalDate, Map<WordStatus, Int>> = mapOf(),
        val isPeriodDialogOpen: Boolean = false
    ) : HomeUiState
}