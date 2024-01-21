package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.memorize
import ua.com.andromeda.wordgalaxy.data.model.reset
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
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
                var amountWordsToReview = 0
                launch {
                    wordRepository.countWordsToReview().collect { value ->
                        amountWordsToReview = value
                    }
                }
                val randomWord = getRandomWord(amountWordsInProgress, amountWordsToLearnPerDay)

                _uiState.update {
                    LearnWordsUiState.Success(
                        embeddedWord = randomWord,
                        learnedWordsToday = learnedWordsToday,
                        amountWordsToReview = amountWordsToReview,
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
        viewModelScope.launch(Dispatchers.IO) {
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

    fun moveToNextWord() {
        fetchUiState()
    }

    fun startLearningWord() {
        updateWordStatus(WordStatus.InProgress)
        moveToNextWord()
    }

    fun alreadyKnowWord() {
        updateWordStatus(WordStatus.AlreadyKnown)
        moveToNextWord()
    }

    fun resetWord() {
        viewModelScope.launch(Dispatchers.IO) {
            val wordsUiState = _uiState.value
            if (wordsUiState is LearnWordsUiState.Success) {
                val currentWord = wordsUiState.embeddedWord.word
                wordRepository.update(currentWord.reset())
            }
        }
        fetchUiState()
    }

    fun updateCardMode(cardMode: CardMode) {
        updateUiState(action = {
            it.copy(cardMode = cardMode)
        })
    }

    fun updateUserGuess(value: TextFieldValue) {
        updateUiState(action = {
            it.copy(userGuess = value)
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
            val indexOfFirstDifference = indexOfFirstDifference(actualValue, userGuess.text)

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
                uiState.copy(
                    userGuess = TextFieldValue(
                        text = updatedUserGuess,
                        selection = TextRange(updatedUserGuess.length)
                    )
                )
            }
        }
    }

    fun checkAnswer() {
        updateUiState(action = {
            val actual = it.embeddedWord.word.value
            val userGuess = it.userGuess
            val amountAttemptsLeft = it.amountAttempts - 1
            if (actual == userGuess.text || amountAttemptsLeft == 0)
                it.copy(cardMode = CardMode.ShowAnswer)
            else
                it.copy(amountAttempts = amountAttemptsLeft)
        })
    }

    fun memorizeWord() {
        viewModelScope.launch(Dispatchers.IO) {
            val uiStateValue = _uiState.value
            if (uiStateValue is LearnWordsUiState.Success) {
                val currentWord = uiStateValue.embeddedWord.word
                wordRepository.update(currentWord.memorize())
                moveToNextWord()
            }
        }
    }

    fun copyWordToMyCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? LearnWordsUiState.Success)?.let {
                val wordWithCategories = it.embeddedWord.toWordWithCategories()
                val updatedCategories = wordWithCategories.categories + MY_WORDS_CATEGORY
                wordRepository.updateWordWithCategories(
                    wordWithCategories.copy(categories = updatedCategories)
                )
            }
        }
    }

    fun reportMistake() {
        TODO("Not yet implemented")
    }

    fun edit() {
        TODO("Not yet implemented")
    }

    fun removeWord() {
        viewModelScope.launch(Dispatchers.IO) {
            (_uiState.value as? LearnWordsUiState.Success)?.let {
                wordRepository.remove(it.embeddedWord)
            }
        }
        moveToNextWord()
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