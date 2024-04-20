package ua.com.andromeda.wordgalaxy.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.DEFAULT_TIME_PERIOD_DAYS
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.KEY_TIME_PERIOD_DAYS
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.home.presentation.components.TimePeriodChartOptions
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val dataStoreHelper: PreferenceDataStoreHelper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Default)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val coroutineDispatcher = Dispatchers.IO

    init {
        viewModelScope.launch(coroutineDispatcher) {
            observeAmountWordsToReview()
            launch {
                fetchLatestData()
            }
            launch {
                fetchChartData()
            }
        }
    }

    private suspend fun fetchLatestData() {
        val amountWordsToLearnPerDayFlow = withContext(Dispatchers.Main) {
            dataStoreHelper.first(
                KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY,
                DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY.toString()
            ).toInt()
        }

        combine(
            wordRepository.countLearnedWordsToday(),
            wordRepository.countCurrentStreak(),
            wordRepository.countBestStreak(),
        ) { learnedWordsToday, currentStreak, bestStreak ->
            val currentState = _uiState.value
            if (currentState is HomeUiState.Default) {
                _uiState.update { _ ->
                    HomeUiState.Success(
                        amountWordsToLearnPerDay = amountWordsToLearnPerDayFlow,
                        learnedWordsToday = learnedWordsToday,
                        currentStreak = currentStreak,
                        bestStreak = bestStreak,
                    )
                }
            } else {
                updateUiState { state ->
                    state.copy(
                        amountWordsToLearnPerDay = amountWordsToLearnPerDayFlow,
                        learnedWordsToday = learnedWordsToday,
                        currentStreak = currentStreak,
                        bestStreak = bestStreak,
                    )
                }
            }
        }.first()
    }

    private suspend fun fetchChartData() = withContext(Dispatchers.Default) {
        dataStoreHelper.get(
            KEY_TIME_PERIOD_DAYS,
            DEFAULT_TIME_PERIOD_DAYS
        ).collect { timePeriodDays ->
            val timePeriod = TimePeriodChartOptions
                .entries
                .find { timePeriodDays == it.days } ?: TimePeriodChartOptions.WEEK
            val listOfWordsCountByStatus = wordRepository.countWordsByStatusLast(
                timePeriodDays,
                ChronoUnit.DAYS
            )
            updateUiState { state ->
                state.copy(
                    timePeriod = timePeriod,
                    listOfWordsCountOfStatus = listOfWordsCountByStatus,
                )
            }
        }
    }

    private fun CoroutineScope.observeAmountWordsToReview() = launch(coroutineDispatcher) {
        wordRepository.countWordsToReview().collect { amountWordsToReview ->
            val currentState = _uiState.value
            if (currentState is HomeUiState.Default) {
                _uiState.update { _ ->
                    HomeUiState.Success(amountWordsToReview = amountWordsToReview)
                }
            } else {
                updateUiState { state ->
                    state.copy(amountWordsToReview = amountWordsToReview)
                }
            }
        }

    }

    fun updateTimePeriod(timePeriodChartOptions: TimePeriodChartOptions) =
        viewModelScope.launch(coroutineDispatcher) {
            dataStoreHelper.put(KEY_TIME_PERIOD_DAYS, timePeriodChartOptions.days)
            updateShowTimePeriodDialog(false)
            val listOfWordsCountByStatus = wordRepository.countWordsByStatusLast(
                timePeriodChartOptions.days,
                ChronoUnit.DAYS
            )
            updateUiState {
                it.copy(
                    listOfWordsCountOfStatus = listOfWordsCountByStatus
                )
            }
        }

    private fun updateUiState(
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

    private fun errorUiState(message: String = "Unexpected state") =
        HomeUiState.Error(message)

    fun updateShowTimePeriodDialog(value: Boolean) {
        updateUiState {
            it.copy(showTimePeriodDialog = value)
        }
    }
}