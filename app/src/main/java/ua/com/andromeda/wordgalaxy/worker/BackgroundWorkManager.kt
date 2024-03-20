package ua.com.andromeda.wordgalaxy.worker

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.KEY_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreHelper
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundWorkManager @Inject constructor(
    private val workManager: WorkManager,
    private val dataStoreHelper: PreferenceDataStoreHelper,
) {
    suspend fun setupBackgroundWork() {
        val frequency = dataStoreHelper.first(
            KEY_NOTIFICATIONS_FREQUENCY,
            DEFAULT_NOTIFICATIONS_FREQUENCY.toString(),
        )
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val duration = Duration.ofHours(frequency.toLong())
        val showReviewWordsNotificationWork =
            PeriodicWorkRequestBuilder<CheckReviewWordsWorker>(duration)
                .setConstraints(constraints)
                .setInitialDelay(duration)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "showReviewWordsNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            showReviewWordsNotificationWork
        )
    }
}