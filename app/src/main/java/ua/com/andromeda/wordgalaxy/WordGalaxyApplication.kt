package ua.com.andromeda.wordgalaxy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import ua.com.andromeda.wordgalaxy.data.AppDatabase
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import ua.com.andromeda.wordgalaxy.utils.notification.ReviewWordsNotificationService
import ua.com.andromeda.wordgalaxy.worker.BackgroundWorkManager
import javax.inject.Inject

@HiltAndroidApp
class WordGalaxyApplication : Application() {
    @Inject lateinit var appDatabase: AppDatabase
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        BackgroundWorkManager(applicationContext).setupBackgroundWork()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ReviewWordsNotificationService.CHANNEL_ID,
            ReviewWordsNotificationService.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Used to remind you to review words"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}