package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepositoryImpl

class VocabularyViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    fun fetchSubCategories(parentCategoryId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository
                .findVocabularyCategories(parentCategoryId)
                .collect { subcategories ->
                    _uiState.update { uiState ->
                        if (uiState is VocabularyUiState.Success) {
                            uiState.copy(
                                uiState.vocabularyCategories.map {
                                    it.copy(subcategories = subcategories)
                                }
                            )
                        } else {
                            throw IllegalStateException("Unexpected state")
                        }
                    }
                }
        }


    private var _uiState = MutableStateFlow<VocabularyUiState>(VocabularyUiState.Default)
    val uiState: StateFlow<VocabularyUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                categoryRepository
                    .findVocabularyCategories(null)
                    .collect { categories ->
                        Log.d(TAG, categories.toString())
                    _uiState.update {
                        VocabularyUiState.Success(categories)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "VocabularyViewModel"
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val categoryDao = application.appDatabase.categoryDao()
                VocabularyViewModel(CategoryRepositoryImpl(categoryDao))
            }
        }
    }
}