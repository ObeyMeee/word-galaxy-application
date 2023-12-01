package ua.com.andromeda.wordgalaxy.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository

class HomeViewModel(
    private val userPreferencesDataStore: UserPreferencesRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = userPreferencesDataStore.amountWordsToLearnPerDay
        .map { HomeUiState.Success(amountWordsLearnPerDay = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Default
        )

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                HomeViewModel(application.userPreferencesRepository)
            }
        }
    }
}