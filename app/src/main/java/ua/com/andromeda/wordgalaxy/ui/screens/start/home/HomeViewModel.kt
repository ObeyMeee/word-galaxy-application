package ua.com.andromeda.wordgalaxy.ui.screens.start.home

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
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import java.time.temporal.ChronoUnit

class HomeViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Default)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val amountWordsToLearnPerDay =
                userPreferencesRepository.amountWordsToLearnPerDay.first()
            val learnedWordsToday = wordRepository.countLearnedWordsToday().first()
            val amountWordsToReview = wordRepository.countWordsToReview().first()
            val listOfWordsCountByStatus = wordRepository.countWordsByStatusLast(7, ChronoUnit.DAYS)

            _uiState.update {
                HomeUiState.Success(
                    learnedWordsToday = learnedWordsToday,
                    amountWordsToLearnPerDay = amountWordsToLearnPerDay,
                    amountWordsToReview = amountWordsToReview,
                    listOfWordsCountOfStatus = listOfWordsCountByStatus
                )
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordRepository = WordRepositoryImpl(
                    application.appDatabase.wordDao()
                )
                val userPreferencesRepository = application.userPreferencesRepository
                HomeViewModel(wordRepository, userPreferencesRepository)
            }
        }
    }
}