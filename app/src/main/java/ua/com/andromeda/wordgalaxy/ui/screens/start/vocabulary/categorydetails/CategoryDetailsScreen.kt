package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordAndPhonetics
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

@Composable
fun CategoryDetailsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CategoryDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CategoryDetailsTopAppBar(
                title = state.title,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->
        CategoryDetailsMain(
            viewModel = viewModel,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
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
        }
    )
}

@Composable
fun CategoryDetailsMain(
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
            WordList(
                items = state.wordsAndPhonetics,
                modifier = modifier
            )
        }
    }
}

@Composable
fun WordList(
    items: List<WordAndPhonetics>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LazyColumn(modifier) {
        items(items) {
            ListItem(
                headlineContent = {
                    Text(text = it.word.value)
                },
                overlineContent = {
                    Text(text = it.word.status.name)
                },
                supportingContent = {
                    Text(text = it.word.translation)
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Square,
                        contentDescription = null,
                        modifier = Modifier.padding(
                            end = dimensionResource(R.dimen.padding_small)
                        ),
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            val audioUrls = it.phonetics.map { it.audio }
                            context.playPronunciation(audioUrls)
                        }) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = stringResource(R.string.play_pronunciation),
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}
