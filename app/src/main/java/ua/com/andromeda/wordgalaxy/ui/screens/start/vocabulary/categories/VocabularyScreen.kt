package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
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
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val viewModel: VocabularyViewModel = hiltViewModel()
    val vocabularyUiState by viewModel.uiState.collectAsState()

    when (val uiState = vocabularyUiState) {
        is VocabularyUiState.Default -> CenteredLoadingSpinner(modifier)
        is VocabularyUiState.Error -> Message(
            message = uiState.message,
            modifier = modifier,
        )

        is VocabularyUiState.Success -> {
            val selectedWord = uiState.selectedWord
            WordActionsDialog(
                selectedWord = selectedWord,
                closeDialog = viewModel::selectSuggestedWord,
                copyWordToMyCategory = viewModel::copyWordToMyCategory,
                jumpToWord = {
                    navigateTo(
                        CategoryDetailsScreen(
                            id = selectedWord!!.categories[0].id,
                            wordId = selectedWord.word.id
                        )
                    )
                },
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
fun CategoryList(
    items: List<VocabularyCategory>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    fetchSubCategories: (VocabularyCategory) -> Unit = {},
    navigateTo: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        item {
            AddCategory(
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
private fun AddCategory(modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(R.string.add_category))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .size(dimensionResource(R.dimen.icon_size_largest))
            )
        },
        modifier = modifier,
    )
}

@Composable
fun CategoryItem(
    vocabularyCategory: VocabularyCategory,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit = {},
    fetchSubCategories: (VocabularyCategory) -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(),
        label = "ExpandParentCategoryAnimation"
    )
    val categoryName = vocabularyCategory.category.name

    ListItem(
        headlineContent = {
            Text(text = categoryName)
        },
        modifier = modifier.clickable {
            expanded = !expanded
            fetchSubCategories(vocabularyCategory)
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
                CategoryIcon(
                    category = categoryName,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size_largest))
                        .padding(dimensionResource(R.dimen.padding_small))
                )
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
    AnimatedVisibility(visible = expanded) {
        NestedCategories(
            items = vocabularyCategory.subcategories,
            navigateTo = navigateTo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .heightIn(0.dp, 5000.dp)
        )
    }
}

@Composable
private fun CategoryIcon(
    category: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val iconRes = context.getCategoryIconIdentifier(category)
    val painter = if (iconRes != RESOURCE_NOT_FOUND)
        painterResource(iconRes)
    else
        rememberVectorPainter(Icons.Default.BrokenImage)

    Icon(
        painter = painter,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier,
    )
}

@Composable
fun NestedCategories(
    items: List<VocabularyCategory>,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    LazyColumn(modifier) {
        items(items, key = { it.category.id }) {
            val category = it.category
            ListItem(
                headlineContent = {
                    Text(text = category.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateTo(CategoryDetailsScreen(category.id))
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