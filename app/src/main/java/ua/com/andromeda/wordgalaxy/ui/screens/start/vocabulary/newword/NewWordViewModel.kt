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
import ua.com.andromeda.wordgalaxy.data.model.EMPTY_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class NewWordViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewWordUiState>(NewWordUiState.Default)
    val uiState: StateFlow<NewWordUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                categoryRepository.findAll().collect { categories ->
                    _uiState.update {
                        NewWordUiState.Success(
                            suggestedCategories = categories
                        )
                    }
                }
            }
        }
    }

    fun updateWord(value: String) {
        updateUiState {
            it.copy(
                word = value,
                isFormValid = isFormValid(value, it.translation, it.selectedCategories)
            )
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
            it.copy(
                translation = value,
                isFormValid = isFormValid(it.word, value, it.selectedCategories)
            )
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

    private fun errorState() = NewWordUiState.Error("Unexpected state")


    fun updateExampleText(index: Int, value: String) {
        updateUiState {
            val updatedExamples = it.examples.toMutableList()
            updatedExamples[index] = updatedExamples[index].copy(
                text = value
            )
            it.copy(examples = updatedExamples)
        }
    }

    fun updateExampleTranslation(index: Int, value: String) {
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
            val example = buildEmptyExample()
            it.copy(examples = it.examples + example)
        }
    }

    private fun buildEmptyExample() = Example(
        id = RANDOM.nextLong(),
        text = "",
        translation = "",
        wordId = 0
    )

    fun deleteExample(index: Int) {
        updateUiState {
            val examples = it.examples
            it.copy(examples = examples - examples[index])
        }
    }

    fun addCategory() {
        updateUiState {
            it.copy(
                selectedCategories = it.selectedCategories + (EMPTY_CATEGORY to false)
            )
        }
    }

    fun deleteCategory(index: Int) {
        updateUiState {
            val selectedCategories = it.selectedCategories
            it.copy(
                selectedCategories = selectedCategories - selectedCategories[index]
            )
        }
    }

    fun submitForm() = viewModelScope.launch(Dispatchers.IO) {
        val state = uiState.value
        if (state is NewWordUiState.Success) {
            val embeddedWord = buildEmbeddedWord(state)
            wordRepository.insert(embeddedWord)
        }
    }

    private fun buildEmbeddedWord(state: NewWordUiState.Success): EmbeddedWord {
        val formattedTranscription = formatTranscription(state.transcription)
        return EmbeddedWord(
            word = Word(
                value = state.word,
                translation = state.translation
            ),
            categories = state.selectedCategories
                .map { it.first }
                .filter { it.name.isNotBlank() }
                .distinct(),
            phonetics = listOf(
                Phonetic(text = formattedTranscription, audio = "")
            ),
            examples = state.examples
                .filter { it.text.isNotBlank() }
                .map { it.copy(id = 0) }
        )
    }

    fun updateCategory(index: Int, value: Category) {
        updateUiState {
            val modifiedSelectedCategories = it.selectedCategories
                .toMutableList()
                .apply { this[index] = value to false }
            it.copy(
                selectedCategories = modifiedSelectedCategories
            )
        }
    }

    fun updateCategoriesExpanded(index: Int, value: Boolean) {
        updateUiState {
            val modifiedSelectedCategories = it.selectedCategories.toMutableList().apply {
                this[index] = this[index].first to value
            }
            it.copy(selectedCategories = modifiedSelectedCategories)
        }
    }

    companion object {
        private val RANDOM = Random()
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

private fun isFormValid(
    word: String,
    translation: String,
    selectedCategories: List<Pair<Category, Boolean>>,
): Boolean =
    word.isNotBlank()
            && translation.isNotBlank()
            && selectedCategories.isNotEmpty()