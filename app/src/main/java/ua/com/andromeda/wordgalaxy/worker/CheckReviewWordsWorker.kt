package ua.com.andromeda.wordgalaxy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.utils.notification.NotificationService
import javax.inject.Inject

private const val TAG = "CheckReviewWordsWorker"

class CheckReviewWordsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var wordRepository: WordRepository

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        return@withContext try {
            val amountWordsToReview = wordRepository.countWordsToReview().first()
            if (amountWordsToReview > 0) {
                notificationService.showNotification()
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(
                TAG,
                applicationContext.resources.getString(R.string.error_sending_notification),
                e
            )
            Result.failure()
        }
    }
}