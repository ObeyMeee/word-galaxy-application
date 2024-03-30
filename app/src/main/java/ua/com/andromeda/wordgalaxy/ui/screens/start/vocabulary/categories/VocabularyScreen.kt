package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.iconpicker.ImageUtil
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun VocabularyScreen(
    listState: LazyListState,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val viewModel: VocabularyViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is VocabularyUiState.Default -> CenteredLoadingSpinner(modifier)
        is VocabularyUiState.Error -> Message(
            message = state.message,
            modifier = modifier,
        )

        is VocabularyUiState.Success -> {
            val selectedWord = state.selectedWord
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
                state = state,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_small))
            )
            CategoryList(
                items = state.vocabularyCategories,
                listState = listState,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateTo(CategoryDetailsScreen(it.category.id))
                    }
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
                    .padding(dimensionResource(R.dimen.padding_smaller))
            )
        },
        modifier = modifier,
    )
}

@Composable
fun CategoryItem(
    vocabularyCategory: VocabularyCategory,
    modifier: Modifier = Modifier,
) {
    val category = vocabularyCategory.category
    val categoryName = category.name
    ListItem(
        headlineContent = {
            Text(text = categoryName)
        },
        modifier = modifier,
        leadingContent = {
            CategoryIcon(
                category = category,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .size(dimensionResource(R.dimen.icon_size_largest))
                    .padding(dimensionResource(R.dimen.padding_smaller))
            )
        },
        supportingContent = {
            Text(text = "${vocabularyCategory.totalWords} words")
        },
        trailingContent = {
            val roundedPercentage = getFormattedPercentage(vocabularyCategory.completedWords)
            Text(text = "$roundedPercentage%")
        }
    )
}

@Composable
private fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
) {
    val painter = if (category.materialIconId != null)
        rememberVectorPainter(ImageUtil.createImageVector(category.materialIconId))
    else if (category.customIconId != null)
        painterResource(category.customIconId)
    else rememberVectorPainter(Icons.Default.BrokenImage)

    Icon(
        painter = painter,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier,
    )
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