package ua.com.andromeda.wordgalaxy.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.core.data.db.dao.CategoryDao
import ua.com.andromeda.wordgalaxy.core.data.db.dao.WordDao
import ua.com.andromeda.wordgalaxy.core.data.db.database.AppDatabase
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
            appContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .createFromAsset("database/word-galaxy.db")
            .fallbackToDestructiveMigration()
            .build()
}