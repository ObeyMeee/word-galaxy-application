package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import ua.com.andromeda.wordgalaxy.data.model.WordAndPhonetics

sealed class CategoryDetailsUiState(
    open val title: String = "Category"
) {
    data object Default : CategoryDetailsUiState()
    data class Error(
        val message: String = "Something went wrong"
    ) : CategoryDetailsUiState()

    data class Success(
        val wordsAndPhonetics: List<WordAndPhonetics> = emptyList(),
        val isActionDialogOpen: Boolean = false,
        override val title: String = "Category"
    ) : CategoryDetailsUiState(title)
}