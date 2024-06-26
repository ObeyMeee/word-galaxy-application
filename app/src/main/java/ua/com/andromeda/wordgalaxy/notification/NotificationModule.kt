package ua.com.andromeda.wordgalaxy.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object NotificationModule {
    @Provides
    fun provideReviewWordsNotificationService(
        @ApplicationContext appContext: Context
    ): NotificationService = ReviewWordsNotificationService(appContext)
}