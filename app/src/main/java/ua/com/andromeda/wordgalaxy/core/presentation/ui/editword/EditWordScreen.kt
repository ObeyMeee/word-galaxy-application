package ua.com.andromeda.wordgalaxy.core.presentation.ui.editword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.AddTextButton
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.CategoryList
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.ExistingWordsList
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.TextFields
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.WordFormScaffold

@Composable
fun EditWordScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WordFormScaffold(
        topAppBarTextRes = R.string.edit,
        navigateUp = navigateUp,
        onFabClick = {
            navigateTo(Destination.EditWord.ExamplesScreen())
        },
        fabEnabled = uiState.isFormValid,
        modifier = modifier,
    ) { innerPadding ->
        EditWordMain(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun EditWordMain(
    modifier: Modifier = Modifier,
    viewModel: EditWordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is EditWordUiState.Default -> CenteredLoadingSpinner(modifier)
        is EditWordUiState.Error -> Message(message = state.message, modifier = modifier)
        is EditWordUiState.Success -> {
            SuccessContent(
                state = state,
                viewModel = viewModel,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: EditWordUiState.Success,
    modifier: Modifier = Modifier,
    viewModel: EditWordViewModel = hiltViewModel(),
) {
    Column(modifier) {
        TextFields(
            word = state.word,
            updateWord = viewModel::updateWord,
            translation = state.translation,
            updateTranslation = viewModel::updateTranslation,
            transcription = state.transcription,
            updateTranscription = viewModel::updateTranscription,
            modifier = Modifier.fillMaxWidth()
        )
        ExistingWordsList(
            items = state.existingWords,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        AddTextButton(
            onClick = viewModel::addCategory,
            labelRes = R.string.add_category
        )
        CategoryList(
            onExpandedChange = viewModel::updateCategoriesExpanded,
            selectedCategories = state.selectedCategories,
            suggestedCategories = state.suggestedCategories,
            updateCategory = viewModel::updateCategory,
            deleteCategory = viewModel::deleteCategory
        )
    }
}
