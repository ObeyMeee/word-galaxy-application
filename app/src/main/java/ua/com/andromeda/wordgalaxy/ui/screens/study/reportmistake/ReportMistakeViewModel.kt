package ua.com.andromeda.wordgalaxy.ui.screens.study.reportmistake

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import javax.inject.Inject

private const val TAG = "ReportMistakeViewModel"

@HiltViewModel
class ReportMistakeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {
    private var _uiState = MutableStateFlow<ReportMistakeUiState>(ReportMistakeUiState.Default)
    val uiState: StateFlow<ReportMistakeUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                val wordId: Long? = stateHandle["wordId"]
                if (wordId == null) {
                    _uiState.update {
                        ReportMistakeUiState.Error(message = "Cannot obtain wordId")
                    }
                } else {
                    wordRepository.findWordById(wordId).collect { word ->
                        _uiState.update {
                            ReportMistakeUiState.Success(word)
                        }
                    }
                }
            }
        }
    }

    private fun updateUiState(action: (ReportMistakeUiState.Success) -> ReportMistakeUiState.Success) {
        viewModelScope.launch {
            _uiState.update {
                if (it is ReportMistakeUiState.Success) {
                    action(it)
                } else {
                    ReportMistakeUiState.Error()
                }
            }
        }
    }

    fun updateWordInput(value: String) {
        updateUiState {
            val report = it.report
            val mistakenWord = it.mistakenWord
            it.copy(
                report = report.copy(word = value),
                isFormValid = mistakenWord.value != value || mistakenWord.translation != report.translation
            )
        }
    }

    fun updateTranslationInput(value: String) {
        updateUiState {
            val report = it.report
            val mistakenWord = it.mistakenWord
            it.copy(
                report = report.copy(translation = value),
                isFormValid = mistakenWord.value != report.word || mistakenWord.translation != value
            )
        }
    }

    fun updateComment(value: String) {
        updateUiState {
            it.copy(
                report = it.report.copy(comment = value),
            )
        }
    }

    fun send() {
        (_uiState.value as? ReportMistakeUiState.Success)?.let {
            Log.d(TAG, it.report.toString())
        }
    }
}