package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import ua.com.andromeda.wordgalaxy.data.model.Category

data class ExistingWord(
    val translation: String,
    val categories: List<Category>
)
