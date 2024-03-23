package ua.com.andromeda.wordgalaxy.ui.common.wordform.examples

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.ui.common.AddTextButton
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.VerticalSpacer
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun ExamplesContainer(
    examples: List<Example>,
    updateExampleText: (index: Int, value: String) -> Unit,
    updateExampleTranslation: (index: Int, value: String) -> Unit,
    deleteExample: (index: Int) -> Unit,
    addExample: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NoExamplesMessage(visible = examples.isEmpty())
    Column(modifier) {
        AddTextButton(
            labelRes = R.string.add_example,
            onClick = addExample
        )
        VerticalSpacer(R.dimen.padding_medium)
        ExampleList(
            examples = examples,
            updateText = updateExampleText,
            updateTranslation = updateExampleTranslation,
            deleteExample = deleteExample,
        )
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
            message = stringResource(R.string.click_button_to_add_some_examples),
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
            ExampleItem(
                index = i,
                example = example,
                deleteExample = deleteExample,
                updateText = updateText,
                updateTranslation = updateTranslation,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun ExampleItem(
    index: Int,
    example: Example,
    deleteExample: (index: Int) -> Unit,
    updateText: (index: Int, value: String) -> Unit,
    updateTranslation: (index: Int, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_mediumish))) {
            val fillMaxWidth = Modifier.fillMaxWidth()
            CardHeader(
                index = index,
                deleteExample = deleteExample,
                modifier = fillMaxWidth
                    .offset(y = -dimensionResource(R.dimen.offset_small))
            )
            TextField(
                value = example.text,
                onValueChange = { updateText(index, it) },
                label = {
                    Text(text = stringResource(R.string.text))
                },
                modifier = fillMaxWidth
            )
            VerticalSpacer(R.dimen.padding_medium)
            TextField(
                value = example.translation,
                onValueChange = { updateTranslation(index, it) },
                label = {
                    Text(text = stringResource(R.string.translation))
                },
                modifier = fillMaxWidth
            )
        }
    }
}

@Composable
private fun CardHeader(
    index: Int,
    deleteExample: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val number = index + 1
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(R.string.example)} $number",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(
            onClick = { deleteExample(index) },
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete example $number",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun ExamplesContainerPreview() {
    WordGalaxyTheme {
        Surface {
            ExamplesContainer(
                examples = listOf(
                    Example(id = 1, text = "", translation = "", wordId = 0),
                    Example(id = 2, text = "", translation = "", wordId = 0),
                    Example(id = 3, text = "", translation = "", wordId = 0)
                ),
                updateExampleText = { _, _ -> },
                updateExampleTranslation = { _, _ -> },
                deleteExample = {},
                addExample = {},
            )
        }
    }
}

@Preview
@Composable
fun ExamplesContainerEmptyPreview() {
    WordGalaxyTheme {
        Surface {
            ExamplesContainer(
                examples = emptyList(),
                updateExampleText = { _, _ -> },
                updateExampleTranslation = { _, _ -> },
                deleteExample = {},
                addExample = {},
            )
        }
    }
}