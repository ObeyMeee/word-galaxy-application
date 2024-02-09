package ua.com.andromeda.wordgalaxy.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.data.AppDatabase
import ua.com.andromeda.wordgalaxy.data.dao.CategoryDao
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideWordDao(database: AppDatabase): WordDao =
        database.wordDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.categoryDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(
            appContext, AppDatabase::class.java,
            "word-galaxy.db"
        )
            .fallbackToDestructiveMigration()
            .createFromAsset("database/word-galaxy.db")
            .build()
}