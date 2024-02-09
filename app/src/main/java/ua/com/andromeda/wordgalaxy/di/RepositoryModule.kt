package ua.com.andromeda.wordgalaxy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepositoryImpl

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindWordRepository(wordRepository: WordRepositoryImpl): WordRepository
}