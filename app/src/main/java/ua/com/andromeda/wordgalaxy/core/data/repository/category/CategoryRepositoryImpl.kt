package ua.com.andromeda.wordgalaxy.core.data.repository.category

import ua.com.andromeda.wordgalaxy.core.data.db.dao.CategoryDao
import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun findAllVocabularyCategories() =
        categoryDao.findCategoriesWithWordCountAndCompletedWordsCount()

    override fun findAll() =
        categoryDao.findAll()

    override fun findById(id: Long) =
        categoryDao.findByCategoryId(id)

    override suspend fun insert(vararg categories: Category) =
        categoryDao.insert(*categories)
}