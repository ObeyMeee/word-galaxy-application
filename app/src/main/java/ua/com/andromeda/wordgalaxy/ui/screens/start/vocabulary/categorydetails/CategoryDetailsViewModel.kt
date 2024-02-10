package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen.ID_KEY
import javax.inject.Inject

private const val TAG = "CategoryDetailsViewModel"

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _uiState: MutableStateFlow<CategoryDetailsUiState> =
        MutableStateFlow(CategoryDetailsUiState.Default)
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState

    init {
        val categoryId = savedStateHandle.get<Long>(ID_KEY)
            ?: throw IllegalStateException("category id not found")
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                wordRepository.findWordsByCategoryId(categoryId),
                categoryRepository.findById(categoryId)
            ) { words, category ->
                CategoryDetailsUiState.Success(
                    wordsAndPhonetics = words,
                    title = category?.name ?: "Category"
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }
}
