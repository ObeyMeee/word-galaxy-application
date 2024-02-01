package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NewWordViewModel = viewModel(factory = NewWordViewModel.factory)
    Scaffold(
        topBar = {
            NewWordTopAppBar(onClickNavIcon = navigateUp)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.add))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                },
                onClick = viewModel::submitForm
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier
    ) { innerPadding ->
        NewWordMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NewWordTopAppBar(
    modifier: Modifier = Modifier,
    onClickNavIcon: () -> Unit = {}
) {
    val viewModel: NewWordViewModel = viewModel(factory = NewWordViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is NewWordUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is NewWordUiState.Error -> {
            Text(text = state.message)
        }

        is NewWordUiState.Success -> {
            TopAppBar(
                title = {
                    Text(text = "Add word")
                    ExposedDropdownMenuBox(
                        expanded = state.categoriesExpanded,
                        onExpandedChange = viewModel::updateCategoriesExpanded
                    ) {
                        TextField(
                            value = state.selectedCategory.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(state.categoriesExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = state.categoriesExpanded,
                            onDismissRequest = viewModel::updateCategoriesExpanded
                        ) {
                            state.categories.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = it.name)
                                    },
                                    onClick = {
                                        viewModel.updateCategory(it)
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = onClickNavIcon) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordMain(modifier: Modifier = Modifier) {
    val viewModel: NewWordViewModel = viewModel(factory = NewWordViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is NewWordUiState.Success) {
        val state = uiState as NewWordUiState.Success
        val examples = state.examples
        val focusRequester = remember { FocusRequester() }

        Column(modifier.verticalScroll(rememberScrollState())) {
            TextField(
                value = state.word,
                onValueChange = viewModel::updateWord,
                label = {
                    Text(text = "${stringResource(R.string.word)}*")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            VerticalSpacer()
            TextField(
                value = state.translation,
                onValueChange = viewModel::updateTranslation,
                label = {
                    Text(text = "${stringResource(R.string.translation)}*")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            VerticalSpacer()
            TextField(
                value = state.transcription,
                onValueChange = viewModel::updateTranscription,
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
                modifier = Modifier.fillMaxWidth()
            )
            if (examples.isNotEmpty()) {
                VerticalSpacer()
            }
            ExistingWordsList(
                items = (uiState as NewWordUiState.Success).existingWords,
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.padding_small)
                )
            )
            ExampleList(
                examples = examples,
                updateText = viewModel::updateText,
                updateTranslation = viewModel::updateTranslation,
                deleteExample = viewModel::deleteExample,
                modifier = Modifier
                    .fillMaxWidth()
                    // TODO:  
                    .heightIn(0.dp, 2000.dp)
            )
            TextButton(
                onClick = viewModel::addEmptyExample,
                modifier = Modifier.offset(y = dimensionResource(R.dimen.offset_smaller))
            ) {
                Row(
                    modifier = Modifier.padding(
                        dimensionResource(R.dimen.padding_small)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_example)
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                    Text(text = stringResource(R.string.add_example))
                }
            }
        }
    }
}

@Composable
fun ExistingWordsList(
    items: List<ExistingWord>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedContent(
        targetState = items,
        label = "ExistingWordsAnimation"
    ) { words ->
        Column(modifier) {
            if (words.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.this_word_already_exists),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            words.forEach { existingWord ->
                Row(
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.padding_small))
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = existingWord.translation,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                    existingWord.categories.forEach { category ->
                        val iconRes = context.getCategoryIconIdentifier(category)
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleList(
    examples: List<Example>,
    modifier: Modifier = Modifier,
    updateText: (index: Int, value: String) -> Unit = { _, _ -> },
    updateTranslation: (index: Int, value: String) -> Unit = { _, _ -> },
    deleteExample: (index: Int) -> Unit = { _ -> },
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_mediumish))
    ) {
        itemsIndexed(examples) { i, example ->
            AnimatedContent(
                targetState = example,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                },
                label = ""
            ) { targetExample ->
                Card {
                    Column(
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_mediumish)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = -dimensionResource(R.dimen.offset_small)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(R.string.example)} ${i + 1}",
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            IconButton(
                                onClick = { deleteExample(i) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        TextField(
                            value = targetExample.text,
                            onValueChange = { updateText(i, it) },
                            label = {
                                Text(text = stringResource(R.string.text))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        VerticalSpacer()
                        TextField(
                            value = targetExample.translation,
                            onValueChange = { updateTranslation(i, it) },
                            label = {
                                Text(text = stringResource(R.string.translation))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(
        modifier.height(
            dimensionResource(R.dimen.padding_medium)
        )
    )
}

@Preview
@Composable
fun ExampleListPreview() {
    WordGalaxyTheme {
        Surface {
            ExampleList(
                examples = listOf(
                    Example(text = "", translation = "", wordId = 0),
                    Example(text = "", translation = "", wordId = 0),
                    Example(text = "", translation = "", wordId = 0)
                )
            )
        }
    }
}