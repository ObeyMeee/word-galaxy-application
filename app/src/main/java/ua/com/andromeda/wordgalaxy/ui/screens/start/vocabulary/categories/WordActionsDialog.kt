package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer

@Composable
fun WordActionsDialog(
    selectedWord: EmbeddedWord?,
    closeDialog: () -> Unit,
    copyWordToMyCategory: () -> Unit,
    jumpToWord: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    AnimatedVisibility(
        visible = selectedWord != null,
        modifier = modifier
    ) {
        AlertDialog(
            onDismissRequest = closeDialog,
            confirmButton = {},
            title = {
                Text(text = stringResource(R.string.word_actions))
            },
            text = {
                Column {
                    Button(onClick = jumpToWord) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageSearch,
                                contentDescription = null
                            )
                            HorizontalSpacer(R.dimen.padding_small)
                            Text(text = stringResource(R.string.jump_to_this_word))
                        }
                    }
                    Button(onClick = {
                        copyWordToMyCategory()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "You have successfully copied '${selectedWord!!.word.value}' word to your category",
                                withDismissAction = true,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null
                            )
                            HorizontalSpacer(R.dimen.padding_small)
                            Text(text = stringResource(R.string.copy_to_my_category))
                        }
                    }
                }
            }
        )
    }
}
