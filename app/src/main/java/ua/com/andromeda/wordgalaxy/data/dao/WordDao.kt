package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories

@Dao
interface WordDao {
    @Transaction
    @Query("""
        SELECT *
        FROM word
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    fun findOneRandomNewWord(): Flow<List<WordWithCategories>>

    @Query("""
        SELECT COUNT(*) 
        FROM word
        WHERE amount_repetition = 0 
        AND memorized_at BETWEEN JulianDay('now') AND JulianDay('now','+1 day','-0.001 second')
    """)
    fun countLearnedWordsToday(): Flow<Int>

    @Update
    suspend fun updateWord(word: Word)
}