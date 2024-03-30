package ua.com.andromeda.wordgalaxy.data.repository.word

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.MY_WORDS_CATEGORY
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.ui.common.wordform.ExistingWord
import java.time.temporal.TemporalUnit

interface WordRepository {
    fun findRandomWordsWhereStatusEquals(status: WordStatus, limit: Int): Flow<List<EmbeddedWord>>
    fun findWordsToReview(limit: Int): Flow<List<EmbeddedWord>>
    fun findWordById(id: Long): Flow<Word>
    fun findEmbeddedWordById(id: Long): Flow<EmbeddedWord>
    fun findWordsByValue(value: String): Flow<List<ExistingWord>>
    fun findWordsByValueOrTranslation(searchQuery: String): Flow<List<EmbeddedWord>>
    fun findWordsByCategoryId(categoryId: Long): Flow<List<EmbeddedWord>>
    fun countLearnedWordsToday(): Flow<Int>
    fun countWordsWhereStatusEquals(status: WordStatus): Flow<Int>
    fun countWordsToReview(): Flow<Int>
    fun countReviewedWordsToday(): Flow<Int>
    fun countWordsByStatusLast(value: Int, unit: TemporalUnit): List<Map<WordStatus, Int>>
    fun countCurrentStreak(): Flow<Int>
    fun countBestStreak(): Flow<Int>
    suspend fun update(vararg words: Word)
    suspend fun update(embeddedWord: EmbeddedWord)
    suspend fun updateWordWithCategories(wordWithCategories: WordWithCategories)
    suspend fun insert(embeddedWord: EmbeddedWord)
    suspend fun remove(embeddedWord: EmbeddedWord)
    suspend fun insertAll(embeddedWords: List<EmbeddedWord>)
}

suspend fun WordRepository.copyWordToMyCategory(wordWithCategories: WordWithCategories) {
    val updatedCategories = wordWithCategories.categories + MY_WORDS_CATEGORY
    updateWordWithCategories(
        wordWithCategories.copy(categories = updatedCategories)
    )
}

suspend fun WordRepository.copyWordToMyCategory(embeddedWord: EmbeddedWord) {
    copyWordToMyCategory(embeddedWord.toWordWithCategories())
}

suspend fun WordRepository.removeWordFromMyCategory(embeddedWord: EmbeddedWord) {
    removeWordFromMyCategory(embeddedWord.toWordWithCategories())
}

suspend fun WordRepository.removeWordFromMyCategory(wordWithCategories: WordWithCategories) {
    val updatedCategories = wordWithCategories.categories.toMutableList()
    updatedCategories.removeIf { it.name == MY_WORDS_CATEGORY.name }
    updateWordWithCategories(
        wordWithCategories.copy(
            categories = updatedCategories,
        )
    )
}
