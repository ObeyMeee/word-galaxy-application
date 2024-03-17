package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.repeat
import ua.com.andromeda.wordgalaxy.data.model.reset
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.common.FlashcardViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewWordsViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel(), FlashcardViewModel {
    private var _uiState: MutableStateFlow<ReviewWordsUiState> =
        MutableStateFlow(ReviewWordsUiState.Default)
    val uiState: StateFlow<ReviewWordsUiState> = _uiState

    init {
        fetchUiState()
    }

    private fun fetchUiState() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                wordRepository.findWordToReview(),
                wordRepository.countReviewedWordsToday(),
                wordRepository.countWordsToReview()
            ) { wordToReview, reviewedToday, amountWordsToReview ->
                _uiState.update {
                    if (wordToReview == null)
                        errorUiState("No words to repeat. You do a great work!")
                    else
                        ReviewWordsUiState.Success(
                            wordToReview = wordToReview,
                            reviewedToday = reviewedToday,
                            amountWordsToReview = amountWordsToReview,
                        )
                }
            }.collect()
        }
    }

    private fun updateUiState(
        errorMessage: String = "Something went wrong",
        action: (ReviewWordsUiState.Success) -> ReviewWordsUiState.Success
    ) {
        _uiState.update { uiState ->
            if (uiState is ReviewWordsUiState.Success) {
                action(uiState)
            } else {
                errorUiState(errorMessage)
            }
        }
    }

    private fun errorUiState(message: String = "Something went wrong") =
        ReviewWordsUiState.Error(message)

    fun repeatWord() {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is ReviewWordsUiState.Success) {
                val currentWord = uiStateValue.wordToReview.word
                wordRepository.update(currentWord.repeat())
                fetchUiState()
            }
        }
    }

    fun skipWord() {
        fetchUiState()
    }

    fun updateCardMode(cardMode: CardMode) {
        updateUiState {
            it.copy(cardMode = cardMode)
        }
    }

    fun updateUserGuess(value: TextFieldValue) {
        updateUiState {
            it.copy(userGuess = value)
        }
    }

    private fun indexOfFirstDifference(str1: String, str2: String): Int {
        val minLength = minOf(str1.length, str2.length)

        for (i in 0 until minLength) {
            if (str1[i] != str2[i]) {
                return i
            }
        }

        // If the loop completes without finding a difference in the common prefix,
        // return the length of the shorter string (or -1 if the strings are identical).
        return if (str1.length != str2.length) minLength else -1
    }

    fun revealOneLetter() {
        updateUiState { state ->
            val actualValue = state.wordToReview.word.value
            val userGuess = state.userGuess
            val indexOfFirstDifference = indexOfFirstDifference(actualValue, userGuess.text)

            if (indexOfFirstDifference == -1) {
                state.copy(cardMode = CardMode.ShowAnswer)
            } else {
                val updatedUserGuess =
                    if (indexOfFirstDifference > actualValue.lastIndex)
                        actualValue
                    else
                        actualValue.replaceRange(
                            range = (indexOfFirstDifference..actualValue.lastIndex),
                            replacement = actualValue[indexOfFirstDifference].toString()
                        )
                state.copy(
                    userGuess = TextFieldValue(
                        text = updatedUserGuess,
                        selection = TextRange(updatedUserGuess.length)
                    )
                )
            }
        }
    }

    fun checkAnswer() {
        updateUiState {
            val actual = it.wordToReview.word.value
            val userGuess = it.userGuess
            val amountAttemptsLeft = it.amountAttempts - 1
            if (actual == userGuess.text || amountAttemptsLeft == 0)
                it.copy(cardMode = CardMode.ShowAnswer)
            else
                it.copy(amountAttempts = amountAttemptsLeft)
        }
    }

    fun updateMenuExpanded(expanded: Boolean) {
        updateUiState {
            it.copy(menuExpanded = expanded)
        }
    }

    fun resetWord() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? ReviewWordsUiState.Success)?.let {
                val currentWord = it.wordToReview.word
                wordRepository.update(currentWord.reset())
            }
        }
        fetchUiState()
    }

    override fun copyWordToMyCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? ReviewWordsUiState.Success)?.let {
                val wordWithCategories = it.wordToReview.toWordWithCategories()
                val updatedCategories = wordWithCategories.categories + MY_WORDS_CATEGORY
                wordRepository.updateWordWithCategories(
                    wordWithCategories.copy(categories = updatedCategories)
                )
            }
        }
    }

    override fun removeWordFromMyCategory() {
        TODO("Not yet implemented")
    }

    override fun removeWordFromQueue() {
        TODO("Not yet implemented")
    }

    override fun addWordToQueue() {
        TODO("Not yet implemented")
    }

    override fun removeWord() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? ReviewWordsUiState.Success)?.let {
                wordRepository.remove(it.wordToReview)
            }
        }
        skipWord()
    }
}