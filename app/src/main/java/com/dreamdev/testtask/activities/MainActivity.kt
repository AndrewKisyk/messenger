package com.dreamdev.testtask.activities


import android.content.IntentFilter
import android.os.Bundle
import android.util.Log

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
import com.dreamdev.testtask.utils.NotificationReceiver
import com.dreamdev.testtask.utils.Preferences
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPagerController,
    NotificationController {
    private val TAG = "MainActivity"
    private var pagerAdapter: DynamicPagerAdapter? = null
    private var pageChangeCallback: PageChangeCallback? = null
    private var lastFragmentRemovingAction: Boolean = false
    private var notificationHelper: FragmentNotificationHelper? = null
    private val disposables = CompositeDisposable()
    private var notificationReceiver: NotificationReceiver? = null
    private var preferences: Preferences? = null
    override fun layoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(this)
        setUpViewPager()
        setUpNotifications()
    }

    private fun setUpViewPager() {
        initViewPagerAdapter()
        initViewPager()
        initViewPagerCallBacks()
        subscribeOnDynamicPagerItemsSetChange()
        subscribeOnPagerAnimation()
        restoreViewPager()
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

    private fun restoreViewPager() {
        disposables.add(
            Single.just(preferences!!.fragmentsCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ repeat(it, { addFragmentToTheEndOfViewPager() }) }, ::logError)
        )
    }

    private fun saveViewPagerFragmentCount() {
        disposables.add(Single.just(pagerAdapter?.itemCount)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { preferences!!.fragmentsCount = it ?: 1 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "Saved fragments count ") }, ::logError)
        )
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

    private fun goToFragment(fragmentPosition: Int) {
        pager.isUserInputEnabled = false
        pager.currentItem = fragmentPosition
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
                                saveViewPagerFragmentCount()
                            }
                            is ItemsChangesAction.Removing -> {
                                cancelNotifications(fragmentSequenceNumber = pagerAdapter!!.itemCount)
                                pager.post {
                                    dynamicPagerAdapter.removeLastFragment()
                                    saveViewPagerFragmentCount()
                                }
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
                null -> return@subscribe
            }
        }, ::logError))
    }

    private fun setUpNotifications() {
        initFragmentNotificationHelper()
        setUpReceiver()
        subscribeOnNotificationPressed()
    }

    private fun setUpReceiver() {
        val filter = IntentFilter(NotificationHelper.MESSAGE_ACTION)
        notificationReceiver = NotificationReceiver()
        registerReceiver(notificationReceiver, filter)
    }

    private fun subscribeOnNotificationPressed() {
        notificationReceiver?.notificationPressedObservable
            ?.subscribe(::goToFragment, ::logError)
            ?.let { disposables.add(it) }
    }

    private fun initFragmentNotificationHelper() {
        notificationHelper = FragmentNotificationHelper(this)
    }

    override fun sendNotification(fragmentSequenceNumber: Int) {
        notificationHelper?.createNotificationByFragmentSequenceNumber(fragmentSequenceNumber)
    }

    private fun cancelNotifications(fragmentSequenceNumber: Int) {
        notificationHelper?.cancelAllFragmentNotifications(fragmentSequenceNumber)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        unregisterReceiver(notificationReceiver)
    }
}