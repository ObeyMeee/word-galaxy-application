package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary

import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

sealed interface VocabularyUiState {
    data object Default : VocabularyUiState
    data class Error(val message: String = "Unexpected error occurred") : VocabularyUiState
    data class Success(
        val vocabularyCategories: List<VocabularyCategory>
    ) : VocabularyUiState
}