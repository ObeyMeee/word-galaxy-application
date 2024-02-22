package ua.com.andromeda.wordgalaxy.ui.screens.editword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.AddTextButton
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.TitledTopAppBar
import ua.com.andromeda.wordgalaxy.ui.common.VerticalSpacer
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.ExampleList
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NoExamplesMessage

@Composable
fun ExamplesScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditWordViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TitledTopAppBar(
                titleRes = R.string.edit_examples,
                navigateUp = navigateUp,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.save))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                    )
                },
                onClick = {
                    viewModel.submitForm()
                    repeat(2) { navigateUp() }
                }
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
    viewModel: EditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is EditWordUiState.Default -> CenteredLoadingSpinner()
        is EditWordUiState.Error -> Message(state.message)

        is EditWordUiState.Success -> {
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