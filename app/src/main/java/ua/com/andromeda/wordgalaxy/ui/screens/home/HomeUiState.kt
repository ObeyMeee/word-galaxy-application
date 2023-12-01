package ua.com.andromeda.wordgalaxy.ui.screens.home

sealed interface HomeUiState {
    data object Default : HomeUiState
    data class Error(val throwable: Throwable) : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0,
        val wordsToReview: Int = 0
    ) : HomeUiState
}