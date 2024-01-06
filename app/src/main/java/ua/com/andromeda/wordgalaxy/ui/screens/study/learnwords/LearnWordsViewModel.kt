package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

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
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.memorize
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import java.time.LocalDateTime

class LearnWordsViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LearnWordsUiState>(LearnWordsUiState.Default)
    val uiState: StateFlow<LearnWordsUiState> = _uiState

    init {
        fetchUiState()
    }

    private fun fetchUiState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val amountWordsToLearnPerDay =
                    userPreferencesRepository.amountWordsToLearnPerDay.first()
                val learnedWordsToday = wordRepository.countLearnedWordsToday().first()
                val amountWordsInProgress =
                    wordRepository.countWordsWhereStatusEquals(WordStatus.InProgress).first()
                val randomWord = getRandomWord(amountWordsInProgress, amountWordsToLearnPerDay)

                _uiState.update {
                    LearnWordsUiState.Success(
                        embeddedWord = randomWord,
                        learnedWordsToday = learnedWordsToday,
                        amountWordsLearnPerDay = amountWordsToLearnPerDay
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    LearnWordsUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    private suspend fun getRandomWord(
        amountWordsInProgress: Int,
        amountWordsToLearnPerDay: Int
    ): EmbeddedWord {
        val wordStatus =
            if (amountWordsInProgress < amountWordsToLearnPerDay)
                WordStatus.New
            else
                WordStatus.InProgress
        return wordRepository.findOneRandomWordWhereStatusEquals(wordStatus).first()
    }

    private fun updateUiState(
        errorMessage: String = "Something went wrong",
        action: (LearnWordsUiState.Success) -> LearnWordsUiState.Success
    ) {
        _uiState.update { uiState ->
            if (uiState is LearnWordsUiState.Success) {
                action(uiState)
            } else {
                errorUiState(errorMessage)
            }
        }
    }

    private fun errorUiState(message: String) =
        LearnWordsUiState.Error(message)

    private fun updateWordStatus(wordStatus: WordStatus) {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is LearnWordsUiState.Success) {
                val word = uiStateValue.embeddedWord.word
                wordRepository.update(
                    word.copy(
                        status = wordStatus,
                        statusChangedAt = LocalDateTime.now()
                    )
                )
            }
        }
    }

    private fun moveToNextCard() {
        fetchUiState()
    }

    fun startLearningWord() {
        updateWordStatus(WordStatus.InProgress)
        moveToNextCard()
    }

    fun alreadyKnowWord() {
        updateWordStatus(WordStatus.AlreadyKnown)
        moveToNextCard()
    }

    fun skipWord() {
        fetchUiState()
    }

    fun updateCardMode(cardMode: CardMode) {
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
            val actualValue = uiState.embeddedWord.word.value
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
            val actual = it.embeddedWord.word.value
            val userGuess = it.userGuess
            val amountAttemptsLeft = it.amountAttempts - 1
            if (actual == userGuess || amountAttemptsLeft == 0)
                it.copy(cardMode = CardMode.ShowAnswer)
            else
                it.copy(amountAttempts = amountAttemptsLeft)
        })
    }

    fun memorizeWord() {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is LearnWordsUiState.Success) {
                val word = uiStateValue.embeddedWord.word
                wordRepository.update(word.memorize())
                moveToNextCard()
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WordGalaxyApplication)
                val wordRepository = WordRepositoryImpl(
                    application.appDatabase.wordDao()
                )
                LearnWordsViewModel(wordRepository, application.userPreferencesRepository)
            }
        }
    }
}