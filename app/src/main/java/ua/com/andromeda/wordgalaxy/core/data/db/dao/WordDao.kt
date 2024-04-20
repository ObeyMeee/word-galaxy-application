package ua.com.andromeda.wordgalaxy.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.Example
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordAndCategoryCrossRef
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.WordWithCategories

@Dao
interface WordDao {
    @Transaction
    @Query(
        """
        SELECT *
        FROM word
        WHERE status = :status
        ORDER BY RANDOM() 
        LIMIT :limit
        """
    )
    fun findRandomWordsWhereStatusEquals(status: WordStatus, limit: Int): Flow<List<EmbeddedWord>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM word
        WHERE strftime('%s', 'now', 'localtime') > strftime('%s', next_repeat_at)
        ORDER BY RANDOM() 
        LIMIT :limit
        """
    )
    fun findRandomWordToReview(limit: Int): Flow<List<EmbeddedWord>>

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
        AND strftime('%Y-%m-%d', 'now', 'localtime') = strftime('%Y-%m-%d', repeated_at);
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

    @Query("SELECT * FROM Word WHERE id = :id")
    fun findWordById(id: Long): Flow<Word>

    @Transaction
    @Query("SELECT * FROM Word WHERE id = :id")
    fun findEmbeddedWordById(id: Long): Flow<EmbeddedWord>


    @Query(
        """
        SELECT *
        FROM Word
        WHERE status NOT IN (:statuses)
        """
    )
    fun findAllWhereStatusNotIn(statuses: List<WordStatus>): Flow<List<Word>>

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
    fun findCategoriesByWordId(wordId: Long): List<Category>

    @Query(
        """
            SELECT id
            FROM categories
            WHERE name = :name
        """
    )
    fun findCategoryIdByName(name: String): Long?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT *
            FROM words_categories
            INNER JOIN word ON words_categories.word_id = word.id
            WHERE words_categories.category_id = :categoryId
            ORDER BY word.value
        """
    )
    fun findWordsByCategoryId(categoryId: Long): Flow<List<EmbeddedWord>>

    @Query(
        """
    DELETE FROM words_categories
    WHERE word_id = :wordId AND category_id IN (:categoryIds)
    """
    )
    suspend fun deleteWordAndCategories(wordId: Long, categoryIds: List<Long>)

    @Transaction
    suspend fun remove(embeddedWord: EmbeddedWord) {
        val (word, categories, phonetics, examples) = embeddedWord
        removePhonetics(phonetics)
        removeExamples(examples)
        deleteWordAndCategories(word.id, categories.map(Category::id))
        removeWord(word)
    }

    @Delete
    suspend fun removeWord(word: Word)

    @Delete
    suspend fun removePhonetics(phonetics: List<Phonetic>)

    @Delete
    suspend fun removeExamples(examples: List<Example>)

    @Update
    suspend fun updateWord(vararg word: Word)

    @Update
    suspend fun updateCategory(vararg category: Category)

    @Update
    suspend fun updatePhonetic(vararg phonetic: Phonetic)

    @Update
    suspend fun updateExample(vararg example: Example)

    @Transaction
    suspend fun updateEmbeddedWord(embeddedWord: EmbeddedWord) {
        val (word, categories, phonetics, examples) = embeddedWord
        updateWord(word)
        insertAllCategories(categories)
        insertPhonetics(phonetics)
        insertExamples(examples)
        deleteExamplesWhereIdsNotIn(examples.map { it.id }, word.id)
    }

    @Transaction
    @Query(
        """
        DELETE FROM examples
        WHERE id NOT IN (:ids) AND word_id = :wordId
        """
    )
    suspend fun deleteExamplesWhereIdsNotIn(ids: List<Long>, wordId: Long)

    @Transaction
    suspend fun updateWordWithCategories(wordWithCategories: WordWithCategories) {
        val (word, categories) = wordWithCategories

        // Update the categories
        val wordId = word.id
        val existingCategories = findCategoriesByWordId(wordId)
        val newCategories = categories.filterNot { it in existingCategories }

        // Insert new categories
        val newCategoriesIds = insertCategories(newCategories)

        // Delete categories that are no longer associated with the word
        val categoriesToDelete = existingCategories.filterNot { it in categories }
        deleteWordAndCategories(wordId, categoriesToDelete.map { it.id })

        // Insert the associations between the updated word and its categories
        val updatedWordAndCategoryCrossRefs = newCategoriesIds.map {
            WordAndCategoryCrossRef(wordId, it)
        }
        insertWordAndCategories(updatedWordAndCategoryCrossRefs)
    }

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhonetics(phonetics: List<Phonetic>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExample(vararg example: Example): List<Long>

    suspend fun insertExamples(examples: List<Example>) {
        insertExample(*examples.toTypedArray())
    }

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

    @Transaction
    @Query(
        """
        SELECT *
        FROM Word
        WHERE value = :value
        """
    )
    fun findWordByValue(value: String): Flow<List<EmbeddedWord>>

    @Transaction
    @Query(
        """
        SELECT * 
        FROM Word
        WHERE lower(value) LIKE '%' || lower(:searchValue) || '%' 
        OR lower(translation) LIKE '%' || lower(:searchValue) || '%'
        """
    )
    fun findLikeValueOrTranslationIgnoreCase(searchValue: String): Flow<List<EmbeddedWord>>
}