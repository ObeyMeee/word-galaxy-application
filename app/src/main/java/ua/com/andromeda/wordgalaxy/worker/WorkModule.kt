package ua.com.andromeda.wordgalaxy.worker

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object WorkModule {
    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager =
        WorkManager.getInstance(appContext)
}