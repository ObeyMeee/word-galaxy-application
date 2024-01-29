package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY

sealed interface NewWordUiState {
    data object Default : NewWordUiState
    data class Error(val message: String = "Unexpected error occurred") : NewWordUiState
    data class Success(
        val categories: List<Category> = emptyList(),
        val word: String = "",
        val transcription: String = "",
        val translation: String = "",
        val categoriesExpanded: Boolean = false,
        val selectedCategory: Category = MY_WORDS_CATEGORY,
        val examples: List<Example> = emptyList()
    ) : NewWordUiState
}