package ua.com.andromeda.wordgalaxy.data.repository.category

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

interface CategoryRepository {
    fun findAllVocabularyCategories(): Flow<List<VocabularyCategory>>
    fun findAll(): Flow<List<Category>>
    fun findById(id: Long): Flow<Category?>
    suspend fun insert(vararg categories: Category)
}
