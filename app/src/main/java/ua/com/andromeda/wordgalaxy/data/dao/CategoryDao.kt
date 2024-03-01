package ua.com.andromeda.wordgalaxy.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

@Dao
interface CategoryDao {
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
        WHERE categories.parent_category_id IS :parentCategoryId 
        GROUP BY categories.id
        """
    )
    fun findCategoriesWithWordCountAndCompletedWordsCount(parentCategoryId: Long?): Flow<List<VocabularyCategory>>

    @Query(
        """
        SELECT * 
        FROM categories
        WHERE parent_category_id IS NOT NULL
        """
    )
    fun findCategoriesWhereParentIsNotNull(): Flow<List<Category>>

    @Query(
        """
        SELECT *
        FROM categories
        WHERE parent_category_id IS :parentCategoryId
        """
    )
    fun findCategoriesByParentCategoryId(parentCategoryId: Int?): Flow<List<Category>>

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :id
        """
    )
    fun findByCategoryId(id: Long): Flow<Category?>
}