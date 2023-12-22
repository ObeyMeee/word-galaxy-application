package ua.com.andromeda.wordgalaxy.utils.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context

private const val NOTIFICATION_ID = 1

abstract class AbstractNotificationService(
    val context: Context
) : NotificationService {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun showNotification() {
        val notification = buildNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    abstract fun buildNotification(): Notification
}