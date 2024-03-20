package ua.com.andromeda.wordgalaxy.ui.screens.start.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_TIME_PERIOD_DAYS
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.KEY_TIME_PERIOD_DAYS
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.utils.TAG
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val dataStoreHelper: PreferenceDataStoreHelper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Default)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combinedLatestData()
        }
    }

    private suspend fun combinedLatestData() {
        var amountWordsToLearnPerDayFlow: Flow<Int>
        withContext(Dispatchers.Main) {
            amountWordsToLearnPerDayFlow = dataStoreHelper.get(
                KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY,
                DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY.toString()
            ).map { it.toInt() }
        }

        val combinedFlow: Flow<HomeUiState> = combine(
            amountWordsToLearnPerDayFlow,
            wordRepository.countLearnedWordsToday(),
            wordRepository.countWordsToReview(),
            dataStoreHelper.get(KEY_TIME_PERIOD_DAYS, DEFAULT_TIME_PERIOD_DAYS),
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

        combinedFlow.catch { error ->
            Log.e(TAG, error.toString())
            emit(HomeUiState.Error("Error occurred: ${error.message}"))
        }.collect { newUiState ->
            _uiState.update { newUiState }
        }
    }

    fun updateTimePeriod(timePeriodChartOptions: TimePeriodChartOptions) =
        viewModelScope.launch(Dispatchers.IO) {
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