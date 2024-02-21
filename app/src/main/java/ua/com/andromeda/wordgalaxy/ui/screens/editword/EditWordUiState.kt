package ua.com.andromeda.wordgalaxy.ui.screens.editword

import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example

sealed class EditWordUiState(
    open val isFormValid: Boolean = false,
) {
    data object Default : EditWordUiState()
    data class Error(val message: String = "Something went wrong") : EditWordUiState()

    data class Success(
        val editableWord: EmbeddedWord,
        val word: String = editableWord.word.value,
        val transcription: String = editableWord.phonetics[0].text,
        val translation: String = editableWord.word.translation,
        val selectedCategories: List<Pair<Category, Boolean>> = editableWord.categories.map { it to false },
        val examples: List<Example> = editableWord.examples,
        val suggestedCategories: List<Category> = emptyList(),
        override val isFormValid: Boolean = true,
    ) : EditWordUiState(isFormValid)
}