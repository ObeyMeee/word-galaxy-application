package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepositoryImpl

class CategoryDetailsViewModel(
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

    companion object {
        private const val TAG = "CategoryDetailsViewModel"

        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordDao = application.appDatabase.wordDao()
                val wordRepository = WordRepositoryImpl(wordDao)
                CategoryDetailsViewModel(
                    wordRepository,
                    SavedStateHandle()
                )
            }
        }
    }
}
