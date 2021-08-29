package com.dreamdev.testtask.framents

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dreamdev.testtask.R
import com.dreamdev.testtask.base.BaseFragment
import com.dreamdev.testtask.constants.FragmentArguments
import com.dreamdev.testtask.databinding.NotificationGenerateFragmentLayoutBinding
import com.dreamdev.testtask.helpers.NotificationHelper

import com.dreamdev.testtask.helpers.setOnSingleClickListener
import com.dreamdev.testtask.interfaces.NotificationController
import com.dreamdev.testtask.interfaces.ViewPagerController
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.notification_generate_fragment_layout.*
import java.security.SecureRandom
import kotlin.math.min

class NotificationGenerationFragment : BaseFragment<NotificationGenerateFragmentLayoutBinding>() {
    private val TAG = "NotificationGenerationFragment"
    override fun layoutId(): Int = R.layout.notification_generate_fragment_layout
    private var viewPagerController: ViewPagerController? = null
    private var notificationController: NotificationController? = null
    private var fragmentSequenceNumber: Int? = null
    private val notificationIds = mutableListOf<Int>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSequenceNumber()
        initBindings()
        initListeners()
    }

    private fun getSequenceNumber() {
        arguments?.takeIf { it.containsKey(FragmentArguments.SEQUENCE_NUMBER) }?.apply {
            fragmentSequenceNumber = getInt(FragmentArguments.SEQUENCE_NUMBER)
        }
    }

    private fun initBindings() {
        fragmentSequenceNumber?.let {
            binding.fragmentSequenceNumber = it
        }

        viewPagerController?.let {
            binding.viewPagerController = it
        }

    }


    private fun initListeners() {
        viewPagerController?.let { viewPagerController ->
            plusBtn.setOnSingleClickListener {
                viewPagerController.addFragmentToTheEndOfViewPager()
            }

            minusBtn.setOnSingleClickListener {
                viewPagerController.removeItemFromTheEndOfViewPager()
            }
        }

        notificationBtn.setOnClickListener {
            sendNotification()
        }

    }

    private fun sendNotification() {
        notificationController?.sendNotification(fragmentSequenceNumber!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            viewPagerController = context as ViewPagerController
            notificationController = context as NotificationController
        } catch (castException: ClassCastException) {
            Log.e(TAG, castException.toString())
        }
    }


}