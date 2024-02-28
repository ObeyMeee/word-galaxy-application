package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier

@Composable
fun VocabularyScreen(
    listState: LazyListState,
    navigateTo: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val viewModel: VocabularyViewModel = hiltViewModel()
    val vocabularyUiState by viewModel.uiState.collectAsState()

    when (val uiState = vocabularyUiState) {
        is VocabularyUiState.Default -> CenteredLoadingSpinner()
        is VocabularyUiState.Error -> Message(uiState.message)
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
                modifier = Modifier.clickable {
                    navigateTo(Destination.Start.VocabularyScreen.NewCategoryScreen())
                }
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
            val roundedPercentage = getFormattedPercentage(vocabularyCategory.completedWords)
            Text(text = "$roundedPercentage%")
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
                    val roundedPercentage = getFormattedPercentage(it.completedWords)
                    Text(text = "$roundedPercentage%")
                }
            )
        }
    }
}

private fun getFormattedPercentage(value: Number) =
    String.format("%.2f", value)

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