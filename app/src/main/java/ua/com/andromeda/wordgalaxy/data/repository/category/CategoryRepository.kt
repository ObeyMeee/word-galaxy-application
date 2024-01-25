package ua.com.andromeda.wordgalaxy.data.repository.category

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.VocabularyCategory

interface CategoryRepository {
    fun findVocabularyCategories(parentCategoryId: Int?): Flow<List<VocabularyCategory>>
}
