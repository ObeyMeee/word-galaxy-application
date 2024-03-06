package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.painter.Painter

data class DropdownItemState(
    @StringRes val labelRes: Int,
    val icon: Painter,
    val onClick: () -> Unit,
    val snackbarMessage: String? = null,
    val onActionPerformed: () -> Unit = {},
    val onDismissAction: () -> Unit = {},
)