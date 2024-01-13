package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordAndCategoryCrossRef
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories
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

    @Query(
        """
    SELECT *
    FROM categories
    WHERE id IN (
        SELECT category_id
        FROM words_categories
        WHERE word_id = :wordId
    )
    """
    )
    fun getCategoriesForWord(wordId: Long): List<Category>

    @Query(
        """
            SELECT id FROM categories WHERE name = :name
        """
    )
    fun findCategoryIdByName(name: String): Long?

    @Query(
        """
    DELETE FROM words_categories
    WHERE word_id = :wordId AND category_id IN (:categoryIds)
    """
    )
    suspend fun deleteWordAndCategories(wordId: Long, categoryIds: List<Long>)

    @Update
    suspend fun updateWord(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordAndCategories(wordAndCategoryCrossRef: List<WordAndCategoryCrossRef>)

    @Transaction
    suspend fun insertWordWithCategories(wordWithCategories: WordWithCategories): Long {
        val (word, categories) = wordWithCategories
        val wordId = insertWord(word)

        val categoriesIds = insertCategories(categories)
        val wordAndCategoryCrossRefs = categoriesIds.map {
            WordAndCategoryCrossRef(wordId, it)
        }
        insertWordAndCategories(wordAndCategoryCrossRefs)
        return wordId
    }

    @Transaction
    suspend fun insertAllWords(wordsWithCategories: List<WordWithCategories>) =
        wordsWithCategories.map { insertWordWithCategories(it) }

    @Transaction
    suspend fun updateWordWithCategories(wordWithCategories: WordWithCategories) {
        val (word, categories) = wordWithCategories

        // Update the word
        updateWord(word)

        // Update the categories
        val wordId = word.id
        val existingCategories = getCategoriesForWord(wordId)
        val newCategories = categories.filterNot { it in existingCategories }

        // Insert new categories
        val newCategoriesIds = insertCategories(newCategories).map { it }

        // Delete categories that are no longer associated with the word
        val categoriesToDelete = existingCategories.filterNot { it in categories }
        deleteWordAndCategories(wordId, categoriesToDelete.map { it.id })

        // Insert the associations between the updated word and its categories
        val updatedWordAndCategoryCrossRefs =
            newCategoriesIds.map { WordAndCategoryCrossRef(wordId, it) }
        insertWordAndCategories(updatedWordAndCategoryCrossRefs)
    }

    @Insert
    suspend fun insertPhonetics(phonetics: List<Phonetic>): List<Long>

    @Insert
    suspend fun insertExamples(examples: List<Example>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCategories(categories: List<Category>): List<Long>

    @Transaction
    suspend fun insertCategories(categories: List<Category>): List<Long> {
        val categoriesIds = insertAllCategories(categories)
        return categoriesIds.mapIndexed { i, categoryId ->
            if (categoryId == -1L) {
                findCategoryIdByName(categories[i].name)!!
            } else {
                categoryId
            }
        }
    }
}