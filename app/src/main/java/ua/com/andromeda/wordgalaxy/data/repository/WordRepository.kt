package ua.com.andromeda.wordgalaxy.data.repository

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord

interface WordRepository {
    fun findOneRandomWordWhereStatusEquals(status: WordStatus): Flow<EmbeddedWord>

    fun countLearnedWordsToday(): Flow<Int>

    fun countWordsWhereStatusEquals(status: WordStatus): Flow<Int>

    fun countWordsToReview(): Flow<Int>

    suspend fun update(word: Word)

    suspend fun insert(embeddedWord: EmbeddedWord)

    suspend fun insertAll(embeddedWords: List<EmbeddedWord>)
}