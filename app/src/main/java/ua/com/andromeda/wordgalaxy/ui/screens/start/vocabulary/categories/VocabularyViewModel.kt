package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.copyWordToMyCategory
import javax.inject.Inject

private const val MIN_SEARCH_QUERY_LENGTH = 2

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow<VocabularyUiState>(VocabularyUiState.Default)
    val uiState: StateFlow<VocabularyUiState> = _uiState

    private val coroutineDispatcher = Dispatchers.IO

    init {
        viewModelScope.launch(coroutineDispatcher) {
            observeCategories()
        }
    }

    private fun CoroutineScope.observeCategories() = launch {
        categoryRepository
            .findAllVocabularyCategories()
            .collect { categories ->
                _uiState.update {
                    VocabularyUiState.Success(vocabularyCategories = categories)
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

    fun updateSearchQuery(value: String) {
        updateState {
            it.copy(
                searchQuery = value
            )
        }
        viewModelScope.launch(coroutineDispatcher) {
            val modifiedQuery = value.trim()
            val suggestedWords = if (modifiedQuery.length >= MIN_SEARCH_QUERY_LENGTH) {
                wordRepository
                    .findWordsByValueOrTranslation(modifiedQuery)
                    .first()
                    .flatMap { embeddedWord ->
                        embeddedWord.categories
                            .map { category ->
                                embeddedWord.copy(categories = listOf(category))
                            }
                    }
            } else
                emptyList()

            updateState {
                it.copy(suggestedWords = suggestedWords)
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
                it.copy(activeSearch = false, suggestedWords = emptyList())
            } else {
                it.copy(searchQuery = "", suggestedWords = emptyList())
            }
        }
    }

    fun selectSuggestedWord(embeddedWord: EmbeddedWord? = null) {
        updateState {
            it.copy(selectedWord = embeddedWord)
        }
    }

    fun copyWordToMyCategory() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? VocabularyUiState.Success)?.let {
                val selectedWord = it.selectedWord
                if (selectedWord == null) {
                    _uiState.update {
                        VocabularyUiState.Error()
                    }
                } else {
                    wordRepository.copyWordToMyCategory(selectedWord)
                }
            }
            selectSuggestedWord()
        }
    }
}