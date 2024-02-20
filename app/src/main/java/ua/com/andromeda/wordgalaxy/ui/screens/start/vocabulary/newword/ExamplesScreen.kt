package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.ui.common.AddTextButton
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.VerticalSpacer
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun ExamplesScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            NewWordTopAppBar(
                titleRes = R.string.add_examples,
                onClickNavIcon = navigateUp
            )
        },
        floatingActionButton = {
            val label = stringResource(R.string.add)
            ExtendedFloatingActionButton(
                text = {
                    Text(text = label)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = label
                    )
                },
                onClick = viewModel::submitForm
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier
    ) { innerPadding ->
        ExamplesMain(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@Composable
private fun ExamplesMain(
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is NewWordUiState.Default -> CenteredLoadingSpinner()
        is NewWordUiState.Error -> Message(state.message)

        is NewWordUiState.Success -> {
            val examples = state.examples
            NoExamplesMessage(visible = examples.isEmpty())
            Column(modifier) {
                AddTextButton(
                    labelRes = R.string.add_example,
                    onClick = viewModel::addEmptyExample
                )
                VerticalSpacer(R.dimen.padding_medium)
                ExampleList(
                    examples = examples,
                    updateText = viewModel::updateExampleText,
                    updateTranslation = viewModel::updateExampleTranslation,
                    deleteExample = viewModel::deleteExample,
                )
            }
        }
    }
}

@Composable
private fun NoExamplesMessage(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { -it / 4 },
        exit = fadeOut() + slideOutVertically { it / 4 },
        modifier = modifier
    ) {
        Message(
            message = stringResource(R.string.clikc_button_to_add_some_examples),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            icon = {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExampleList(
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
        itemsIndexed(examples, key = { _, example -> example.id }) { i, example ->
            Card(modifier = Modifier.animateItemPlacement()) {
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
                                contentDescription = "Delete example ${i + 1}",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TextField(
                        value = example.text,
                        onValueChange = { updateText(i, it) },
                        label = {
                            Text(text = stringResource(R.string.text))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    VerticalSpacer(R.dimen.padding_medium)
                    TextField(
                        value = example.translation,
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