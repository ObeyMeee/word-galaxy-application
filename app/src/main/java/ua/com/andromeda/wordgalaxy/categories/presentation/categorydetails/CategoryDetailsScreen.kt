@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails.components.CategoryDetailsTopAppBar
import ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails.components.Dialogs
import ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails.components.ScrollToTop
import ua.com.andromeda.wordgalaxy.core.data.db.database.DefaultStorage
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.isNew
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.core.presentation.components.showUndoSnackbar
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

@Composable
fun CategoryDetailsScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val viewModel: CategoryDetailsViewModel = hiltViewModel()
    Scaffold(
        topBar = {
            CategoryDetailsTopAppBar(
                navigateUp = navigateUp,
                viewModel = viewModel,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        CategoryDetailsMain(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
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
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is CategoryDetailsUiState.Default -> CenteredLoadingSpinner(modifier)
        is CategoryDetailsUiState.Error ->
            Message(
                message = state.message,
                modifier = modifier
            )

        is CategoryDetailsUiState.Success -> {
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            WordList(
                items = state.embeddedWords,
                listState = listState,
                snackbarHostState = snackbarHostState,
                viewModel = viewModel,
                modifier = modifier,
            )
            Dialogs(
                state = state,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                navigateTo = navigateTo,
            )
            ScrollToTop(listState = listState)

            LaunchedEffect(Unit) {
                val indexToScroll = state.indexToScroll
                if (indexToScroll != -1) {
                    scope.launch {
                        listState.scrollToItem(indexToScroll)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordList(
    items: List<EmbeddedWord>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    listState: LazyListState = rememberLazyListState(),
) {
    // SnackbarDuration.Long == 10 seconds
    val removeMessage = stringResource(R.string.word_will_be_removed_in_seconds, 10)
    val resetMessage = stringResource(R.string.progress_has_been_reset_successfully)
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_smaller))
    ) {
        items(items, key = { it.word.id }) {
            SwipeWordListItem(
                embeddedWord = it,
                onSwipeLeft = {
                    viewModel.addWordToQueue(it)
                    scope.launch {
                        snackbarHostState.showUndoSnackbar(
                            message = removeMessage,
                            onActionPerformed = viewModel::removeWordFromQueue,
                            onDismiss = viewModel::removeWord,
                        )
                    }
                },
                onSwipeRight = {
                    if (it.word.isNew) {
                        viewModel.updateWordStatus(it, WordStatus.InProgress)
                    } else {
                        viewModel.resetWordProgress(it)
                        scope.launch {
                            snackbarHostState.showUndoSnackbar(
                                message = resetMessage,
                                onActionPerformed = viewModel::recoverWord,
                                onDismiss = viewModel::removeWordFromQueue,
                            )
                        }
                    }
                },
                modifier = Modifier
                    .animateItemPlacement(tween(500))
                    .clip(MaterialTheme.shapes.small),
                onClick = {
                    viewModel.selectWord(it)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeWordListItem(
    embeddedWord: EmbeddedWord,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val dismissState = rememberDismissState(positionalThreshold = { _ -> 86.dp.toPx() })

    handleDismiss(dismissState, onSwipeLeft, onSwipeRight)
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = DismissDirection.entries.toSet(),
        background = {
            SwipeBackground(dismissState, embeddedWord)
        },
        dismissContent = {
            // Content displayed when not dismissed
            WordListItem(
                embeddedWord = embeddedWord,
                modifier = Modifier.clickable(onClick = onClick),
            )
        }
    )
}

@Composable
private fun WordListItem(
    embeddedWord: EmbeddedWord,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val (word, _, phonetics) = embeddedWord
    val status = word.status
    val amountRepetition = word.amountRepetition ?: 0
    val label = stringResource(status.labelRes, amountRepetition)

    ListItem(
        headlineContent = {
            Text(
                text = word.value,
                style = MaterialTheme.typography.titleMedium
            )
        },
        overlineContent = { Text(text = label) },
        supportingContent = { Text(text = word.translation) },
        leadingContent = {
            Icon(
                imageVector = Icons.Rounded.Square,
                contentDescription = label,
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
        modifier = modifier,
    )
}

@Composable
private fun SwipeBackground(
    dismissState: DismissState,
    embeddedWord: EmbeddedWord
) {
    val dismissValue = dismissState.targetValue

    val iconImageVector = getIconImageVector(embeddedWord, dismissValue)
    val iconAlignment = getIconAlignment(dismissValue)
    val iconScale = getIconScale(dismissValue)
    val backgroundColor by animateColorAsState(
        targetValue = getBackgroundColor(embeddedWord, dismissValue),
        label = ""
    )

    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(backgroundColor)
            }
            .padding(
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            ),
        contentAlignment = iconAlignment
    ) {
        Icon(
            imageVector = iconImageVector,
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
            },
            tint = Color.White
        )
    }
}

@Composable
private fun handleDismiss(
    dismissState: DismissState,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val dismissValue = dismissState.currentValue
    LaunchedEffect(dismissValue) {
        // Only execute the swipe actions when the dismissal is completed
        if (dismissValue != DismissValue.Default) {
            if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
                onSwipeLeft()
            } else if (dismissState.isDismissed(direction = DismissDirection.StartToEnd)) {
                onSwipeRight()
            }

            // Reset dismiss state after executing the actions
            dismissState.reset()
        }
    }
}

private fun getBackgroundColor(word: EmbeddedWord, dismissValue: DismissValue) =
    when (dismissValue) {
        DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.8f)
        DismissValue.DismissedToEnd -> {
            if (word.word.isNew) WordStatus.InProgress.iconColor
            else WordStatus.New.iconColor
        }

        else -> Color.Transparent
    }

private fun getIconImageVector(
    embeddedWord: EmbeddedWord,
    dismissValue: DismissValue
): ImageVector =
    when (dismissValue) {
        DismissValue.DismissedToEnd -> {
            if (embeddedWord.word.isNew) Icons.Outlined.Lightbulb
            else Icons.Outlined.HourglassEmpty
        }

        else -> Icons.Outlined.Delete
    }

private fun getIconAlignment(dismissValue: DismissValue): Alignment =
    when (dismissValue) {
        DismissValue.DismissedToEnd -> Alignment.CenterStart
        else -> Alignment.CenterEnd
    }

private fun getIconScale(dismissValue: DismissValue) =
    if (dismissValue == DismissValue.Default) 0.5f else 1.3f


@Preview
@Composable
fun WordListItemPreview() {
    WordGalaxyTheme {
        Surface {
            WordListItem(
                embeddedWord = DefaultStorage.embeddedWord,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WordListItemDarkThemePreview() {
    WordGalaxyTheme {
        Surface {
            WordListItem(
                embeddedWord = DefaultStorage.embeddedWord,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            )
        }
    }
}
