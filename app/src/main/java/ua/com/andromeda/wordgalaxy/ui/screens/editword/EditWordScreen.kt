package ua.com.andromeda.wordgalaxy.ui.screens.editword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.AddTextButton
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.TitledTopAppBar
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.CategoryList
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.EnabledFloatingActionButton
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.TextFields

@Composable
fun EditWordScreen(
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TitledTopAppBar(
                titleRes = R.string.edit,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            EnabledFloatingActionButton(
                textRes = R.string.next,
                enabled = uiState.isFormValid,
                onClick = {
                    navigateTo(Destination.EditWord.ExamplesScreen())
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { innerPadding ->
        EditWordMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun EditWordMain(
    modifier: Modifier = Modifier
) {
    val viewModel: EditWordViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is EditWordUiState.Default -> CenteredLoadingSpinner(modifier)
        is EditWordUiState.Error -> Message(message = state.message, modifier = modifier)

        is EditWordUiState.Success -> {
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
//                ExistingWordsList(
//                    items = state.existingWords,
//                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
//                )
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
    }
}
