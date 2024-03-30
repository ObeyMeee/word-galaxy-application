package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.utils.Direction

sealed class CategoryDetailsUiState(
    open val title: String = "Category",
    open val topAppBarMenuExpanded: Boolean = false,
    open val orderDialogVisible: Boolean = false,
    open val resetProgressDialogVisible: Boolean = false,
    open val selectedSortOrder: WordSortOrder = WordSortOrder.ALPHABETICAL,
    open val direction: Direction = Direction.ASC,
) {
    data object Default : CategoryDetailsUiState()
    data class Error(
        val message: String = "Something went wrong"
    ) : CategoryDetailsUiState()

    data class Success(
        override val title: String = "Category",
        override val topAppBarMenuExpanded: Boolean = false,
        override val orderDialogVisible: Boolean = false,
        override val resetProgressDialogVisible: Boolean = false,
        override val selectedSortOrder: WordSortOrder = WordSortOrder.ALPHABETICAL,
        override val direction: Direction = Direction.ASC,
        val embeddedWords: List<EmbeddedWord> = emptyList(),
        val selectedWord: EmbeddedWord? = null,
        val indexToScroll: Int = -1,
        val wordsInProcessQueue: List<EmbeddedWord> = emptyList(),
    ) : CategoryDetailsUiState(title)
}