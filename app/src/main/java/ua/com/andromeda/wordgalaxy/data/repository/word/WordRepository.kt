package ua.com.andromeda.wordgalaxy.data.repository.word

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories
import java.time.temporal.TemporalUnit

interface WordRepository {
    fun findOneRandomWordWhereStatusEquals(status: WordStatus): Flow<EmbeddedWord>

    fun findWordToReview(): Flow<EmbeddedWord?>

    fun countLearnedWordsToday(): Flow<Int>

    fun countWordsWhereStatusEquals(status: WordStatus): Flow<Int>

    fun countWordsToReview(): Flow<Int>

    fun countReviewedWordsToday(): Flow<Int>

    fun countWordsByStatusLast(value: Int, unit: TemporalUnit): List<Map<WordStatus, Int>>

    suspend fun update(word: Word)

    suspend fun insert(embeddedWord: EmbeddedWord)

    suspend fun remove(embeddedWord: EmbeddedWord)

    suspend fun insertAll(embeddedWords: List<EmbeddedWord>)

    suspend fun updateWordWithCategories(wordWithCategories: WordWithCategories)
}