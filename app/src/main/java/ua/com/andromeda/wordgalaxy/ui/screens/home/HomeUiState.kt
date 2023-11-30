package ua.com.andromeda.wordgalaxy.ui.screens.home

sealed interface HomeUiState {
    object Loading : HomeUiState
    class Error(val throwable: Throwable) : HomeUiState

    data class Success(
        val learnedWordsToday: Int = 0,
        val maxLearnedWords: Int = 0,
        val wordsToReview: Int = 0
    ) : HomeUiState
}