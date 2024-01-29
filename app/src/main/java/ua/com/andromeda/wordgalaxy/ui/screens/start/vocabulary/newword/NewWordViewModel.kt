package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepositoryImpl

class NewWordViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewWordUiState>(NewWordUiState.Default)
    val uiState: StateFlow<NewWordUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                val categories = categoryRepository.findChildCategories().first()
                NewWordUiState.Success(
                    categories = categories
                )
            }
        }
    }

    fun updateWord(value: String) {
        updateUiState {
            it.copy(word = value)
        }
    }

    fun updateTranscription(value: String) {
        updateUiState {
            it.copy(transcription = value)
        }
    }

    fun updateTranslation(value: String) {
        updateUiState {
            it.copy(translation = value)
        }
    }

    private fun updateUiState(
        action: (NewWordUiState.Success) -> NewWordUiState
    ) {
        _uiState.update { uiState ->
            if (uiState is NewWordUiState.Success) {
                action(uiState)
            } else {
                errorState()
            }
        }
    }

    private fun errorState(): Nothing {
        throw IllegalStateException("Unexpected state")
    }

    fun updateText(index: Int, value: String) {
        updateUiState {
            val updatedExamples = it.examples.toMutableList()
            updatedExamples[index] = updatedExamples[index].copy(
                text = value
            )
            it.copy(examples = updatedExamples)
        }
    }

    fun updateTranslation(index: Int, value: String) {
        updateUiState {
            val updatedExamples = it.examples.toMutableList()
            updatedExamples[index] = updatedExamples[index].copy(
                translation = value
            )
            it.copy(examples = updatedExamples)
        }
    }

    fun addEmptyExample() {
        updateUiState {
            val example = Example(
                text = "",
                translation = "",
                wordId = 0
            )
            it.copy(examples = it.examples + example)
        }
    }

    fun deleteExample(index: Int) {
        updateUiState {
            val examples = it.examples
            it.copy(examples = examples - examples[index])
        }
    }

    fun submitForm() = viewModelScope.launch(Dispatchers.IO) {
        val state = uiState.value
        if (state !is NewWordUiState.Success) return@launch

        val embeddedWord = EmbeddedWord(
            word = Word(
                value = state.word,
                translation = state.translation
            ),
            categories = listOf(),
            phonetics = listOf(
                Phonetic(text = state.transcription, audio = "")
            ),
            examples = state.examples
        )
        wordRepository.insert(embeddedWord)
    }

    fun updateCategory(value: Category) {
        updateUiState {
            it.copy(
                selectedCategory = value,
                categoriesExpanded = false
            )
        }
    }

    fun updateCategoriesExpanded(value: Boolean = false) {
        updateUiState {
            it.copy(categoriesExpanded = value)
        }
    }

    companion object {
        const val TAG = "NewWordViewModel"
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordDao = application.appDatabase.wordDao()
                val categoryDao = application.appDatabase.categoryDao()
                NewWordViewModel(
                    wordRepository = WordRepositoryImpl(wordDao),
                    categoryRepository = CategoryRepositoryImpl(categoryDao)
                )
            }
        }
    }
}