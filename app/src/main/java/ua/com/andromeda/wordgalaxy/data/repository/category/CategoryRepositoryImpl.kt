package ua.com.andromeda.wordgalaxy.data.repository.category

import ua.com.andromeda.wordgalaxy.data.dao.CategoryDao

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun findVocabularyCategories(parentCategoryId: Int?) =
        categoryDao.findCategoriesWithWordCountAndCompletedWordsCount(parentCategoryId)

    override fun findChildCategories() =
        categoryDao.findCategoriesWhereParentIsNotNull()
}