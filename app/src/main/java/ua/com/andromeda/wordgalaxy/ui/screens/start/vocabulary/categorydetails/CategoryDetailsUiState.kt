package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord

sealed class CategoryDetailsUiState(
    open val title: String = "Category"
) {
    data object Default : CategoryDetailsUiState()
    data class Error(
        val message: String = "Something went wrong"
    ) : CategoryDetailsUiState()

    data class Success(
        override val title: String = "Category",
        val embeddedWords: List<EmbeddedWord> = emptyList(),
        val selectedWord: EmbeddedWord? = null
    ) : CategoryDetailsUiState(title)
}