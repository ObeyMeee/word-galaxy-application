package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.WordAndPhonetics
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
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
        modifier = modifier
    ) { innerPadding ->
        CategoryDetailsMain(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_smallest))
    ) {
        items(items) { (word, phonetics) ->
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
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
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
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Preview
@Composable
fun WordListPreview() {
    WordGalaxyTheme {
        Surface {
            WordList(
                items = DefaultStorage.wordAndPhoneticsList,
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
                items = DefaultStorage.wordAndPhoneticsList,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}
