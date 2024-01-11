package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import java.time.LocalDate

@Dao
interface WordDao {
    @Transaction
    @Query(
        """
        SELECT *
        FROM word
        WHERE status = :status
        ORDER BY RANDOM() 
        LIMIT 1
        """
    )
    fun findOneRandomWordWhereStatusEquals(status: WordStatus): Flow<List<EmbeddedWord>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM word
        WHERE strftime('%s', 'now', 'localtime') > strftime('%s', next_repeat_at)
        ORDER BY RANDOM() 
        LIMIT 1
        """
    )
    fun findRandomWordToReview(): Flow<List<EmbeddedWord>>

    @Query(
        """
        SELECT COUNT(*) 
        FROM word
        WHERE status = 'Memorized' 
        AND strftime('%Y-%m-%d', status_changed_at) = strftime('%Y-%m-%d', 'now', 'localtime');
        """
    )
    fun countMemorizedWordsToday(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) 
        FROM word
        WHERE status = 'Memorized' 
        AND strftime('%Y-%m-%d', 'now', 'localtime') < strftime('%Y-%m-%d', repeated_at);
        """
    )
    fun countReviewedWordsToday(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM word
        WHERE status = 'Memorized'
        AND strftime('%s', 'now', 'localtime') > strftime('%s', next_repeat_at);
        """
    )
    fun countWordsToReview(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM word
        WHERE status = :status
        """
    )
    fun countWordsWhereStatusEquals(status: WordStatus): Flow<Int>


    @Query(
        """
        SELECT 
            CASE 
                WHEN (status = 'Memorized' AND amount_repetition = 0) THEN 'InProgress'
                ELSE status
            END as status,
            COUNT(*) as count
        FROM Word
        WHERE (status != 'Memorized' AND strftime('%Y-%m-%d', status_changed_at) = strftime('%Y-%m-%d', :date))
           OR ((status = 'Memorized' AND amount_repetition > 0) AND strftime('%Y-%m-%d', repeated_at) = strftime('%Y-%m-%d', :date))
        GROUP BY status;
        """
    )
    fun countWordsByStatusAt(date: LocalDate):
            Map<@MapColumn(columnName = "status") WordStatus, @MapColumn(columnName = "count") Int>

    @Update
    suspend fun updateWord(word: Word)

    @Insert
    suspend fun insertWord(word: Word): Long

    @Insert
    suspend fun insertAllWords(words: List<Word>): List<Long>

    @Insert
    suspend fun insertPhonetics(phonetics: List<Phonetic>): List<Long>

    @Insert
    suspend fun insertExamples(examples: List<Example>): List<Long>

    @Insert
    suspend fun insertCategories(categories: List<Category>): List<Long>
}