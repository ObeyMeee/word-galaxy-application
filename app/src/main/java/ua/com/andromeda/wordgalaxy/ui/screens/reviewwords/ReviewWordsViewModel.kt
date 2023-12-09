package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.model.repeat
import ua.com.andromeda.wordgalaxy.data.repository.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.WordRepositoryImpl
import ua.com.andromeda.wordgalaxy.ui.screens.common.ReviewMode

class ReviewWordsViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<ReviewWordsUiState> =
        MutableStateFlow(ReviewWordsUiState.Default)
    val uiState: StateFlow<ReviewWordsUiState> = _uiState

    init {
        fetchUiState()
    }

    private fun fetchUiState() {
        viewModelScope.launch {
            val wordToReview = wordRepository.findWordToReview().first()
            val reviewedToday = wordRepository.countReviewedWordsToday().first()
            _uiState.update {
                if (wordToReview == null)
                    ReviewWordsUiState.Error(message = "No words to repeat. You do a great work!")
                else
                    ReviewWordsUiState.Success(wordToReview, reviewedToday, ReviewMode.Default)
            }
        }
    }

    fun repeatWord() {
        viewModelScope.launch {
            val uiStateValue = _uiState.value
            if (uiStateValue is ReviewWordsUiState.Success) {
                val word = uiStateValue.wordToReview.word
                wordRepository.update(word.repeat())
            }
        }
    }

    fun skipWord() {
        fetchUiState()
    }

    fun updateReviewMode(reviewMode: ReviewMode) {
        _uiState.update {
            if (it is ReviewWordsUiState.Success) {
                it.copy(reviewMode = reviewMode)
            } else
                throw IllegalStateException()
        }
    }

    companion object {
        private const val TAG = "ReviewWordsViewModel"

        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val wordDao = application.appDatabase.wordDao()
                val wordRepository = WordRepositoryImpl(wordDao)
                ReviewWordsViewModel(wordRepository)
            }
        }
    }
}