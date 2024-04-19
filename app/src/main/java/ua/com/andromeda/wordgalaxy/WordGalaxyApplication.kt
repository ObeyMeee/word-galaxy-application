package ua.com.andromeda.wordgalaxy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ua.com.andromeda.wordgalaxy.utils.notification.ReviewWordsNotificationService
import ua.com.andromeda.wordgalaxy.worker.BackgroundWorkManager
import javax.inject.Inject

@HiltAndroidApp
class WordGalaxyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var backgroundWorkManager: BackgroundWorkManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        backgroundWorkManager.setupBackgroundWork()
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

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}