package ua.com.andromeda.wordgalaxy.categories.presentation.root.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.categories.presentation.root.VocabularyViewModel
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.utils.playPronunciation


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VocabularySearchBar(
    query: String,
    active: Boolean,
    suggestedWords: List<EmbeddedWord>,
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel = hiltViewModel(),
) {
    SearchBar(
        query = query,
        onQueryChange = viewModel::updateSearchQuery,
        active = active,
        onSearch = {},
        onActiveChange = viewModel::updateActive,
        modifier = modifier,
        placeholder = {
            Text(text = stringResource(R.string.type_at_least_two_letters))
        },
        leadingIcon = {
            LeadingIcon(
                activeSearch = active,
                onBackClicked = {
                    viewModel.updateActive()
                    viewModel.clearSearch()
                }
            )
        },
        trailingIcon = {
            AnimatedVisibility(visible = active) {
                IconButton(onClick = viewModel::clearSearch) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search query"
                    )
                }
            }
        }
    ) {
        SearchBarContent(
            items = suggestedWords,
            query = query,
            selectSuggestion = viewModel::selectSuggestedWord,
        )
    }
}

@Composable
private fun LeadingIcon(
    activeSearch: Boolean,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = activeSearch,
        label = "SearchIconAnimation",
        modifier = modifier,
    ) { active ->
        if (active) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to category list",
                )
            }
        } else {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
    }
}

@Composable
private fun SearchBarContent(
    items: List<EmbeddedWord>,
    query: String,
    selectSuggestion: (EmbeddedWord?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty() && query.isNotEmpty()) {
        Message(
            message = stringResource(R.string.no_words_found),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            icon = {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_medium))
        )
    } else {
        SuggestedWordList(
            suggestions = items,
            selectSuggestion = selectSuggestion,
            modifier = modifier
        )
    }
}


@Composable
private fun SuggestedWordList(
    suggestions: List<EmbeddedWord>,
    selectSuggestion: (EmbeddedWord?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LazyColumn(modifier) {
        items(suggestions) { embeddedWord ->
            val (word, categories, phonetics) = embeddedWord
            ListItem(
                headlineContent = { Text(text = "${word.value} - ${word.translation}") },
                supportingContent = { Text(text = categories.joinToString { it.name }) },
                trailingContent = {
                    IconButton(
                        onClick = {
                            context.playPronunciation(phonetics)
                        }) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = stringResource(R.string.play_pronunciation),
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .clickable {
                        selectSuggestion(embeddedWord)
                    }
                    .fillMaxWidth()
            )
        }
    }
}