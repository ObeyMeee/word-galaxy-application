package ua.com.andromeda.wordgalaxy.ui.common.wordform

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier


@Composable
fun TextFields(
    word: String,
    updateWord: (String) -> Unit,
    translation: String,
    updateTranslation: (String) -> Unit,
    transcription: String,
    updateTranscription: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ) {
        val fillMaxWidthModifier = Modifier.fillMaxWidth()
        val nextKeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)

        TextField(
            value = word,
            onValueChange = updateWord,
            label = {
                Text(text = "${stringResource(R.string.word)}*")
            },
            singleLine = true,
            keyboardOptions = nextKeyboardOptions,
            modifier = fillMaxWidthModifier.focusRequester(focusRequester)
        )
        TextField(
            value = translation,
            onValueChange = updateTranslation,
            label = {
                Text(text = "${stringResource(R.string.translation)}*")
            },
            singleLine = true,
            keyboardOptions = nextKeyboardOptions,
            modifier = fillMaxWidthModifier
        )
        TextField(
            value = transcription,
            onValueChange = updateTranscription,
            label = {
                Text(text = stringResource(R.string.transcription_optional))
            },
            leadingIcon = {
                Text(text = "/")
            },
            trailingIcon = {
                Text(text = "/")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = fillMaxWidthModifier
        )
    }
}

@Composable
fun ExistingWordsList(
    items: List<ExistingWord>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = items,
        label = "ExistingWordsAnimation",
        modifier = modifier,
    ) { words ->
        Column {
            if (words.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.this_word_already_exists),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            words.forEach {
                ExistingWordItem(
                    item = it,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.padding_small))
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                )
            }
        }
    }
}

@Composable
private fun ExistingWordItem(
    item: ExistingWord,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.translation,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
        )
        item.categories.forEach { category ->
            val iconRes = context.getCategoryIconIdentifier(category)
            if (iconRes != RESOURCE_NOT_FOUND) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size_largest))
                        .padding(dimensionResource(R.dimen.padding_small)),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CategoryList(
    onExpandedChange: (index: Int, value: Boolean) -> Unit,
    suggestedCategories: List<Category>,
    selectedCategories: List<Pair<Category, Boolean>>,
    updateCategory: (Int, Category) -> Unit,
    deleteCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium)
        ),
    ) {
        itemsIndexed(selectedCategories, key = { i, _ -> i }) { i, selectedCategory ->
            CategoryItem(
                item = selectedCategory,
                index = i,
                onExpandedChange = onExpandedChange,
                suggestedCategories = suggestedCategories,
                updateCategory = updateCategory,
                deleteCategory = deleteCategory,
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun CategoryItem(
    item: Pair<Category, Boolean>,
    index: Int,
    onExpandedChange: (index: Int, value: Boolean) -> Unit,
    suggestedCategories: List<Category>,
    updateCategory: (Int, Category) -> Unit,
    deleteCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryDropdown(
            item = item,
            index = index,
            onExpandedChange = onExpandedChange,
            suggestedCategories = suggestedCategories,
            updateCategory = updateCategory,
        )
        DeleteCategoryIcon { deleteCategory(index) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CategoryDropdown(
    item: Pair<Category, Boolean>,
    onExpandedChange: (index: Int, value: Boolean) -> Unit,
    index: Int,
    suggestedCategories: List<Category>,
    updateCategory: (Int, Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (category, expanded) = item
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange(index, it)
        },
        modifier = modifier,
    ) {
        TextField(
            value = category.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(index, false) },
        ) {
            suggestedCategories.forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.name)
                    },
                    onClick = {
                        updateCategory(index, it)
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteCategoryIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete_category),
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
        )
    }
}

@Preview
@Composable
fun CategoryListPreview() {
    WordGalaxyTheme {
        Surface {
            CategoryList(
                onExpandedChange = { _, _ -> },
                suggestedCategories = emptyList(),
                selectedCategories = listOf(MY_WORDS_CATEGORY to false, MY_WORDS_CATEGORY to false),
                updateCategory = { _, _ -> },
                deleteCategory = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    }
}