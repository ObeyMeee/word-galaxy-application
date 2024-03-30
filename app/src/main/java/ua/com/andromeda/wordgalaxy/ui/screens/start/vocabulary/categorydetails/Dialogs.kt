package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.ui.common.showUndoSnackbar
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

@Composable
fun Dialogs(
    state: CategoryDetailsUiState.Success,
    snackbarHostState: SnackbarHostState,
    navigateTo: (String) -> Unit,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    state.selectedWord?.let {
        WordActionDialog(
            selectedWord = it,
            snackbarHostState = snackbarHostState,
            navigateTo = navigateTo,
            scope = scope
        )
    }
    ResetProgressDialog(
        visible = state.resetProgressDialogVisible,
        closeDialog = viewModel::openConfirmResetProgressDialog,
        confirmAction = viewModel::resetCategoryProgress
    )
    SelectOrderDialog(
        visible = state.orderDialogVisible,
        selectedOption = state.selectedSortOrder,
        onOptionSelected = viewModel::selectSortOrder,
        closeDialog = viewModel::openOrderDialog
    )
}

@Composable
fun ResetProgressDialog(
    visible: Boolean,
    closeDialog: () -> Unit,
    confirmAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visible) {
        AlertDialog(
            onDismissRequest = closeDialog,
            confirmButton = {
                Button(onClick = confirmAction) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = closeDialog) {
                    Text(text = "Cancel")
                }
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.are_you_sure_you_want_to_reset_progress_of_all_words_of_current_category
                    )
                )
            },
            modifier = modifier,
        )
    }
}

@Composable
fun SelectOrderDialog(
    visible: Boolean,
    selectedOption: WordSortOrder,
    onOptionSelected: (WordSortOrder) -> Unit,
    closeDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = visible) {
        AlertDialog(
            onDismissRequest = closeDialog,
            confirmButton = {},
            modifier = modifier,
            title = {
                Text(text = "Word order")
            },
            text = {
                val radioOptions = WordSortOrder.entries
                Column(modifier = Modifier.selectableGroup()) {
                    radioOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .selectable(
                                    selected = option == selectedOption,
                                    onClick = {
                                        onOptionSelected(option)
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                        ) {
                            RadioButton(
                                selected = option == selectedOption,
                                onClick = null
                            )
                            Text(
                                text = option.label,
                                modifier = Modifier.padding(
                                    start = dimensionResource(R.dimen.padding_medium)
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun WordActionDialog(
    selectedWord: EmbeddedWord,
    snackbarHostState: SnackbarHostState,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    scope: CoroutineScope,
) {
    val closeDialog = { viewModel.selectWord() }
    AlertDialog(
        onDismissRequest = closeDialog,
        confirmButton = {},
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.word_actions))
        },
        text = {
            val wordId = selectedWord.word.id
            Column {
                if (selectedWord.word.status == WordStatus.New) {
                    WordActionButton(
                        labelRes = R.string.mark_word_as_already_known,
                        icon = Icons.Default.Check,
                        onClick = {
                            viewModel.updateWordStatus(WordStatus.AlreadyKnown)
                            closeDialog()
                        },
                    )
                    WordActionButton(
                        labelRes = R.string.learn,
                        icon = Icons.Default.Lightbulb,
                        onClick = {
                            viewModel.updateWordStatus(WordStatus.InProgress)
                            closeDialog()
                        },
                    )
                } else {
                    WordActionButton(
                        labelRes = R.string.reset_progress_for_this_word,
                        icon = Icons.Default.HourglassEmpty,
                        snackbarHostState = snackbarHostState,
                        message = stringResource(R.string.progress_has_been_reset_successfully),
                        scope = scope,
                        onClick = {
                            viewModel.resetWordProgress()
                            closeDialog()
                        },
                        snackbarOnActionPerformed = viewModel::recoverWord,
                        snackbarOnDismiss = viewModel::removeWordFromQueue,
                    )
                }
                WordActionButton(
                    labelRes = R.string.copy_to_my_category,
                    icon = Icons.Default.FolderCopy,
                    snackbarHostState = snackbarHostState,
                    message = stringResource(R.string.word_has_been_copied_to_your_category),
                    scope = scope,
                    onClick = {
                        viewModel.copyWordToMyCategory().invokeOnCompletion {
                            closeDialog()
                        }
                    },
                    snackbarOnActionPerformed = viewModel::removeWordFromMyCategory,
                    snackbarOnDismiss = viewModel::removeWordFromQueue,
                )
                WordActionButton(
                    labelRes = R.string.report_a_mistake,
                    icon = Icons.Default.Report,
                    onClick = { navigateTo(Destination.ReportMistakeScreen(wordId)) }
                )
                WordActionButton(
                    labelRes = R.string.edit,
                    icon = Icons.Default.Edit,
                    onClick = { navigateTo(Destination.EditWord(wordId)) }
                )
                WordActionButton(
                    labelRes = R.string.remove,
                    icon = Icons.Default.Remove,
                    snackbarHostState = snackbarHostState,
                    // SnackbarDuration.Long == 10s
                    message = stringResource(R.string.word_will_be_removed_in_seconds, 10),
                    onClick = {
                        viewModel.addWordToQueue(selectedWord)
                        closeDialog()
                    },
                    scope = scope,
                    snackbarOnActionPerformed = viewModel::removeWordFromQueue,
                    snackbarOnDismiss = viewModel::removeWord,
                )
            }
        }
    )
}

@Composable
private fun WordActionButton(
    @StringRes labelRes: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    message: String = "Success",
    scope: CoroutineScope = rememberCoroutineScope(),
    onClick: () -> Unit = {},
    snackbarOnActionPerformed: () -> Unit = {},
    snackbarOnDismiss: () -> Unit = {},
) {
    Button(
        onClick = {
            onClick()
            snackbarHostState?.let {
                scope.launch {
                    it.showUndoSnackbar(
                        message = message,
                        onActionPerformed = snackbarOnActionPerformed,
                        onDismiss = snackbarOnDismiss,
                    )
                }
            }
        },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            HorizontalSpacer(width = R.dimen.padding_small)
            Text(text = stringResource(labelRes))
        }
    }
}
