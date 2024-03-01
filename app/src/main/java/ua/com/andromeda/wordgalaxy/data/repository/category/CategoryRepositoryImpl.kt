package ua.com.andromeda.wordgalaxy.data.repository.category

import ua.com.andromeda.wordgalaxy.data.dao.CategoryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun findVocabularyCategories(parentCategoryId: Long?) =
        categoryDao.findCategoriesWithWordCountAndCompletedWordsCount(parentCategoryId)

    override fun findAllChildCategories() =
        categoryDao.findCategoriesWhereParentIsNotNull()

    override fun findAllByParentCategoryId(parentCategoryId: Int?) =
        categoryDao.findCategoriesByParentCategoryId(parentCategoryId)

    override fun findById(id: Long) =
        categoryDao.findByCategoryId(id)
}