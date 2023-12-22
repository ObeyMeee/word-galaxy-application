package ua.com.andromeda.wordgalaxy.utils.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ua.com.andromeda.wordgalaxy.MainActivity
import ua.com.andromeda.wordgalaxy.R

class ReviewWordsNotificationService(context: Context) : AbstractNotificationService(context) {
    override fun buildNotification(): Notification {
        val activityPendingIntent = pendingIntent()
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Review words")
            .setContentText("Hey, take a moment to review your words. Regular work pays off😉")
            .setContentIntent(activityPendingIntent)
            .build()
    }

    private fun pendingIntent(): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.FLAG_IMMUTABLE else 0
        )
    }

    companion object {
        const val CHANNEL_ID = "review_words_channel"
        const val CHANNEL_NAME = "Review words reminder"
    }
}