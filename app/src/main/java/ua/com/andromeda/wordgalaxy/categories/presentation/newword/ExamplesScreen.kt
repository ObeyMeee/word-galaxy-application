package ua.com.andromeda.wordgalaxy.categories.presentation.newword

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.examples.ExampleListFormScaffold
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.examples.ExamplesContainer

@Composable
fun ExamplesScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    ExampleListFormScaffold(
        topAppBarTextRes = R.string.add_examples,
        floatingActionButtonTextRes = R.string.add,
        navigateUp = navigateUp,
        onFloatingActionButtonClick = {
            viewModel.submitForm()
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Word has been added to your vocabulary",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Long,
                )
            }
            navigateTo(Destination.Start.VocabularyScreen.CategoriesScreen())
        },
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
            ExamplesContainer(
                examples = state.examples,
                modifier = modifier,
                addExample = viewModel::addEmptyExample,
                updateExampleText = viewModel::updateExampleText,
                updateExampleTranslation = viewModel::updateExampleTranslation,
                deleteExample = viewModel::deleteExample
            )
        }
    }
}