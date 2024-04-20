package ua.com.andromeda.wordgalaxy.core.presentation.ui.reportmistake

data class Report(
    val word: String,
    val translation: String,
    val comment: String? = null
)
