package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.painter.Painter

data class DropdownItemState(
    @StringRes val labelRes: Int,
    val icon: Painter,
    val onClick: () -> Unit,
    val showToast: Boolean = true,
    @StringRes val toastMessageRes: Int = 0
)