package com.dreamdev.testtask.activities


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.dreamdev.testtask.R
import com.dreamdev.testtask.adapters.DynamicPagerAdapter
import com.dreamdev.testtask.base.BaseActivity
import com.dreamdev.testtask.constants.FragmentArguments
import com.dreamdev.testtask.databinding.ActivityMainBinding
import com.dreamdev.testtask.enums.ItemsChangesAction
import com.dreamdev.testtask.enums.ViewPagerAnimationState
import com.dreamdev.testtask.framents.NotificationGenerationFragment
import com.dreamdev.testtask.helpers.FragmentNotificationHelper
import com.dreamdev.testtask.helpers.NotificationHelper
import com.dreamdev.testtask.helpers.PageChangeCallback
import com.dreamdev.testtask.interfaces.NotificationController
import com.dreamdev.testtask.interfaces.ViewPagerController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPagerController,
    NotificationController {
    private val TAG = "MainActivity"
    private var pagerAdapter: DynamicPagerAdapter? = null
    private var pageChangeCallback: PageChangeCallback? = null
    private var lastFragmentRemovingAction: Boolean = false
    private var notificationHelper: FragmentNotificationHelper? = null
    private val disposables = CompositeDisposable()
    override fun layoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragmentNotificationHelper()
        initViewPagerAdapter()
        initViewPager()
        initViewPagerCallBacks()
        subscribeOnDynamicPagerItemsSetChange()
        subscribeOnPagerAnimation()
        addFragmentToTheEndOfViewPager()
    }

    private fun initFragmentNotificationHelper() {
        notificationHelper = FragmentNotificationHelper(this)
    }

    private fun initViewPager() {
        pager.adapter = pagerAdapter
    }

    private fun initViewPagerAdapter() {
        pagerAdapter = DynamicPagerAdapter(supportFragmentManager, lifecycle)
    }

    private fun initViewPagerCallBacks() {
        pageChangeCallback = PageChangeCallback()
        pager.registerOnPageChangeCallback(pageChangeCallback!!)
    }


    override fun addFragmentToTheEndOfViewPager() {
        pagerAdapter?.startAdditionFragmentAction(
            provideNewNotificationGenerationFragment(
                pagerAdapter?.getNextFragmentSequenceNumber()
            )
        )
    }

    override fun removeItemFromTheEndOfViewPager() {
        if (pager.currentItem == pagerAdapter!!.getLastFragmentPosition()) {
            lastFragmentRemovingAction = true
            gotToSecondLastFragment()
        } else {
            pagerAdapter?.startRemovingLastFragmentAction()
        }
    }

    private fun goToFragment(fragmentSequenceNumber: Int) {
        pager.isUserInputEnabled = false
        pager.currentItem = fragmentSequenceNumber
    }

    private fun provideNewNotificationGenerationFragment(
        fragmentSequenceNumber: Int?
    ): NotificationGenerationFragment {
        val fragment = NotificationGenerationFragment()
        fragment.arguments = Bundle().apply {
            fragmentSequenceNumber?.let { putInt(FragmentArguments.SEQUENCE_NUMBER, it) }
        }
        return fragment
    }

    private fun subscribeOnDynamicPagerItemsSetChange() {
        pagerAdapter?.let { dynamicPagerAdapter ->
            disposables.add(
                dynamicPagerAdapter.itemsChangedObservable
                    .subscribe({ itemsChangesType ->
                        when (itemsChangesType) {
                            is ItemsChangesAction.Addition -> {
                                dynamicPagerAdapter.addFragment(itemsChangesType.item)
                                goToFragment(dynamicPagerAdapter.getLastFragmentPosition())
                            }
                            is ItemsChangesAction.Removing -> {
                                cancelNotifications(fragmentSequenceNumber = pagerAdapter!!.itemCount)
                               pager.post {  dynamicPagerAdapter.removeLastFragment() }
                            }
                        }
                    }, ::logError)
            )
        }
    }

    private fun gotToSecondLastFragment() {
        pagerAdapter?.let {
            pager.currentItem = it.getLastFragmentPosition() - 1
        }
    }

    private fun subscribeOnPagerAnimation() {
        disposables.add(pageChangeCallback!!.pagerAnimationObservable.subscribe({
            when (it) {
                ViewPagerAnimationState.ANIMATION_ENDS -> {
                    pager.isUserInputEnabled = true
                    if (lastFragmentRemovingAction) {
                        pagerAdapter?.startRemovingLastFragmentAction()
                        lastFragmentRemovingAction = false
                    }
                }
            }
        }, ::logError))
    }



    override fun sendNotification(fragmentSequenceNumber: Int) {
       notificationHelper?.createNotificationByFragmentSequenceNumber(fragmentSequenceNumber)
    }

    private fun cancelNotifications(fragmentSequenceNumber: Int) {
        notificationHelper?.cancelAllFragmentNotifications(fragmentSequenceNumber)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}