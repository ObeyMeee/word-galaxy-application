package ua.com.andromeda.wordgalaxy.study.reviewwords.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.repeat
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardUiState
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewWordsViewModel @Inject constructor(
    wordRepository: WordRepository,
    dataStoreHelper: PreferenceDataStoreHelper,
) : FlashcardViewModel(wordRepository, dataStoreHelper) {
    val amountReviewedWordsToday = wordRepository.countReviewedWordsToday().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    override suspend fun buildWordsQueue(): List<EmbeddedWord> {
        return wordRepository.findWordsToReview(LIMIT_AMOUNT_WORDS_IN_REVIEW_QUEUE).first()
    }

    fun repeatWord() {
        viewModelScope.launch {
            (_uiState.value as? FlashcardUiState.Success)?.let { state ->
                val currentWord = state.memorizingWordsQueue.firstOrNull()?.word
                    ?: throw IllegalStateException("Word is null")
                wordRepository.update(currentWord.repeat())
                moveToNextWord()
            }
        }
    }

    companion object {
        const val LIMIT_AMOUNT_WORDS_IN_REVIEW_QUEUE = 50
    }
}