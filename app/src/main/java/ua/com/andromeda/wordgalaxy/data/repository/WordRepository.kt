package ua.com.andromeda.wordgalaxy.data.repository

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories

interface WordRepository {
    fun findOneRandomNewWord(): Flow<WordWithCategories>

    fun countLearnedWordsToday(): Flow<Int>

    suspend fun update(word: Word)
}