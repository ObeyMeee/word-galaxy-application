package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

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
import ua.com.andromeda.wordgalaxy.ui.common.AddTextButton
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.wordform.CategoryList
import ua.com.andromeda.wordgalaxy.ui.common.wordform.ExistingWordsList
import ua.com.andromeda.wordgalaxy.ui.common.wordform.TextFields
import ua.com.andromeda.wordgalaxy.ui.common.wordform.WordFormScaffold
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

@Composable
fun NewWordScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    WordFormScaffold(
        topAppBarTextRes = R.string.new_word,
        navigateUp = navigateUp,
        onFabClick = {
            navigateTo(Destination.Start.VocabularyScreen.NewWord.ExamplesScreen())
        },
        fabEnabled = uiState.isFormValid,
        modifier = modifier,
    ) { innerPadding ->
        NewWordMain(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@Composable
private fun NewWordMain(
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is NewWordUiState.Default -> CenteredLoadingSpinner(modifier)
        is NewWordUiState.Error -> Message(message = state.message, modifier = modifier)
        is NewWordUiState.Success -> {
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
    state: NewWordUiState.Success,
    modifier: Modifier = Modifier,
    viewModel: NewWordViewModel = hiltViewModel(),
) {
    Column(modifier) {
        TextFields(
            word = state.word,
            updateWord = viewModel::updateWord,
            translation = state.translation,
            updateTranslation = viewModel::updateTranslation,
            transcription = state.transcription,
            updateTranscription = viewModel::updateTranscription,
            modifier = Modifier.fillMaxWidth(),
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
            selectedCategories = state.selectedCategories,
            suggestedCategories = state.suggestedCategories,
            onExpandedChange = viewModel::updateCategoriesExpanded,
            updateCategory = viewModel::updateCategory,
            deleteCategory = viewModel::deleteCategory,
        )
    }
}