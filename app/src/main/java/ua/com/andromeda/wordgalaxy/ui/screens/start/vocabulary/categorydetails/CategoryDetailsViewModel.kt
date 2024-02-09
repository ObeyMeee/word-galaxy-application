package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import javax.inject.Inject

private const val TAG = "CategoryDetailsViewModel"

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _uiState: MutableStateFlow<CategoryDetailsUiState> =
        MutableStateFlow(CategoryDetailsUiState.Default)
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState

    init {
        val categoryId = savedStateHandle.get<Long>("categoryId")
            ?: throw IllegalStateException("categoryId not found")
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                wordRepository.findWordsByCategoryId(categoryId).collect { words ->
                    _uiState.update {
                        CategoryDetailsUiState.Success(wordsAndPhonetics = words)
                    }
                }
            }
        }
    }
}
