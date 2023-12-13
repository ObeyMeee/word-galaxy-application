package ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.theme.md_theme_light_primary
import ua.com.andromeda.wordgalaxy.ui.theme.md_theme_light_secondary
import ua.com.andromeda.wordgalaxy.ui.theme.md_theme_light_tertiary

sealed class FlashcardState(
    @StringRes val headerLabelRes: Int,
    val iconColor: Color,
    val actionLabelResLeft: Int,
    val actionLabelResRight: Int,
    val onLeftClick: () -> Unit,
    val onRightClick: () -> Unit,
) {
    class New(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
    ) : FlashcardState(
        headerLabelRes = R.string.new_word,
        iconColor = md_theme_light_primary,
        actionLabelResLeft = R.string.i_already_know_this_word,
        actionLabelResRight = R.string.start_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

    class InProgress(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
    ) : FlashcardState(
        headerLabelRes = R.string.learning_new_word,
        iconColor = md_theme_light_secondary,
        actionLabelResLeft = R.string.i_have_memorized_this_word,
        actionLabelResRight = R.string.keep_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

    class Review(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
    ) : FlashcardState(
        headerLabelRes = R.string.memorized_word,
        iconColor = md_theme_light_tertiary,
        actionLabelResLeft = R.string.got_it,
        actionLabelResRight = R.string.forgot_it,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )
}