package ua.com.andromeda.wordgalaxy.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration

class BackgroundWorkManager(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    @RequiresApi(Build.VERSION_CODES.O)
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