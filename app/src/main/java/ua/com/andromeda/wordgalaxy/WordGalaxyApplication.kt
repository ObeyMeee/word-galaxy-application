package ua.com.andromeda.wordgalaxy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.utils.notification.ReviewWordsNotificationService
import ua.com.andromeda.wordgalaxy.worker.BackgroundWorkManager
import javax.inject.Inject

@HiltAndroidApp
class WordGalaxyApplication : Application() {
    @Inject
    lateinit var backgroundWorkManager: BackgroundWorkManager

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        GlobalScope.launch(Dispatchers.IO) {
            backgroundWorkManager.setupBackgroundWork()
        }
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