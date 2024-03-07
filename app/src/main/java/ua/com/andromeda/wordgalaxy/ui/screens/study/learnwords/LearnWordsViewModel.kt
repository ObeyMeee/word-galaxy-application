package ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords

import android.content.Context
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.chillibits.simplesettings.tool.getPreferenceLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.memorize
import ua.com.andromeda.wordgalaxy.data.model.reset
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.data.model.updateStatus
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.common.CardMode
import javax.inject.Inject

@HiltViewModel
class LearnWordsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LearnWordsUiState>(LearnWordsUiState.Default)
    val uiState: StateFlow<LearnWordsUiState> = _uiState
    private val coroutineDispatcher = Dispatchers.IO

    init {
        fetchUiState()
    }


    private fun fetchUiState() {
        viewModelScope.launch(coroutineDispatcher) {
            var amountWordsToLearnPerDayFlow: Flow<Int>
            withContext(Dispatchers.Main) {
                amountWordsToLearnPerDayFlow = getPreferenceLiveData(
                    context,
                    KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY,
                    DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
                ).asFlow()
            }

            combine(
                amountWordsToLearnPerDayFlow,
                wordRepository.countLearnedWordsToday(),
                wordRepository.countWordsToReview(),
                wordRepository.countWordsWhereStatusEquals(WordStatus.InProgress),
            ) { amountWordsToLearnPerDay, amountLearnedWordsToday, amountWordsToReview, amountWordsInProgress ->
                val words = buildWordsQueue(amountWordsInProgress, amountWordsToLearnPerDay).first()
                _uiState.update { state ->
                    if (state is LearnWordsUiState.Success) {
                        state.copy(
                            learningWordsQueue = words,
                            cardMode = CardMode.Default,
                            learnedWordsToday = amountLearnedWordsToday,
                            amountWordsToReview = amountWordsToReview,
                            amountWordsLearnPerDay = amountWordsToLearnPerDay,
                        )
                    } else {
                        LearnWordsUiState.Success(
                            learningWordsQueue = words,
                            learnedWordsToday = amountLearnedWordsToday,
                            amountWordsToReview = amountWordsToReview,
                            amountWordsLearnPerDay = amountWordsToLearnPerDay
                        )
                    }
                }
            }.catch { error ->
                _uiState.update {
                    errorUiState(error.message ?: "Something went wrong")
                }
            }.collect()
        }
    }

    private fun buildWordsQueue(
        amountWordsInProgress: Int,
        amountWordsToLearnPerDay: Int
    ): Flow<List<EmbeddedWord>> {
        val wordStatus =
            if (amountWordsInProgress < amountWordsToLearnPerDay)
                WordStatus.New
            else
                WordStatus.InProgress
        val limit = if (amountWordsInProgress < amountWordsToLearnPerDay)
            amountWordsToLearnPerDay - amountWordsInProgress
        else
            amountWordsInProgress
        return wordRepository.findRandomWordsWhereStatusEquals(wordStatus, limit)
    }

    private fun updateUiState(
        errorMessage: String = "Something went wrong",
        action: (LearnWordsUiState.Success) -> LearnWordsUiState,
    ) {
        _uiState.update { uiState ->
            if (uiState is LearnWordsUiState.Success) {
                action(uiState)
            } else {
                errorUiState(errorMessage)
            }
        }
    }

    private fun errorUiState(message: String = "Something went wrong") =
        LearnWordsUiState.Error(message)

    private fun updateWordStatus(status: WordStatus) {
        (_uiState.value as? LearnWordsUiState.Success)?.let { state ->
            viewModelScope.launch(coroutineDispatcher) {
                val word = state.learningWordsQueue.firstOrNull()?.word
                    ?: throw IllegalStateException("Word is null")
                wordRepository.update(
                    word.updateStatus(status)
                )
            }
        }
    }

    fun moveToNextWord() {
        viewModelScope.launch(coroutineDispatcher) {
            updateUiState { state ->
                val updatedQueue = state.learningWordsQueue.toMutableList()
                val cardMode = CardMode.Default
                if (updatedQueue.size == 1) {
                    val newLearningQueue = runBlocking {
                        val amountWordsInProgress =
                            wordRepository.countWordsWhereStatusEquals(WordStatus.InProgress)
                                .first()
                        buildWordsQueue(
                            amountWordsInProgress,
                            state.amountWordsLearnPerDay
                        ).first()
                    }
                    state.copy(
                        learningWordsQueue = newLearningQueue,
                        cardMode = cardMode,
                    )
                } else {
                    updatedQueue.removeFirst()
                    state.copy(
                        learningWordsQueue = updatedQueue,
                        cardMode = cardMode,
                    )
                }
            }
        }
    }

    fun startLearningWord() {
        updateWordStatus(WordStatus.InProgress)
        moveToNextWord()
    }

    fun alreadyKnowWord() {
        updateWordStatus(WordStatus.AlreadyKnown)
        moveToNextWord()
    }

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

        // If the loop completes without finding a difference in the common prefix,
        // return the length of the shorter string (or -1 if the strings are identical).
        return if (str1.length != str2.length) minLength else -1
    }

    fun revealOneLetter() {
        updateUiState { state ->
            val currentWord =
                state.learningWordsQueue.firstOrNull() ?: return@updateUiState errorUiState()
            val actualValue = currentWord.word.value
            val userGuess = state.userGuess
            val indexOfFirstDifference = indexOfFirstDifference(actualValue, userGuess.text)

            if (indexOfFirstDifference == -1) {
                state.copy(cardMode = CardMode.ShowAnswer)
            } else {
                val updatedUserGuess =
                    if (indexOfFirstDifference > actualValue.lastIndex)
                        actualValue
                    else
                        actualValue.replaceRange(
                            range = (indexOfFirstDifference..actualValue.lastIndex),
                            replacement = actualValue[indexOfFirstDifference].toString()
                        )
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
        updateUiState {
            val currentEmbeddedWord =
                it.learningWordsQueue.firstOrNull() ?: return@updateUiState errorUiState()
            val actual = currentEmbeddedWord.word.value
            val userGuess = it.userGuess
            val amountAttemptsLeft = it.amountAttempts - 1
            if (actual == userGuess.text || amountAttemptsLeft == 0)
                it.copy(cardMode = CardMode.ShowAnswer)
            else
                it.copy(amountAttempts = amountAttemptsLeft)
        }
    }

    fun memorizeWord() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? LearnWordsUiState.Success)?.let { state ->
                val currentWord = state.learningWordsQueue.firstOrNull()?.word
                    ?: throw IllegalStateException("Word is null")
                wordRepository.update(currentWord.memorize())
                moveToNextWord()
            }
        }
    }

    fun resetWord() {
        addWordToQueue()
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? LearnWordsUiState.Success)?.let {
                val currentWord = it.learningWordsQueue.firstOrNull()?.word
                    ?: throw IllegalStateException("Word is null")
                wordRepository.update(currentWord.reset())
            }
        }
        updateUiState { state ->
            state.copy(
                cardMode = CardMode.Default,
            )
        }
    }

    fun copyWordToMyCategory() {
        addWordToQueue()
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? LearnWordsUiState.Success)?.let { state ->
                val wordWithCategories =
                    state.learningWordsQueue.firstOrNull()?.toWordWithCategories()
                        ?: throw IllegalStateException("Word is null")
                val updatedCategories = wordWithCategories.categories + MY_WORDS_CATEGORY
                wordRepository.updateWordWithCategories(
                    wordWithCategories.copy(
                        categories = updatedCategories
                    )
                )
            }
        }
    }

    fun addWordToQueue() {
        updateUiState { state ->
            val processedWord = state.learningWordsQueue.firstOrNull()
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
        }
    }

    fun updateMenuExpanded(expanded: Boolean) {
        updateUiState {
            it.copy(menuExpanded = expanded)
        }
    }

    fun removeWordFromQueue() {
        updateUiState {
            val updatedQueue = it.wordsInProcessQueue.toMutableList()
            it.copy(wordsInProcessQueue = updatedQueue)
        }
    }

    fun removeWordFromMyCategory() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? LearnWordsUiState.Success)?.let {
                val wordWithCategories = it.wordsInProcessQueue.first().toWordWithCategories()
                val updatedCategories = wordWithCategories.categories - MY_WORDS_CATEGORY
                wordRepository.updateWordWithCategories(
                    wordWithCategories.copy(
                        categories = updatedCategories,
                    )
                )
                removeWordFromQueue()
            }
        }
    }

    fun recoverWord() {
        viewModelScope.launch(coroutineDispatcher) {
            (_uiState.value as? LearnWordsUiState.Success)?.let { state ->
                val recoveredWord = state.wordsInProcessQueue.first()
                wordRepository.update(recoveredWord)
                removeWordFromQueue()
            }
        }
    }
}