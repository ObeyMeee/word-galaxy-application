package ua.com.andromeda.wordgalaxy.study.flashcard.presentation

import ua.com.andromeda.wordgalaxy.R

sealed class FlashcardState(
    val actionLabelResLeft: Int,
    val actionLabelResRight: Int,
    val onLeftClick: () -> Unit,
    val onRightClick: () -> Unit,
) {
    class New(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
    ) : FlashcardState(
        actionLabelResLeft = R.string.i_already_know_this_word,
        actionLabelResRight = R.string.start_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

    class InProgress(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
    ) : FlashcardState(
        actionLabelResLeft = R.string.i_have_memorized_this_word,
        actionLabelResRight = R.string.keep_learning_this_word,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )

    class Review(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
    ) : FlashcardState(
        actionLabelResLeft = R.string.got_it,
        actionLabelResRight = R.string.forgot_it,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )
}