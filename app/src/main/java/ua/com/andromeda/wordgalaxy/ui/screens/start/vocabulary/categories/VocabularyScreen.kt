package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

@Composable
fun VocabularyScreen(
    listState: LazyListState,
    navigateTo: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val viewModel: VocabularyViewModel = hiltViewModel()
    val vocabularyUiState by viewModel.uiState.collectAsState()

    when (val uiState = vocabularyUiState) {
        is VocabularyUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is VocabularyUiState.Error -> {
            Message(
                message = uiState.message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        is VocabularyUiState.Success -> {
            val coroutineScope = rememberCoroutineScope()
            val selectedWord = uiState.selectedWord
            WordActionsDialog(
                selectedWord = selectedWord,
                closeDialog = viewModel::selectSuggestedWord,
                copyWordToMyCategory = viewModel::copyWordToMyCategory,
                jumpToWord = {
                    navigateTo(
                        CategoryDetailsScreen(
                            selectedWord!!.categories[0].id,
                            selectedWord.word.value
                        )
                    )
                },
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
            VocabularySearchBar(
                state = uiState,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_small))
            )
            CategoryList(
                items = uiState.vocabularyCategories,
                listState = listState,
                fetchSubCategories = viewModel::fetchSubCategories,
                navigateTo = navigateTo,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_huge))
            )
        }
    }
}

@Composable
private fun WordActionsDialog(
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
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
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
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                            Text(text = stringResource(R.string.copy_to_my_category))
                        }
                    }
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun VocabularySearchBar(
    state: VocabularyUiState.Success,
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel = hiltViewModel(),
) {
    val query = state.searchQuery
    val notEmptyQuery = query.isNotEmpty()
    SearchBar(
        query = query,
        onQueryChange = viewModel::updateSearchQuery,
        active = state.activeSearch,
        onSearch = {},
        onActiveChange = viewModel::updateActive,
        modifier = modifier,
        placeholder = {
            Text(text = stringResource(R.string.search_for_words))
        },
        leadingIcon = {
            AnimatedContent(
                targetState = state.activeSearch,
                label = "SearchIconAnimation"
            ) { active ->
                if (active) {
                    IconButton(onClick = viewModel::updateActive) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                } else {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            }
        },
        trailingIcon = {
            AnimatedVisibility(visible = state.activeSearch) {
                IconButton(
                    onClick = viewModel::clearSearch,
                    enabled = notEmptyQuery
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search query"
                    )
                }
            }
        }
    ) {
        SearchBarContent(
            items = state.suggestedWords,
            query = state.searchQuery,
            selectSuggestion = viewModel::selectSuggestedWord,
        )
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

@Composable
fun CategoryList(
    items: List<VocabularyCategory>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    fetchSubCategories: (Int) -> Unit = {},
    navigateTo: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.add_category))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_category),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .size(dimensionResource(R.dimen.icon_size_largest))
                    )
                },
                modifier = Modifier.clickable(onClick = {
                    navigateTo(Destination.Start.VocabularyScreen.NewCategoryScreen())
                })
            )
        }
        items(items, key = { it.category.id }) {
            CategoryItem(
                vocabularyCategory = it,
                fetchSubCategories = fetchSubCategories,
                navigateTo = navigateTo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
fun CategoryItem(
    vocabularyCategory: VocabularyCategory,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit = {},
    fetchSubCategories: (Int) -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ExpandParentCategoryAnimation"
    )
    val category = vocabularyCategory.category

    val categoryName = category.name
    val context = LocalContext.current

    ListItem(
        headlineContent = {
            Text(text = categoryName)
        },
        modifier = modifier.clickable {
            expanded = !expanded
            fetchSubCategories(category.id.toInt())
        },
        leadingContent = {
            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .padding(dimensionResource(R.dimen.padding_smallest)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand),
                    modifier = Modifier.rotate(rotationAngle)
                )
                val iconRes = context.getCategoryIconIdentifier(categoryName)
                if (iconRes != RESOURCE_NOT_FOUND) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_size_largest))
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        },
        supportingContent = {
            Text(text = "${vocabularyCategory.totalWords} words")
        },
        trailingContent = {
            Text(text = "${vocabularyCategory.completedWords}%")
        }
    )
    AnimatedVisibility(
        visible = expanded
    ) {
        NestedCategories(
            items = vocabularyCategory.subcategories,
            navigateTo = navigateTo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
fun NestedCategories(
    items: List<VocabularyCategory>,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    Column(modifier) {
        items.forEach {
            ListItem(
                headlineContent = {
                    Text(text = it.category.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateTo(CategoryDetailsScreen(it.category.id))
                    },
                leadingContent = {
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(dimensionResource(R.dimen.padding_smallest)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.animals_category_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.icon_size_largest))
                                .padding(dimensionResource(R.dimen.padding_small))
                        )
                    }
                },
                supportingContent = {
                    Text(text = "${it.totalWords} words")
                },
                trailingContent = {
                    Text(text = "${it.completedWords}%")
                }
            )
        }
    }
}

@Preview
@Composable
fun CategoryItemPreview() {
    WordGalaxyTheme {
        Surface {
            CategoryItem(
                vocabularyCategory = VocabularyCategory(
                    category = Category(1, "Animals"),
                    totalWords = 100,
                    completedWords = 23f
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}