package ua.com.andromeda.wordgalaxy.ui.screens.learnwords

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord

sealed interface LearnWordsUiState {
    data object Default: LearnWordsUiState
    data object Error: LearnWordsUiState
    data class Success(
        val embeddedWord: EmbeddedWord,
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0
    ): LearnWordsUiState
}