package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.common.ScrollToTop
import ua.com.andromeda.wordgalaxy.ui.screens.common.isScrollingUp
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
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

@Composable
private fun CategoryDetailsMain(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    firstShownWord: String? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
            Dialogs(
                state = state,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
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

            ScrollToTop(visible = !listState.isScrollingUp()) {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            }
        }
    }
}

@Composable
private fun WordList(
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
