package ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform

import ua.com.andromeda.wordgalaxy.core.domain.model.Category

data class ExistingWord(
    val translation: String,
    val categories: List<Category>
)
