package ua.com.andromeda.wordgalaxy.ui.screens.study.reportmistake

data class Report(
    val word: String,
    val translation: String,
    val comment: String? = null
)
