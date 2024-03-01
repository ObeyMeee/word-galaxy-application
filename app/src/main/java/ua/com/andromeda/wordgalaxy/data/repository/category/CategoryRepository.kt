package ua.com.andromeda.wordgalaxy.data.repository.category

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

interface CategoryRepository {
    fun findVocabularyCategories(parentCategoryId: Long?): Flow<List<VocabularyCategory>>
    fun findAllChildCategories(): Flow<List<Category>>
    fun findAllByParentCategoryId(parentCategoryId: Int? = null): Flow<List<Category>>
    fun findById(id: Long): Flow<Category?>
}
