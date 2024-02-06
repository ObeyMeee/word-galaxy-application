package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory

import ua.com.andromeda.wordgalaxy.data.model.Category

sealed interface NewCategoryUiState {
    data object Default : NewCategoryUiState
    data class Error(val message: String = "Something went wrong") : NewCategoryUiState
    data class Success(
        val title: String = "",
        val parentCategories: List<Category> = emptyList(),
        val selectedCategory: Category? = null,
        val parentCategoriesExpanded: Boolean = false
    ) : NewCategoryUiState
}