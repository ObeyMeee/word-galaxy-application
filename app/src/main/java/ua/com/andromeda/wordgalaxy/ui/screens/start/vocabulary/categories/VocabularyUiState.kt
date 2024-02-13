package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

sealed interface VocabularyUiState {
    data object Default : VocabularyUiState
    data class Error(val message: String = "Unexpected error occurred") : VocabularyUiState
    data class Success(
        val vocabularyCategories: List<VocabularyCategory> = emptyList(),
        val searchQuery: String = "",
        val activeSearch: Boolean = false,
        val suggestedWords: List<EmbeddedWord> = emptyList(),
        val selectedWord: EmbeddedWord? = null
    ) : VocabularyUiState
}