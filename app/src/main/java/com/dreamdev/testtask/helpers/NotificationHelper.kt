package com.dreamdev.testtask.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.dreamdev.testtask.R
import com.dreamdev.testtask.activities.MainActivity

open class NotificationHelper(context: Context) : ContextWrapper(context) {
    val PRIMARY_CHANNEL = "default"

    init {
        createNotificationChannel()
    }

    fun getNotification(title: String, body: String): NotificationCompat.Builder {
        val intent = Intent(baseContext, MainActivity::class.java).apply {
            flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Builder(baseContext, PRIMARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_messenger_icon)
            .setLargeIcon(getIcon())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
    }

    private fun getIcon(): Bitmap? {
        return AppCompatResources
            .getDrawable(this, R.drawable.ic_notification_icon)
            ?.toBitmap()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val name = getString(R.string.notification_channel_default)
            val descriptionText = getString(R.string.notification_channel_description)
            val channel = NotificationChannel(PRIMARY_CHANNEL, name, importance)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = descriptionText
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notificationId: Int, builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(baseContext)) {
            notify(notificationId, builder.build())
        }
    }

    fun cancelNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(baseContext)) {
            cancel(notificationId)
        }
    }


}