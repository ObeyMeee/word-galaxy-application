package ua.com.andromeda.wordgalaxy.study.learnwords.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.memorize
import ua.com.andromeda.wordgalaxy.core.domain.model.updateStatus
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardUiState
import ua.com.andromeda.wordgalaxy.study.flashcard.presentation.FlashcardViewModel
import javax.inject.Inject

@HiltViewModel
class LearnWordsViewModel @Inject constructor(
    wordRepository: WordRepository,
    dataStoreHelper: PreferenceDataStoreHelper,
) : FlashcardViewModel(wordRepository, dataStoreHelper) {
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