package ua.com.andromeda.wordgalaxy.ui.screens.home

sealed interface HomeUiState {
    data object Default : HomeUiState
    data class Error(val message: String = "Unexpected error occurred") : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val amountWordsToLearnPerDay: Int = 0,
        val amountWordsToReview: Int = 0
    ) : HomeUiState
}