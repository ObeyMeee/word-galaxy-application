package ua.com.andromeda.wordgalaxy.ui.screens.editword

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.wordform.examples.ExampleListFormScaffold
import ua.com.andromeda.wordgalaxy.ui.common.wordform.examples.ExamplesContainer

@Composable
fun ExamplesScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: EditWordViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    ExampleListFormScaffold(
        topAppBarTextRes = R.string.edit_examples,
        floatingActionButtonTextRes = R.string.save,
        navigateUp = navController::navigateUp,
        onFloatingActionButtonClick = {
            viewModel.submitForm()
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Word has been successfully saved",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Long,
                )
            }
            repeat(2) { navController.popBackStack() }
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
    viewModel: EditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is EditWordUiState.Default -> CenteredLoadingSpinner()
        is EditWordUiState.Error -> Message(state.message)
        is EditWordUiState.Success -> {
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