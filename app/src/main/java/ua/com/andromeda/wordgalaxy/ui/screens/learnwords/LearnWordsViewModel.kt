package ua.com.andromeda.wordgalaxy.ui.screens.learnwords

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

class LearnWordsViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LearnWordsUiState>(LearnWordsUiState.Default)
    val uiState: StateFlow<LearnWordsUiState> = _uiState

    init {
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
                    LearnWordsUiState.Error
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

    fun updateWordStatus(wordStatus: WordStatus) {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is LearnWordsUiState.Success) {
                val word = uiStateValue.embeddedWord.word
                wordRepository.update(
                    word.copy(
                        status = wordStatus
                    )
                )
            }
        }
    }

    fun memorizeWord() {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is LearnWordsUiState.Success) {
                val word = uiStateValue.embeddedWord.word
                wordRepository.update(word.memorize())
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