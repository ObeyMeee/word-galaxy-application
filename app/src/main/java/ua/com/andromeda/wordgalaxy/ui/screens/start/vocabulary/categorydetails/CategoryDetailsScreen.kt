package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.common.ScrollToTop
import ua.com.andromeda.wordgalaxy.ui.screens.common.isScrollingUp
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.Direction
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

private const val TAG = "CategoryDetailsScreen"

@Composable
fun CategoryDetailsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    firstShownWord: String? = null,
) {
    val viewModel: CategoryDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        topBar = {
            CategoryDetailsTopAppBar(
                title = state.title,
                sortOrder = state.selectedSortOrder,
                direction = state.direction,
                navigateUp = navigateUp,
                updateSortDirection = viewModel::updateSortDirection,
                menuExpanded = state.topAppBarMenuExpanded,
                openConfirmResetProgressDialog = viewModel::openConfirmResetProgressDialog,
                expandMenu = viewModel::expandTopAppBarMenu,
                openOrderDialog = viewModel::openOrderDialog
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { innerPadding ->
        CategoryDetailsMain(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            firstShownWord = firstShownWord,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsTopAppBar(
    title: String,
    sortOrder: WordSortOrder,
    direction: Direction,
    modifier: Modifier = Modifier,
    menuExpanded: Boolean = false,
    updateSortDirection: () -> Unit = {},
    openConfirmResetProgressDialog: (Boolean) -> Unit = {},
    navigateUp: () -> Unit = {},
    expandMenu: (Boolean) -> Unit = {},
    openOrderDialog: (Boolean) -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            Crossfade(
                targetState = direction,
                label = "OrderIconAnimation"
            ) {
                val sortIcon =
                    if (it == Direction.ASC)
                        R.drawable.sort_ascending_icon
                    else R.drawable.sort_descending_icon
                IconButton(onClick = updateSortDirection) {
                    Icon(
                        painter = painterResource(sortIcon),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
                    )
                }
            }
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                IconButton(onClick = { expandMenu(true) }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.show_more)
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { expandMenu(false) },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.reset_progress))
                        },
                        onClick = {
                            openConfirmResetProgressDialog(true)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.word_order, sortOrder.label))
                        },
                        onClick = { openOrderDialog(true) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun CategoryDetailsMain(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    firstShownWord: String? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    SelectOrderDialog(
        visible = uiState.orderDialogVisible,
        selectedOption = uiState.selectedSortOrder,
        onOptionSelected = viewModel::selectSortOrder,
        closeDialog = viewModel::openOrderDialog
    )

    when (val state = uiState) {
        is CategoryDetailsUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is CategoryDetailsUiState.Error -> {
            Message(
                message = state.message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        is CategoryDetailsUiState.Success -> {
            val items = state.embeddedWords
            WordActionDialog(
                selectedWord = state.selectedWord,
                snackbarHostState = snackbarHostState,
                closeDialog = viewModel::selectWord,
            )
            ResetProgressDialog(
                visible = state.resetProgressDialogVisible,
                closeDialog = viewModel::openConfirmResetProgressDialog,
                confirmAction = viewModel::resetCategoryProgress
            )

            WordList(
                items = items,
                selectItem = viewModel::selectWord,
                listState = listState,
                modifier = modifier
            )
            val indexToScroll = firstShownWord?.let { word ->
                items.indexOfFirst { it.word.value == word }
            }

            if (indexToScroll != null && indexToScroll != -1) {
                LaunchedEffect(Unit) {
                    coroutineScope.launch(Dispatchers.IO) {
                        listState.scrollToItem(indexToScroll)
                    }
                }
            }

            AnimatedVisibility(
                visible = !listState.isScrollingUp(),
                enter = slideInVertically(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS)) { it }
                        + fadeIn(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS), .4f),
                exit = slideOutVertically(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS)) { it }
                        + fadeOut(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS), .4f)
            ) {
                ScrollToTop {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            }
        }
    }
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
    selectedWord: EmbeddedWord?,
    snackbarHostState: SnackbarHostState,
    closeDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    if (selectedWord != null) {
        AlertDialog(
            onDismissRequest = closeDialog,
            confirmButton = {},
            modifier = modifier,
            title = {
                Text(text = stringResource(R.string.word_actions))
            },
            text = {
                Column {
                    if (selectedWord.word.status == WordStatus.New) {
                        WordActionButton(
                            labelRes = R.string.mark_word_as_already_known,
                            icon = Icons.Default.Check,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            messageRes = R.string.you_have_successfully_marked_word_as_already_known,
                            action = { viewModel.updateWordStatus(WordStatus.AlreadyKnown) }
                        )
                        WordActionButton(
                            labelRes = R.string.learn,
                            icon = Icons.Default.Lightbulb,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            messageRes = R.string.word_status_has_been_changed,
                            action = { viewModel.updateWordStatus(WordStatus.InProgress) }
                        )
                    } else {
                        WordActionButton(
                            labelRes = R.string.reset_progress_for_this_word,
                            icon = Icons.Default.HourglassEmpty,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            messageRes = R.string.progress_has_been_reset_successfully,
                            action = { viewModel.resetWordProgress() }
                        )
                    }
                    WordActionButton(
                        labelRes = R.string.copy_to_my_category,
                        icon = Icons.Default.FolderCopy,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope,
                        messageRes = R.string.word_has_been_copied_to_your_category,
                        action = { viewModel.copyWordToMyCategory() }
                    )
                    WordActionButton(
                        labelRes = R.string.report_a_mistake,
                        icon = Icons.Default.Report,
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        action = { viewModel.reportMistake() }
                    )
                    WordActionButton(
                        labelRes = R.string.edit,
                        icon = Icons.Default.Edit,
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        action = { viewModel.editWord() }
                    )
                    WordActionButton(
                        labelRes = R.string.remove,
                        icon = Icons.Default.Remove,
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        messageRes = R.string.word_has_been_successfully_removed,
                        action = { viewModel.removeWord() }
                    )
                }
            }
        )
    }
}

@Composable
private fun WordActionButton(
    @StringRes labelRes: Int,
    icon: ImageVector,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    @StringRes messageRes: Int? = null,
    action: () -> Unit = {},

    ) {
    val message = if (messageRes == null) "" else stringResource(messageRes)

    Button(
        onClick = {
            action()
            messageRes?.let {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
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
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
            Text(text = stringResource(labelRes))
        }
    }
}

@Composable
fun WordList(
    items: List<EmbeddedWord>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    selectItem: (EmbeddedWord) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_smallest))
    ) {
        items(items) {
            WordListItem(
                embeddedWord = it,
                modifier = Modifier.clickable {
                    selectItem(it)
                }
            )
        }
    }
}

@Composable
private fun WordListItem(
    embeddedWord: EmbeddedWord,
    modifier: Modifier = Modifier
) {
    val (word, _, phonetics) = embeddedWord
    val context = LocalContext.current
    val status = word.status
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1
    val label = stringResource(status.labelRes, numberReview)
    ListItem(
        headlineContent = {
            Text(
                text = word.value,
                style = MaterialTheme.typography.titleMedium
            )
        },
        overlineContent = {
            Text(text = label)
        },
        supportingContent = {
            Text(text = word.translation)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Rounded.Square,
                contentDescription = label,
                modifier = Modifier.padding(
                    end = dimensionResource(R.dimen.padding_small)
                ),
                tint = status.iconColor
            )
        },
        trailingContent = {
            IconButton(
                onClick = {
                    val audioUrls = phonetics.map { it.audio }
                    context.playPronunciation(audioUrls)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = stringResource(R.string.play_pronunciation),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large))
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun WordListPreview() {
    WordGalaxyTheme {
        Surface {
            WordList(
                items = DefaultStorage.embeddedWords,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WordListDarkThemePreview() {
    WordGalaxyTheme {
        Surface {
            WordList(
                items = DefaultStorage.embeddedWords,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}
