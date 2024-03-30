package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.reset
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.copyWordToMyCategory
import ua.com.andromeda.wordgalaxy.data.repository.word.removeWordFromMyCategory
import ua.com.andromeda.wordgalaxy.ui.common.CardMode

abstract class FlashcardViewModel(
    protected val wordRepository: WordRepository,
    private val dataStoreHelper: PreferenceDataStoreHelper,
) : ViewModel() {
    protected var _uiState = MutableStateFlow<FlashcardUiState>(FlashcardUiState.Default)
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()
    protected val coroutineDispatcher = Dispatchers.IO

    init {
        fetchUiState()
    }

    private fun fetchUiState() = viewModelScope.launch(coroutineDispatcher) {
        observeCountWordsToReviewAndLearnedWordsToday()
        val amountWordsToLearnPerDay = getAmountWordsToLearnPerDay()
        val words = buildWordsQueue()
        updateUiState {
            it.copy(
                memorizingWordsQueue = words,
                amountWordsLearnPerDay = amountWordsToLearnPerDay
            )
        }
    }

    protected suspend fun getAmountWordsToLearnPerDay() = dataStoreHelper.first(
        KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY,
        DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY.toString()
    ).toInt()

    abstract suspend fun buildWordsQueue(): List<EmbeddedWord>

    private fun CoroutineScope.observeCountWordsToReviewAndLearnedWordsToday() =
        launch(coroutineDispatcher) {
            combine(
                wordRepository.countWordsToReview(),
                wordRepository.countLearnedWordsToday(),
            ) { amountWordsToReview, amountLearnedWordsToday ->
                val currentState = _uiState.value
                if (currentState is FlashcardUiState.Default) {
                    _uiState.update { _ ->
                        FlashcardUiState.Success(
                            amountWordsToReview = amountWordsToReview,
                            learnedWordsToday = amountLearnedWordsToday,
                        )
                    }
                } else {
                    updateUiState { state ->
                        state.copy(
                            amountWordsToReview = amountWordsToReview,
                            learnedWordsToday = amountLearnedWordsToday,
                        )
                    }
                }
            }.collect()
        }

    private inline fun updateUiState(
        errorMessage: String = "Something went wrong",
        action: (FlashcardUiState.Success) -> FlashcardUiState,
    ) {
        _uiState.update { uiState ->
            if (uiState is FlashcardUiState.Success) {
                action(uiState)
            } else {
                errorUiState(errorMessage)
            }
        }
    }

    private fun errorUiState(message: String = "Something went wrong") =
        FlashcardUiState.Error(message)

    fun updateCardMode(cardMode: CardMode) {
        updateUiState {
            it.copy(cardMode = cardMode)
        }
    }

    fun updateUserGuess(value: TextFieldValue) {
        updateUiState {
            it.copy(userGuess = value)
        }
    }

    private fun indexOfFirstDifference(str1: String, str2: String): Int {
        val minLength = minOf(str1.length, str2.length)

        for (i in 0 until minLength) {
            if (str1[i] != str2[i]) {
                return i
            }
        }

//         If the loop completes without finding a difference in the common prefix,
//         return the length of the shorter string (or -1 if the strings are identical).
        return if (str1.length != str2.length) minLength else -1
    }

    fun revealOneLetter() {
        updateUiState { state ->
            val currentWord =
                state.memorizingWordsQueue.firstOrNull() ?: return@updateUiState errorUiState()
            val actualValue = currentWord.word.value
            val userGuess = state.userGuess.text
            val indexOfFirstDifference = indexOfFirstDifference(actualValue, userGuess)

            if (indexOfFirstDifference == -1) {
                state.correctAnswer()
            } else {
                val revealedChar = actualValue[indexOfFirstDifference]
                val updatedUserGuess = userGuess.substring(0, indexOfFirstDifference) + revealedChar
                state.copy(
                    userGuess = TextFieldValue(
                        text = updatedUserGuess,
                        selection = TextRange(updatedUserGuess.length)
                    )
                )
            }
        }
    }

    fun checkAnswer() {
        updateUiState { state ->
            val currentEmbeddedWord =
                state.memorizingWordsQueue.firstOrNull() ?: return@updateUiState errorUiState()
            val actual = currentEmbeddedWord.word.value
            val userGuess = state.userGuess.text
            val amountAttemptsLeft = state.amountAttempts - 1
            if (actual == userGuess || amountAttemptsLeft == 0)
                state.correctAnswer()
            else
                state.copy(amountAttempts = amountAttemptsLeft)
        }
    }

    fun resetWord() {
        addWordToQueue()
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? FlashcardUiState.Success)?.let {
                val currentWord = it.memorizingWordsQueue.firstOrNull()?.word
                    ?: throw IllegalStateException("Word is null")
                wordRepository.update(currentWord.reset())
            }
        }
        updateUiState { state ->
            state.copy(
                cardMode = CardMode.Default,
            )
        }
        moveToNextWord()
    }


    fun copyWordToMyCategory() {
        addWordToQueue()
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? FlashcardUiState.Success)?.let { state ->
                val currentWord = state.memorizingWordsQueue.first()
                wordRepository.copyWordToMyCategory(currentWord)
            }
        }
    }

    fun removeWordFromQueue() {
        updateUiState {
            val updatedQueue = it.wordsInProcessQueue.toMutableList()
            updatedQueue.removeFirst()
            it.copy(wordsInProcessQueue = updatedQueue)
        }
    }

    fun removeWordFromMyCategory() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? FlashcardUiState.Success)?.let { state ->
                val processedWord = state.wordsInProcessQueue.firstOrNull()
                if (processedWord == null) {
                    _uiState.update { FlashcardUiState.Error() }
                } else {
                    wordRepository.removeWordFromMyCategory(processedWord)
                    removeWordFromQueue()
                }
            }
        }
    }

    fun addWordToQueue() {
        updateUiState { state ->
            val processedWord = state.memorizingWordsQueue.firstOrNull()
                ?: return@updateUiState errorUiState("Word is null")
            state.copy(
                wordsInProcessQueue = state.wordsInProcessQueue + processedWord
            )
        }
    }

    fun removeWord() {
        viewModelScope.launch(coroutineDispatcher) {
            updateUiState {
                val updatedWordsToRemove = it.wordsInProcessQueue.toMutableList()
                val removedWord = updatedWordsToRemove.removeFirst()

                launch {
                    wordRepository.remove(removedWord)
                }

                it.copy(
                    wordsInProcessQueue = updatedWordsToRemove,
                )
            }
            moveToNextWord()
        }
    }

    fun moveToNextWord() = viewModelScope.launch(coroutineDispatcher) {
        updateUiState { state ->
            var updatedLearningQueue = state.memorizingWordsQueue
            if (updatedLearningQueue.size == 1) {
                updatedLearningQueue = buildWordsQueue()
            } else {
                updatedLearningQueue = updatedLearningQueue.toMutableList()
                updatedLearningQueue.removeFirst()
            }
            state.copy(
                memorizingWordsQueue = updatedLearningQueue,
                cardMode = CardMode.Default,
            )
        }
    }

    fun updateMenuExpanded(expanded: Boolean) {
        updateUiState {
            it.copy(menuExpanded = expanded)
        }
    }

    fun recoverWord() = viewModelScope.launch(coroutineDispatcher) {
        (_uiState.value as? FlashcardUiState.Success)?.let { state ->
            val recoveredWord = state.wordsInProcessQueue.first()
            wordRepository.update(recoveredWord)
            removeWordFromQueue()
        }
    }
}