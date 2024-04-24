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
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import java.time.LocalDate
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
            fetchChartData()
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

    private fun fetchChartData() {
        val currentState = _uiState.value

        val chartData = wordRepository.countWordsByStatusInRange(
            START_PERIOD_DEFAULT to END_PERIOD_DEFAULT
        )
        if (currentState is HomeUiState.Success) {
            updateUiState { state ->
                state.copy(
                    chartData = chartData,
                )
            }
        } else {
            _uiState.update { _ ->
                HomeUiState.Success(
                    chartData = chartData,
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
            it.copy(isPeriodDialogOpen = value)
        }
    }

    fun updatePeriod(start: LocalDate, end: LocalDate) {
        val chartData = wordRepository.countWordsByStatusInRange(start to end)
        updateUiState {
            it.copy(
                startPeriod = start,
                endPeriod = end,
                chartData = chartData,
                isPeriodDialogOpen = false,
            )
        }
    }
}