package ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.repeat
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardUiState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewWordsViewModel @Inject constructor(
    wordRepository: WordRepository,
    @ApplicationContext context: Context,
) : FlashcardViewModel(wordRepository, context) {
    val amountReviewedWordsToday = wordRepository.countReviewedWordsToday().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    override suspend fun buildWordsQueue(): List<EmbeddedWord> {
        return wordRepository.findRandomWordsWhereStatusEquals(
            status = WordStatus.Memorized,
            limit = LIMIT_AMOUNT_WORDS_IN_REVIEW_QUEUE
        ).first()
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