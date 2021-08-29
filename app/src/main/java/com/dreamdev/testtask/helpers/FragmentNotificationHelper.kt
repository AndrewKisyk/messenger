package com.dreamdev.testtask.helpers

import android.content.Context
import android.util.Log
import com.dreamdev.testtask.R
import java.security.SecureRandom

class FragmentNotificationHelper(context: Context) : NotificationHelper(context) {
    private var secureRandom = SecureRandom()

    private var fragmentNotificationIds = mutableMapOf<Int, MutableList<Int>>()

    fun createNotificationByFragmentSequenceNumber(fragmentSequenceNumber: Int) {
        val id = generateNotificationId()
        putIdToFragmentNotificationIds(fragmentSequenceNumber, id)
        showNotification(
            notificationId = id,
            builder = getNotification(
                getString(R.string.notification_title),
                getString(R.string.notification_body) + " $fragmentSequenceNumber",
                fragmentSequenceNumber
            )
        )
    }

    fun cancelAllFragmentNotifications(fragmentSequenceNumber: Int) {
        fragmentNotificationIds[fragmentSequenceNumber]?.forEach {
            cancelNotification(it)
        }

        fragmentNotificationIds.remove(fragmentSequenceNumber)
    }

    private fun putIdToFragmentNotificationIds(fragmentSequenceNumber: Int, id: Int) {
        if(fragmentNotificationIds.containsKey(fragmentSequenceNumber)) {
            fragmentNotificationIds[fragmentSequenceNumber]!!.add(id)
        } else {
            fragmentNotificationIds.put(fragmentSequenceNumber, mutableListOf(id))
        }
    }

    private fun generateNotificationId(): Int {
        return secureRandom.nextInt(10000)
    }

}