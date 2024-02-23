package ua.com.andromeda.wordgalaxy.ui.common.wordform

import ua.com.andromeda.wordgalaxy.data.model.Category

data class ExistingWord(
    val translation: String,
    val categories: List<Category>
)
