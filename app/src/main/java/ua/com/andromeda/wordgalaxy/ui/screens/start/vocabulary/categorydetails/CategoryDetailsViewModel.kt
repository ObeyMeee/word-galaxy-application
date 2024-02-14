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
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.reset
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.data.model.updateStatus
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen.ID_KEY
import ua.com.andromeda.wordgalaxy.utils.Direction
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
                    embeddedWords = words,
                    title = category?.name ?: "Category"
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun selectWord(word: EmbeddedWord? = null) {
        updateUiState {
            it.copy(
                selectedWord = word,
            )
        }
    }

    private fun updateUiState(action: (CategoryDetailsUiState.Success) -> CategoryDetailsUiState.Success) {
        viewModelScope.launch {
            _uiState.update {
                if (it is CategoryDetailsUiState.Success) {
                    action(it)
                } else {
                    CategoryDetailsUiState.Error()
                }
            }
        }
    }

    fun resetWordProgress() {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            viewModelScope.launch(Dispatchers.IO) {
                state.selectedWord?.let { embeddedWord ->
                    wordRepository.update(embeddedWord.word.reset())
                }
            }
        }
    }

    fun copyWordToMyCategory() {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            viewModelScope.launch(Dispatchers.IO) {
                state.selectedWord?.let { embeddedWord ->
                    val wordWithCategories = embeddedWord.toWordWithCategories()
                    wordRepository.updateWordWithCategories(
                        wordWithCategories.copy(
                            categories = wordWithCategories.categories + MY_WORDS_CATEGORY
                        )
                    )
                }
            }
        }
    }

    fun reportMistake() {
        TODO("Not yet implemented")
    }

    fun editWord() {
        TODO("Not yet implemented")
    }

    fun removeWord() {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            state.selectedWord?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    wordRepository.remove(it)
                }
            }
        }
    }

    fun updateWordStatus(status: WordStatus) {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            state.selectedWord?.let { embeddedWord ->
                viewModelScope.launch(Dispatchers.IO) {
                    wordRepository.update(
                        embeddedWord.word.updateStatus(status)
                    )
                }
            }
        }
    }

    fun expandTopAppBarMenu(expanded: Boolean = false) {
        updateUiState {
            it.copy(topAppBarMenuExpanded = expanded)
        }
    }

    fun openOrderDialog(visible: Boolean = false) {
        updateUiState {
            it.copy(orderDialogVisible = visible)
        }
    }

    fun selectSortOrder(value: WordSortOrder) {
        updateUiState { state ->
            state.copy(
                selectedSortOrder = value,
                orderDialogVisible = false,
                topAppBarMenuExpanded = false,
                embeddedWords = state.embeddedWords.sortedBy {
                    val word = it.word
                    if (value == WordSortOrder.BY_STATUS) {
                        word.status.name
                    } else {
                        word.value
                    }
                }
            )
        }
    }

    fun openConfirmResetProgressDialog(value: Boolean = false) {
        updateUiState {
            it.copy(resetProgressDialogVisible = value)
        }
    }

    fun resetCategoryProgress() {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            viewModelScope.launch(Dispatchers.IO) {
                val resetWords = state.embeddedWords.map { it.word.reset() }
                wordRepository.update(*resetWords.toTypedArray())
            }
        }
    }

    fun updateSortDirection() {
        updateUiState {
            val newDirection =
                if (it.direction == Direction.ASC)
                    Direction.DESC
                else
                    Direction.ASC

            it.copy(
                direction = newDirection,
                embeddedWords = it.embeddedWords.reversed()
            )
        }
    }
}
