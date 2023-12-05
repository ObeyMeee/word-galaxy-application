package ua.com.andromeda.wordgalaxy.ui.screens.browsecards

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
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.calculateNextRepeatAt
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import java.time.LocalDateTime

class BrowseCardsViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BrowseCardUiState>(BrowseCardUiState.Default)
    val uiState: StateFlow<BrowseCardUiState> = _uiState

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
                    BrowseCardUiState.Success(
                        embeddedWord = randomWord,
                        learnedWordsToday = learnedWordsToday,
                        amountWordsLearnPerDay = amountWordsToLearnPerDay
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    BrowseCardUiState.Error
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
            if (uiStateValue is BrowseCardUiState.Success) {

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
            if (uiStateValue is BrowseCardUiState.Success) {

                val word = uiStateValue.embeddedWord.word
                val newAmountRepetition = 0
                wordRepository.update(
                    word.copy(
                        memorizedAt = LocalDateTime.now(),
                        amountRepetition = newAmountRepetition,
                        status = WordStatus.Memorized,
                        nextRepeatAt = calculateNextRepeatAt(newAmountRepetition)
                    )
                )
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
                BrowseCardsViewModel(wordRepository, application.userPreferencesRepository)
            }
        }
    }
}