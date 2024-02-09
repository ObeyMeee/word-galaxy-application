package ua.com.andromeda.wordgalaxy.worker

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundWorkManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun setupBackgroundWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val duration = Duration.ofHours(6)
        val showReviewWordsNotificationWork = PeriodicWorkRequestBuilder<CheckReviewWordsWorker>(
            duration
        ).setConstraints(constraints)
            .setInitialDelay(duration)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "showReviewWordsNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            showReviewWordsNotificationWork
        )
    }
}