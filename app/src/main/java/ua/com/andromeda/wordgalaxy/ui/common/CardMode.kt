package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.ui.graphics.vector.ImageVector
import ua.com.andromeda.wordgalaxy.R

enum class CardMode(
    val icon: ImageVector? = null,
    @StringRes val labelRes: Int,
) {
    Default(labelRes = R.string.default_mode),
    TypeAnswer(
        icon = Icons.Default.Keyboard,
        labelRes = R.string.type_answer,
    ),
    ShowAnswer(
        icon = Icons.Default.RemoveRedEye,
        labelRes = R.string.show_answer,
    ),
}