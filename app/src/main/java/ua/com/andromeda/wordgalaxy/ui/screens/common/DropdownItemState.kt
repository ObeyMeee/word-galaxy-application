package ua.com.andromeda.wordgalaxy.ui.screens.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.painter.Painter

data class DropdownItemState(
    @StringRes val labelRes: Int,
    val icon: Painter,
    val onClick: () -> Unit
)