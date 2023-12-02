package ua.com.andromeda.wordgalaxy.ui.screens.browsecards

import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories

sealed interface BrowseCardUiState {
    data object Default: BrowseCardUiState
    data object Error: BrowseCardUiState
    data class Success(
        val wordWithCategories: WordWithCategories,
        val learnedWordsToday: Int = 0,
        val amountWordsLearnPerDay: Int = 0
    ): BrowseCardUiState
}