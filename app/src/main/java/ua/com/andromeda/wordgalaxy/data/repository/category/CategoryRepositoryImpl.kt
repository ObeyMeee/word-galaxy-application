package ua.com.andromeda.wordgalaxy.data.repository.category

import ua.com.andromeda.wordgalaxy.data.dao.CategoryDao
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun findVocabularyCategories(parentCategoryId: Int?) =
        categoryDao.findCategoriesWithWordCountAndCompletedWordsCount(parentCategoryId)

    override fun findAllChildCategories() =
        categoryDao.findCategoriesWhereParentIsNotNull()

    override fun findAllByParentCategoryId(parentCategoryId: Int?) =
        categoryDao.findCategoriesByParentCategoryId(parentCategoryId)
}