package ua.com.andromeda.wordgalaxy.worker

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.DEFAULT_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreConstants.KEY_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundWorkManager @Inject constructor(
    private val workManager: WorkManager,
    private val dataStoreHelper: PreferenceDataStoreHelper,
) {
    fun setupBackgroundWork() {
        CoroutineScope(Dispatchers.IO).launch {
            val frequency = dataStoreHelper.first(
                KEY_NOTIFICATIONS_FREQUENCY,
                DEFAULT_NOTIFICATIONS_FREQUENCY.toString(),
            )
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val duration = Duration.ofHours(frequency.toLong())
            val showReviewWordsNotificationWork =
                PeriodicWorkRequestBuilder<CheckReviewWordsWorker>(16, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInitialDelay(duration)
                    .build()
            workManager.enqueueUniquePeriodicWork(
                "showReviewWordsNotificationWork",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                showReviewWordsNotificationWork
            )
        }
    }
}