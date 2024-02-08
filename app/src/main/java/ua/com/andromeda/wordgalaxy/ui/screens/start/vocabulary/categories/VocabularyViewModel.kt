package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

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
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepositoryImpl

class VocabularyViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow<VocabularyUiState>(VocabularyUiState.Default)
    val uiState: StateFlow<VocabularyUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                categoryRepository
                    .findVocabularyCategories(null)
                    .collect { categories ->
                        _uiState.update {
                            VocabularyUiState.Success(categories)
                        }
                    }
            }
        }
    }

    private fun updateState(action: (VocabularyUiState.Success) -> VocabularyUiState.Success) {
        _uiState.update {
            if (it is VocabularyUiState.Success) {
                action(it)
            } else {
                VocabularyUiState.Error()
            }
        }
    }

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

    fun updateSearchQuery(value: String) {
        updateState {
            it.copy(
                searchQuery = value
            )
        }
        if (value.length > 2) {
            viewModelScope.launch(Dispatchers.IO) {
                val modifiedQuery = value.trim()
                wordRepository.findWordsByValueOrTranslation(modifiedQuery).collect { words ->
                    updateState {
                        it.copy(suggestedWords = words)
                    }
                }
            }
        }
    }

    fun updateActive(active: Boolean = false) {
        updateState {
            it.copy(activeSearch = active)
        }
    }

    fun clearSearch() {
        updateState {
            if (it.searchQuery.isEmpty()) {
                it.copy(activeSearch = false)
            } else {
                it.copy(searchQuery = "")
            }
        }
    }

    fun selectSuggestedWord(embeddedWord: EmbeddedWord? = null, open: Boolean = false) {
        updateState {
            it.copy(
                isWordActionDialogOpen = open,
                selectedWord = embeddedWord
            )
        }
    }

    fun copyWordToMyCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? VocabularyUiState.Success)?.let {
                val wordWithCategories = it.selectedWord?.toWordWithCategories()
                    ?: throw IllegalStateException("Selected word cannot be null")
                val updatedCategories = wordWithCategories.categories + MY_WORDS_CATEGORY
                wordRepository.updateWordWithCategories(
                    wordWithCategories.copy(categories = updatedCategories)
                )
            }
            selectSuggestedWord()
        }
    }

    companion object {
        private const val TAG = "VocabularyViewModel"
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordDao = application.appDatabase.wordDao()
                val categoryDao = application.appDatabase.categoryDao()
                VocabularyViewModel(
                    wordRepository = WordRepositoryImpl(wordDao),
                    categoryRepository = CategoryRepositoryImpl(categoryDao)
                )
            }
        }
    }
}