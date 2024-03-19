package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.memorize
import ua.com.andromeda.wordgalaxy.data.model.updateStatus
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardUiState
import ua.com.andromeda.wordgalaxy.ui.common.flashcard.FlashcardViewModel
import javax.inject.Inject

@HiltViewModel
class LearnWordsViewModel @Inject constructor(
    wordRepository: WordRepository,
    @ApplicationContext context: Context,
) : FlashcardViewModel(wordRepository, context) {
    override suspend fun buildWordsQueue(): List<EmbeddedWord> {
        val amountWordsInProgress =
            wordRepository.countWordsWhereStatusEquals(WordStatus.InProgress).first()
        val amountWordsToLearnPerDay = getAmountWordsToLearnPerDay()
        val wordsQueue = if (amountWordsInProgress < amountWordsToLearnPerDay) {
            wordRepository.findRandomWordsWhereStatusEquals(
                status = WordStatus.New,
                limit = amountWordsToLearnPerDay - amountWordsInProgress
            )
        } else {
            wordRepository.findRandomWordsWhereStatusEquals(
                status = WordStatus.InProgress,
                limit = amountWordsInProgress
            )
        }
        return wordsQueue.first()
    }

    fun updateWordStatus(status: WordStatus) = viewModelScope.launch(coroutineDispatcher) {
        (_uiState.value as? FlashcardUiState.Success)?.let { state ->
            val word = state.memorizingWordsQueue.firstOrNull()?.word
                ?: throw IllegalStateException("Word is null")
            wordRepository.update(
                word.updateStatus(status)
            )
        }
        moveToNextWord()
    }

    fun memorizeWord() = viewModelScope.launch(coroutineDispatcher) {
        (_uiState.value as? FlashcardUiState.Success)?.let { state ->
            val currentWord = state.memorizingWordsQueue.firstOrNull()?.word
                ?: throw IllegalStateException("Word is null")
            wordRepository.update(currentWord.memorize())
            moveToNextWord()
        }
    }
}