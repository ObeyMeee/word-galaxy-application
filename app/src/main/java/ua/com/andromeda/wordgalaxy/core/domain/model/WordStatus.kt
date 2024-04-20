package ua.com.andromeda.wordgalaxy.core.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ua.com.andromeda.wordgalaxy.R

enum class WordStatus(
    @StringRes val labelRes: Int,
    val iconColor: Color
) {
    AlreadyKnown(R.string.stack_label_already_known, Color(0xFF787878)),
    New(R.string.new_word, Color(0xFF002060)),
    InProgress(R.string.learning_new_word, Color(0xFFC0C000)),
    Memorized(R.string.memorized_word, Color(0xFF009B00)),
    Mastered(R.string.stack_label_mastered, Color(0xFF673AB7))
}