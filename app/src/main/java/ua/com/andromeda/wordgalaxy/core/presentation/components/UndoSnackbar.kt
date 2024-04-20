package ua.com.andromeda.wordgalaxy.core.presentation.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

private const val UNDO = "Undo"

suspend fun SnackbarHostState.showUndoSnackbar(
    message: String,
    onActionPerformed: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val snackbarResult = showSnackbar(
        message = message,
        actionLabel = UNDO,
        withDismissAction = true,
        duration = SnackbarDuration.Long,
    )
    when (snackbarResult) {
        SnackbarResult.ActionPerformed -> onActionPerformed()
        SnackbarResult.Dismissed -> onDismiss()
    }
}
