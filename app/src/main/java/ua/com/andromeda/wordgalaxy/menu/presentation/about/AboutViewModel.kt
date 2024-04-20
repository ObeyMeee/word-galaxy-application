package ua.com.andromeda.wordgalaxy.menu.presentation.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AboutUiState(
    val aboutAppExpanded: Boolean = false,
    val aboutSpacedRepetitionExpanded: Boolean = false,
)

@HiltViewModel

class AboutViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    fun expandAboutApp(value: Boolean = false) {
        _uiState.update { state ->
            state.copy(aboutAppExpanded = value)
        }
    }

    fun expandAboutSpacedRepetition(value: Boolean = false) {
        _uiState.update { state ->
            state.copy(aboutSpacedRepetitionExpanded = value)
        }
    }
}