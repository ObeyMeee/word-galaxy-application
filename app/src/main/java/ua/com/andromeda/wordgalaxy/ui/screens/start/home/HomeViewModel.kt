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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepositoryImpl
import java.time.temporal.ChronoUnit

class HomeViewModel(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Default)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val combinedFlow = combine(
                userPreferencesRepository.amountWordsToLearnPerDay,
                wordRepository.countLearnedWordsToday(),
                wordRepository.countWordsToReview(),
                userPreferencesRepository.timePeriodChartOptions,
                wordRepository.countCurrentStreak(),
                wordRepository.countBestStreak()
            ) { results ->
                val (amountWordsToLearnPerDay, learnedWordsToday, amountWordsToReview, timePeriodDays, currentStreak) = results
                val timePeriod = TimePeriodChartOptions
                    .entries
                    .find { timePeriodDays == it.days } ?: TimePeriodChartOptions.WEEK
                val listOfWordsCountByStatus = wordRepository.countWordsByStatusLast(
                    timePeriodDays,
                    ChronoUnit.DAYS
                )
                HomeUiState.Success(
                    learnedWordsToday = learnedWordsToday,
                    amountWordsToLearnPerDay = amountWordsToLearnPerDay,
                    amountWordsToReview = amountWordsToReview,
                    timePeriod = timePeriod,
                    listOfWordsCountOfStatus = listOfWordsCountByStatus,
                    currentStreak = currentStreak,
                    bestStreak = results.last()
                )
            }
            combinedFlow.collect { newUiState ->
                _uiState.update { newUiState }
            }
        }
    }

    fun updateTimePeriod(timePeriodChartOptions: TimePeriodChartOptions) =
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveTimePeriod(timePeriodChartOptions)
            updateShowTimePeriodDialog(false)
            val listOfWordsCountByStatus = wordRepository.countWordsByStatusLast(
                timePeriodChartOptions.days,
                ChronoUnit.DAYS
            )
            updateState {
                it.copy(
                    listOfWordsCountOfStatus = listOfWordsCountByStatus
                )
            }
        }

    private fun updateState(
        errorMessage: String = "Something went wrong",
        action: (HomeUiState.Success) -> HomeUiState.Success
    ) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                action(state)
            } else {
                errorUiState(errorMessage)
            }
        }
    }

    fun updateShowTimePeriodDialog(value: Boolean) {
        updateState {
            it.copy(showTimePeriodDialog = value)
        }
    }

    private fun errorUiState(message: String = "Unexpected error occurred") =
        HomeUiState.Error(message)


    companion object {
        private const val TAG = "HomeViewModel"
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