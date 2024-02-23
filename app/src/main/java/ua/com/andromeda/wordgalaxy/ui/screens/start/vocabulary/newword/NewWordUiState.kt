package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.ui.common.wordform.ExistingWord

sealed class NewWordUiState(
    open val isFormValid: Boolean = false,
) {
    data object Default : NewWordUiState()
    data class Error(val message: String = "Unexpected error occurred") : NewWordUiState()
    data class Success(
        val suggestedCategories: List<Category> = emptyList(),
        val word: String = "",
        val transcription: String = "",
        val translation: String = "",
        val selectedCategories: List<Pair<Category, Boolean>> = listOf(MY_WORDS_CATEGORY to false),
        val examples: List<Example> = emptyList(),
        val existingWords: List<ExistingWord> = emptyList(),
        override val isFormValid: Boolean = false,
    ) : NewWordUiState()
}