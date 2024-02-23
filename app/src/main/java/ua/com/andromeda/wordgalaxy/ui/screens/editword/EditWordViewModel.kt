package ua.com.andromeda.wordgalaxy.ui.screens.editword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EMPTY_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class EditWordViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _uiState: MutableStateFlow<EditWordUiState> =
        MutableStateFlow(EditWordUiState.Default)
    val uiState: StateFlow<EditWordUiState> = _uiState

    init {
        val wordId = savedStateHandle.get<Long>("id") ?: throw Error("Cannot obtain word id")
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                combine(
                    wordRepository.findEmbeddedWordById(wordId),
                    categoryRepository.findAllChildCategories()
                ) { embeddedWord, categories ->
                    _uiState.update {
                        EditWordUiState.Success(
                            editableWord = embeddedWord,
                            suggestedCategories = categories,
                        )
                    }
                }.collect()
            }
        }
    }

    private fun updateUiState(
        action: (EditWordUiState.Success) -> EditWordUiState
    ) {
        _uiState.update { uiState ->
            if (uiState is EditWordUiState.Success) {
                action(uiState)
            } else {
                errorState()
            }
        }
    }

    private fun errorState() = EditWordUiState.Error()

    fun updateWord(value: String) {
        updateUiState {
            it.copy(
                word = value,
                isFormValid = isFormValid(it.word, value, it.selectedCategories)
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val existingWords = wordRepository.findWordsByValue(value).first()
            updateUiState {
                it.copy(existingWords = existingWords)
            }
        }
    }

    fun updateTranscription(value: String) =
        updateUiState {
            it.copy(transcription = value)
        }

    fun updateTranslation(value: String) {
        updateUiState {
            it.copy(
                translation = value,
                isFormValid = isFormValid(it.word, value, it.selectedCategories)
            )
        }
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

    fun submitForm() {
        val state = uiState.value
        if (state !is EditWordUiState.Success) return

        val editableWord = state.editableWord
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWord = editableWord.copy(
                word = editableWord.word.copy(
                    value = state.word,
                    translation = state.translation,
                ),
                categories = state.selectedCategories.map { it.first },
                examples = state.examples,
                phonetics = editableWord.phonetics
            )
            wordRepository.update(updatedWord)
        }
    }

    companion object {
        private val RANDOM = Random()
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