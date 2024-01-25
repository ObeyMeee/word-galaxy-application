package ua.com.andromeda.wordgalaxy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ua.com.andromeda.wordgalaxy.data.AppDatabase
import ua.com.andromeda.wordgalaxy.data.repository.preferences.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository
import ua.com.andromeda.wordgalaxy.utils.notification.ReviewWordsNotificationService
import ua.com.andromeda.wordgalaxy.worker.BackgroundWorkManager

class WordGalaxyApplication : Application() {
    val appDatabase: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
)