package com.dreamdev.testtask.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.dreamdev.testtask.R
import com.dreamdev.testtask.activities.MainActivity

class NotificationHelper(context: Context) : ContextWrapper(context) {
    val PRIMARY_CHANNEL = "default"

    init {
        createNotificationChannel()
    }

    fun getNotification(title: String, body: String): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, 0)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL)
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val name = getString(R.string.notification_channel_default)
            val descriptionText = getString(R.string.notification_channel_description)
            val channel = NotificationChannel(PRIMARY_CHANNEL, name, importance)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = descriptionText
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notificationId: Int, builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun cancelNotification(notificationId: Int) {
        Log.d("NOtification", "Canceling")
        with(NotificationManagerCompat.from(this)) {
            cancel(notificationId)
        }
    }


}