package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

data class DropdownItemState(
    @StringRes val labelRes: Int,
    val icon: Painter,
    val onClick: () -> Unit,
    val snackbarMessage: String? = null,
    val onActionPerformed: () -> Unit = {},
    val onDismissAction: () -> Unit = {},
)

@Composable
fun getCommonMenuItems(
    wordId: Long,
    navigateTo: (String) -> Unit,
    viewModel: FlashcardViewModel,
) = listOf(
    DropdownItemState(
        labelRes = R.string.copy_to_my_category,
        icon = rememberVectorPainter(Icons.Default.FolderCopy),
        snackbarMessage = stringResource(R.string.word_has_been_copied_to_your_category),
        onClick = viewModel::copyWordToMyCategory,
        onActionPerformed = viewModel::removeWordFromMyCategory,
        onDismissAction = viewModel::removeWordFromQueue,
    ),
    DropdownItemState(
        labelRes = R.string.report_a_mistake,
        icon = rememberVectorPainter(Icons.Default.Report),
        onClick = {
            navigateTo(Destination.ReportMistakeScreen(wordId))
        },
    ),
    DropdownItemState(
        labelRes = R.string.edit,
        icon = rememberVectorPainter(Icons.Default.EditNote),
        onClick = {
            navigateTo(Destination.EditWord(wordId))
        },
    ),
    DropdownItemState(
        labelRes = R.string.remove,
        icon = rememberVectorPainter(Icons.Default.Remove),
        onClick = viewModel::addWordToQueue,
        // SnackbarDuration.Long == 10 seconds
        snackbarMessage = stringResource(R.string.word_will_be_removed_in_seconds, 10),
        onActionPerformed = viewModel::removeWordFromQueue,
        onDismissAction = viewModel::removeWord,
    )
)
