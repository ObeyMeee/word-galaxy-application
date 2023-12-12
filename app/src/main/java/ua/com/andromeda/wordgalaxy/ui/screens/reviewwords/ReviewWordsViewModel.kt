package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.model.repeat
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode

class ReviewWordsViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<ReviewWordsUiState> =
        MutableStateFlow(ReviewWordsUiState.Default)
    val uiState: StateFlow<ReviewWordsUiState> = _uiState

    init {
        fetchUiState()
    }

    private fun fetchUiState() {
        viewModelScope.launch {
            val wordToReview = wordRepository.findWordToReview().first()
            val reviewedToday = wordRepository.countReviewedWordsToday().first()
            _uiState.update {
                if (wordToReview == null)
                    errorUiState("No words to repeat. You do a great work!")
                else
                    ReviewWordsUiState.Success(wordToReview, reviewedToday, CardMode.Default)
            }
        }
    }

    private fun errorUiState(message: String) =
        ReviewWordsUiState.Error(message)


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

    fun repeatWord() {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is ReviewWordsUiState.Success) {
                val word = uiStateValue.wordToReview.word
                wordRepository.update(word.repeat())
                fetchUiState()
            }
        }
    }

    fun skipWord() {
        fetchUiState()
    }

    fun updateReviewMode(cardMode: CardMode) {
        updateUiState(action = {
            it.copy(cardMode = cardMode)
        })
    }

    fun updateUserGuess(value: String) {
        val trimmedAndLowerCaseValue = value.trim().lowercase()
        updateUiState(action = {
            it.copy(userGuess = trimmedAndLowerCaseValue)
        })
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
        updateUiState { uiState ->
            val actualValue = uiState.wordToReview.word.value
            val userGuess = uiState.userGuess
            val indexOfFirstDifference = indexOfFirstDifference(actualValue, userGuess)

            if (indexOfFirstDifference == -1) {
                uiState.copy(cardMode = CardMode.ShowAnswer)
            } else {
                val updatedUserGuess =
                    if (indexOfFirstDifference > actualValue.lastIndex)
                        actualValue
                    else
                        actualValue.replaceRange(
                            range = (indexOfFirstDifference..actualValue.lastIndex),
                            replacement = actualValue[indexOfFirstDifference].toString()
                        )
                uiState.copy(userGuess = updatedUserGuess)
            }
        }
    }

    fun checkAnswer() {
        updateUiState(action = {
            val actual = it.wordToReview.word.value
            val userGuess = it.userGuess
            val amountAttemptsLeft = it.amountAttempts - 1
            if (actual == userGuess || amountAttemptsLeft == 0)
                it.copy(cardMode = CardMode.ShowAnswer)
            else
                it.copy(amountAttempts = amountAttemptsLeft)
        })
    }

    companion object {
        private const val TAG = "ReviewWordsViewModel"

        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordDao = application.appDatabase.wordDao()
                val wordRepository = WordRepositoryImpl(wordDao)
                ReviewWordsViewModel(wordRepository)
            }
        }
    }
}