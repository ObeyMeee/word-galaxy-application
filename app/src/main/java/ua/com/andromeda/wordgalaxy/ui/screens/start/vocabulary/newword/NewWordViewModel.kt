package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import javax.inject.Inject

private const val TAG = "NewWordViewModel"

@HiltViewModel
class NewWordViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewWordUiState>(NewWordUiState.Default)
    val uiState: StateFlow<NewWordUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                val categories = categoryRepository.findAllChildCategories().first()
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
        viewModelScope.launch(Dispatchers.IO) {
            val existingWords = wordRepository.findWordsByValue(value).first()
            updateUiState {
                it.copy(existingWords = existingWords)
            }
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

        val embeddedWord = buildEmbeddedWord(state)
        wordRepository.insert(embeddedWord)
    }

    private fun buildEmbeddedWord(state: NewWordUiState.Success): EmbeddedWord {
        val formattedTranscription = formatTranscription(state.transcription)
        return EmbeddedWord(
            word = Word(
                value = state.word,
                translation = state.translation
            ),
            categories = listOf(state.selectedCategory),
            phonetics = listOf(
                Phonetic(text = formattedTranscription, audio = "")
            ),
            examples = state.examples
        )
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
}

private fun formatTranscription(transcription: String): String {
    if (transcription.isBlank()) return ""

    val formattedTranscription = transcription.trim().lowercase()
    return buildString {
        if (formattedTranscription.first() != '/') {
            append("/")
        }
        append(formattedTranscription)
        if (formattedTranscription.last() != '/') {
            append("/")
        }
    }
}