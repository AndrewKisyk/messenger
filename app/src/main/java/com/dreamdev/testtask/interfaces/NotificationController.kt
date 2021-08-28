package com.dreamdev.testtask.interfaces

interface NotificationController {
    fun sendNotification(notificationId: Int)
    fun cancelNotification(notificationId: Int)
}