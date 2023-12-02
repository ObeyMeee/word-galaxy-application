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
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository

class BrowseCardsViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BrowseCardUiState>(BrowseCardUiState.Default)
    val uiState: StateFlow<BrowseCardUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val randomWord = wordRepository.findOneRandomNewWord().first()
                val amountWordsToLearnPerDay =
                    userPreferencesRepository.amountWordsToLearnPerDay.first()
                val learnedWordsToday = wordRepository.countLearnedWordsToday().first()
                _uiState.update {
                    BrowseCardUiState.Success(
                        wordWithCategories = randomWord,
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

    fun updateWordStatus(wordStatus: WordStatus) {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is BrowseCardUiState.Success) {

                val word = uiStateValue.wordWithCategories.word
                wordRepository.update(
                    word.copy(
                        status = wordStatus
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