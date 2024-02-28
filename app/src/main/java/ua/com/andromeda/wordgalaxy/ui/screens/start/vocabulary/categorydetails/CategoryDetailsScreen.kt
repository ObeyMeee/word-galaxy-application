package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.ScrollToTop
import ua.com.andromeda.wordgalaxy.ui.common.isScrollingUp
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

@Composable
fun CategoryDetailsScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    firstShownWord: String? = null,
) {
    val viewModel: CategoryDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

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
        modifier = modifier
    ) { innerPadding ->
        CategoryDetailsMain(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            firstShownWord = firstShownWord,
            navigateTo = navigateTo,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun CategoryDetailsMain(
    snackbarHostState: SnackbarHostState,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    firstShownWord: String? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    when (val state = uiState) {
        is CategoryDetailsUiState.Default -> CenteredLoadingSpinner()
        is CategoryDetailsUiState.Error -> Message(message = state.message)

        is CategoryDetailsUiState.Success -> {
            val items = state.embeddedWords
            Dialogs(
                state = state,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                navigateTo = navigateTo,
            )
            WordList(
                items = items,
                listState = listState,
                snackbarHostState = snackbarHostState,
                selectItem = viewModel::selectWord,
                removeItem = viewModel::removeWord,
                updateWordStatus = viewModel::updateWordStatus,
                resetWordProgress = viewModel::resetWordProgress,
                modifier = modifier,
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

            ScrollToTop(visible = !listState.isScrollingUp()) {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordList(
    items: List<EmbeddedWord>,
    selectItem: (EmbeddedWord) -> Unit,
    removeItem: (EmbeddedWord) -> Unit,
    updateWordStatus: (EmbeddedWord, WordStatus) -> Unit,
    resetWordProgress: (EmbeddedWord) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val removeMessage = stringResource(R.string.word_has_been_successfully_removed)

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_smallest))
    ) {
        items(items, key = { it.word.id }) {
            WordListItem(
                embeddedWord = it,
                onSwipeLeft = {
                    removeItem(it)
                    LaunchedEffect(it.word) {
                        coroutineScope.launch(Dispatchers.IO) {
                            snackbarHostState.showSnackbar(
                                message = removeMessage,
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Long,
                            )
                        }
                    }
                },
                onSwipeRight = {
                    if (it.word.status == WordStatus.New) {
                        updateWordStatus(it, WordStatus.InProgress)
                    } else {
                        resetWordProgress(it)
                    }
                },
                modifier = Modifier
                    .animateItemPlacement(tween(500))
                    .clickable {
                        selectItem(it)
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordListItem(
    embeddedWord: EmbeddedWord,
    onSwipeLeft: @Composable () -> Unit,
    onSwipeRight: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val (word, _, phonetics) = embeddedWord
    val context = LocalContext.current
    val status = word.status
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1
    val label = stringResource(status.labelRes, numberReview)
    val dismissState = rememberDismissState()

    // check if the user swiped
    if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
        onSwipeLeft()
    } else if (dismissState.isDismissed(direction = DismissDirection.StartToEnd)) {
        onSwipeRight()
    }

    if (dismissState.currentValue != DismissValue.Default) {
        LaunchedEffect(Unit) {
            dismissState.reset()
        }
    }
    SwipeToDismiss(
        state = dismissState,
        directions = DismissDirection.entries.toSet(),
        background = {
            // this background is visible when we swipe.
            // it contains the icon

            // background color
            val backgroundColor by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.8f)
                    DismissValue.DismissedToEnd -> {
                        val new = WordStatus.New
                        if (status == new) WordStatus.InProgress.iconColor
                        else new.iconColor
                    }

                    else -> Color.Transparent
                },
                label = ""
            )

            // icon
            val iconImageVector = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> {
                    val new = WordStatus.New
                    if (status == new) Icons.Outlined.Lightbulb
                    else Icons.Outlined.HourglassEmpty
                }

                else -> Icons.Outlined.Delete
            }

            // icon placement
            val iconAlignment = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> Alignment.CenterStart
                else -> Alignment.CenterEnd
            }

            // icon size
            val iconScale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) 0.5f else 1.3f
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
                    .padding(
                        start = dimensionResource(R.dimen.padding_medium),
                        end = dimensionResource(R.dimen.padding_medium)
                    ),
                contentAlignment = iconAlignment
            ) {
                Icon(
                    modifier = Modifier.scale(iconScale),
                    imageVector = iconImageVector,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        dismissContent = {
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
                            context.playPronunciation(phonetics)
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
    )
}

//@Preview
//@Composable
//fun WordListPreview() {
//    WordGalaxyTheme {
//        Surface {
//            WordList(
//                items = DefaultStorage.embeddedWords,
//                removeItem = {},
//                navigateTo = {},
//                selectItem = {},
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
//                snackbarHostState = SnackbarHostState()
//            )
//        }
//    }
//}
//
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun WordListDarkThemePreview() {
//    WordGalaxyTheme {
//        Surface {
//            WordList(
//                items = DefaultStorage.embeddedWords,
//                removeItem = {},
//                navigateTo = {},
//                selectItem = {},
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
//                snackbarHostState = SnackbarHostState()
//            )
//        }
//    }
//}
