package ua.com.andromeda.wordgalaxy.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.data.repository.word.WordRepository
import ua.com.andromeda.wordgalaxy.notification.NotificationService
import ua.com.andromeda.wordgalaxy.utils.TAG

@HiltWorker
class CheckReviewWordsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationService: NotificationService,
    private val wordRepository: WordRepository,
) : CoroutineWorker(context, params) {

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