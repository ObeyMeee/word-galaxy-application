package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun findAll(): Flow<List<Category>>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT 
            categories.*, 
            COUNT(words_categories.word_id) AS total_words, 
            SUM(CASE WHEN Word.status IN ('AlreadyKnown', 'Mastered') THEN 1 ELSE 0 END) AS known_words, 
                CASE WHEN COUNT(words_categories.word_id) > 0 
                THEN (CAST(SUM(CASE WHEN Word.status IN ('AlreadyKnown', 'Mastered') THEN 1 ELSE 0 END) AS REAL) / COUNT(words_categories.word_id)) * 100 
                ELSE 0 END AS completed_words 
        FROM categories 
        LEFT JOIN words_categories ON categories.id = words_categories.category_id 
        LEFT JOIN Word ON words_categories.word_id = Word.id 
        GROUP BY categories.id
        ORDER BY categories.name
        """
    )
    fun findCategoriesWithWordCountAndCompletedWordsCount(): Flow<List<VocabularyCategory>>

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :id
        """
    )
    fun findByCategoryId(id: Long): Flow<Category?>

    @Insert
    fun insert(vararg categories: Category)
}