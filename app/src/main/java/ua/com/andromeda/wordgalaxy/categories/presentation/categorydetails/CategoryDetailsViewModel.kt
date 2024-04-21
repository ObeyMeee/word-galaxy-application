package ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails.components.WordSortOrder
import ua.com.andromeda.wordgalaxy.core.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.core.data.repository.word.copyWordToMyCategory
import ua.com.andromeda.wordgalaxy.core.data.repository.word.removeWordFromMyCategory
import ua.com.andromeda.wordgalaxy.core.domain.enums.Direction
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.reset
import ua.com.andromeda.wordgalaxy.core.domain.model.updateStatus
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen.ID_KEY
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination.Start.VocabularyScreen.CategoryDetailsScreen.WORD_ID_KEY
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _uiState: MutableStateFlow<CategoryDetailsUiState> =
        MutableStateFlow(CategoryDetailsUiState.Default)
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState

    private val coroutineDispatcher = Dispatchers.IO

    init {
        val categoryId = savedStateHandle.get<Long>(ID_KEY)
        if (categoryId == null) {
            _uiState.update { CategoryDetailsUiState.Error("Could not get category id") }
        } else {
            viewModelScope.launch(coroutineDispatcher) {
                val firstShownItem = savedStateHandle.get<Long>(WORD_ID_KEY)
                fetchCategory(categoryId)
                observeWordList(categoryId, firstShownItem)
            }
        }
    }

    private fun CoroutineScope.fetchCategory(categoryId: Long) = launch {
        categoryRepository.findById(categoryId).collect { category ->
            _uiState.update { state ->
                if (state is CategoryDetailsUiState.Success) {
                    state.copy(category = category)
                } else {
                    CategoryDetailsUiState.Success(category = category)
                }
            }
        }
    }

    private fun CoroutineScope.observeWordList(
        categoryId: Long,
        firstShownItem: Long?
    ) = launch {
        wordRepository.findWordsByCategoryId(categoryId).collect { words ->
            _uiState.update { state ->
                if (state is CategoryDetailsUiState.Success) {
                    state.copy(embeddedWords = words)
                } else {
                    val indexToScroll = words.indexOfFirst { it.word.id == firstShownItem }
                    CategoryDetailsUiState.Success(
                        embeddedWords = words,
                        indexToScroll = indexToScroll,
                    )
                }
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
                    errorState()
                }
            }
        }
    }

    fun resetWordProgress() {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            state.selectedWord?.let { embeddedWord ->
                resetWordProgress(embeddedWord)
            }
        }
    }

    fun resetWordProgress(embeddedWord: EmbeddedWord) = viewModelScope.launch(coroutineDispatcher) {
        addWordToQueue(embeddedWord)
        wordRepository.update(embeddedWord.word.reset())
    }

    fun copyWordToMyCategory() = viewModelScope.launch(coroutineDispatcher) {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            state.selectedWord?.let { embeddedWord ->
                addWordToQueue(embeddedWord)
                wordRepository.copyWordToMyCategory(embeddedWord)
            }
        }
    }

    fun removeWordFromMyCategory() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
                val processedWord = state.wordsInProcessQueue.firstOrNull()
                if (processedWord == null) {
                    _uiState.update { errorState() }
                } else {
                    wordRepository.removeWordFromMyCategory(processedWord)
                    removeWordFromQueue()
                }
            }
        }
    }

    private fun errorState(message: String = "Something went wrong") =
        CategoryDetailsUiState.Error(message)

    fun removeWordFromQueue() {
        updateUiState {
            val updatedQueue = it.wordsInProcessQueue.toMutableList()
            updatedQueue.removeFirst()
            it.copy(wordsInProcessQueue = updatedQueue)
        }
    }

    fun addWordToQueue(embeddedWord: EmbeddedWord) {
        updateUiState { state ->
            state.copy(
                wordsInProcessQueue = state.wordsInProcessQueue + embeddedWord
            )
        }
    }


    fun removeWord() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
                val removedWord = state.wordsInProcessQueue.first()
                wordRepository.remove(removedWord)
            }
            removeWordFromQueue()
        }
    }

    fun recoverWord() = viewModelScope.launch(coroutineDispatcher) {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            val recoveredWord = state.wordsInProcessQueue.first()
            wordRepository.update(recoveredWord)
            removeWordFromQueue()
        }
    }

    fun updateWordStatus(status: WordStatus) {
        (_uiState.value as? CategoryDetailsUiState.Success)?.let { state ->
            state.selectedWord?.let { embeddedWord ->
                updateWordStatus(embeddedWord, status)
            }
        }
    }

    fun updateWordStatus(
        embeddedWord: EmbeddedWord,
        status: WordStatus
    ) = viewModelScope.launch(coroutineDispatcher) {
        wordRepository.update(
            embeddedWord.word.updateStatus(status)
        )
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
            viewModelScope.launch(coroutineDispatcher) {
                val resetWords = state.embeddedWords.map { it.word.reset() }
                wordRepository.update(*resetWords.toTypedArray())
            }
        }
    }

    fun updateSortDirection() {
        updateUiState {
            val newDirection = if (it.direction == Direction.ASC)
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
