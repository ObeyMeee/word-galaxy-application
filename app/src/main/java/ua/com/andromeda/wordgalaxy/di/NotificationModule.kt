package ua.com.andromeda.wordgalaxy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.utils.notification.ReviewWordsNotificationService

@InstallIn(SingletonComponent::class)
@Module
object NotificationModule {
    @Provides
    fun provideReviewWordsNotificationService(@ApplicationContext appContext: Context) =
        ReviewWordsNotificationService(appContext)
}