package ua.com.andromeda.wordgalaxy.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.core.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.core.data.repository.category.CategoryRepositoryImpl
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepositoryImpl

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindWordRepository(wordRepository: WordRepositoryImpl): WordRepository

    @Binds
    abstract fun provideCategoryRepository(categoryRepository: CategoryRepositoryImpl): CategoryRepository
}