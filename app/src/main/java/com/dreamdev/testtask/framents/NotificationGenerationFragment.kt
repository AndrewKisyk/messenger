package com.dreamdev.testtask.framents

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dreamdev.testtask.R
import com.dreamdev.testtask.base.BaseFragment
import com.dreamdev.testtask.constants.FragmentArguments
import com.dreamdev.testtask.databinding.NotificationGenerateFragmentLayoutBinding
import com.dreamdev.testtask.interfaces.ViewPagerController
import kotlinx.android.synthetic.main.notification_generate_fragment_layout.*

class NotificationGenerationFragment : BaseFragment<NotificationGenerateFragmentLayoutBinding>() {
    private val TAG = "NotificationGenerationFragment"
    override fun layoutId(): Int = R.layout.notification_generate_fragment_layout
    private var viewPagerController: ViewPagerController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(FragmentArguments.SEQUENCE_NUMBER) }?.apply {
           binding.fragmentSequenceNumber = getInt(FragmentArguments.SEQUENCE_NUMBER)
            Log.d(TAG, getInt(FragmentArguments.SEQUENCE_NUMBER).toString())
        }

        viewPagerController?.let {
            binding.viewPagerController = it
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
          viewPagerController = context as ViewPagerController
        } catch (castException: ClassCastException) {
            Log.e(TAG, castException.toString())
        }
    }





}