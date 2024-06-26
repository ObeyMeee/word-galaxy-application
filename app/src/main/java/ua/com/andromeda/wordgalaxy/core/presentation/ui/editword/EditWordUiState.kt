package ua.com.andromeda.wordgalaxy.core.presentation.ui.editword

import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.Example
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.ExistingWord

sealed class EditWordUiState(
    open val isFormValid: Boolean = false,
) {
    data object Default : EditWordUiState()
    data class Error(val message: String = "Something went wrong") : EditWordUiState()

    data class Success(
        val editableWord: EmbeddedWord,
        val word: String = editableWord.word.value,
        val transcription: String = getTranscription(editableWord),
        val translation: String = editableWord.word.translation,
        val selectedCategories: List<Pair<Category, Boolean>> = editableWord.categories.map { it to false },
        val existingWords: List<ExistingWord> = emptyList(),
        val examples: List<Example> = editableWord.examples,
        val suggestedCategories: List<Category> = emptyList(),
        override val isFormValid: Boolean = true,
    ) : EditWordUiState(isFormValid)
}

private fun getTranscription(editableWord: EmbeddedWord) =
    editableWord
        .phonetics[0]
        .text
        .removePrefix("/")
        .removeSuffix("/")
