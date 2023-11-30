package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
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
}