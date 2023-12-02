package ua.com.andromeda.wordgalaxy.ui.screens.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ua.com.andromeda.wordgalaxy.R

sealed class CardState(
    @StringRes val headerLabelRes: Int,
    val iconColor: Color,
    val actionLabelResLeft: Int,
    val actionLabelResRight: Int,
    val onLeftClick: () -> Unit,
    val onRightClick: () -> Unit,
) {
    class NewWord(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
    ) : CardState(
        headerLabelRes = R.string.new_word,
        iconColor = Color.Red,
        actionLabelResLeft = R.string.i_already_know_this_word,
        actionLabelResRight = R.string.start_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

    class InProgress(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
    ) : CardState(
        headerLabelRes = R.string.learning_new_word,
        iconColor = Color.Blue,
        actionLabelResLeft = R.string.i_have_memorized_this_word,
        actionLabelResRight = R.string.keep_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

}

