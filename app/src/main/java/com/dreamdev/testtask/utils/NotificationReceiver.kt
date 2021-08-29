package com.dreamdev.testtask.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dreamdev.testtask.activities.MainActivity
import com.dreamdev.testtask.constants.IntentExtras
import io.reactivex.Observable

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = "NotificationReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "MessageReceived")
        Log.d(
            "Received Message",
            intent?.extras?.getInt(IntentExtras.FRAGMENT_SEQUENCE_NUMBER).toString()
        )
        moveActivityToForegroundIfNeed(context)
        passToObservable(intent?.extras?.getInt(IntentExtras.FRAGMENT_SEQUENCE_NUMBER))
    }

    val notificationPressedObservable: Observable<Int>
    init {
        notificationPressedObservable = Observable.create { subscription ->
            passToObservable = { squenceNumber ->
                if(squenceNumber != null) subscription.onNext(squenceNumber - 1)
            }
        }
    }

    private var passToObservable: (Int?) -> Unit = {}


    private fun moveActivityToForegroundIfNeed(context: Context?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context!!.startActivity(intent)
    }
}