package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier

@Composable
fun VocabularyScreen(
    listState: LazyListState,
    navController: NavController = rememberNavController(),
) {
    val viewModel: VocabularyViewModel = viewModel(factory = VocabularyViewModel.factory)
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
            val vocabularyCategories = uiState.vocabularyCategories
            CategoryList(
                items = vocabularyCategories,
                listState = listState,
                fetchSubCategories = viewModel::fetchSubCategories,
                navigateToAddCategory = {
                    navController.navigate(Destination.Start.VocabularyScreen.NewCategoryScreen())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryList(
    items: List<VocabularyCategory>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    fetchSubCategories: (Int) -> Unit = {},
    navigateToAddCategory: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        item {
            ListItem(
                headlineText = {
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
                modifier = Modifier.clickable(onClick = navigateToAddCategory)
            )
        }
        items(items, key = { it.category.id }) {
            CategoryItem(
                vocabularyCategory = it,
                fetchSubCategories = fetchSubCategories,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    vocabularyCategory: VocabularyCategory,
    modifier: Modifier = Modifier,
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
        headlineText = {
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
        supportingText = {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NestedCategories(
    items: List<VocabularyCategory>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        items.forEach {
            ListItem(
                headlineText = {
                    Text(text = it.category.name)
                },
                modifier = Modifier.fillMaxWidth(),
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
                supportingText = {
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