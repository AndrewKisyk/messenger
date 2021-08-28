package com.dreamdev.testtask.activities


import android.os.Bundle
import android.util.Log
import com.dreamdev.testtask.R
import com.dreamdev.testtask.adapters.DynamicPagerAdapter
import com.dreamdev.testtask.base.BaseActivity
import com.dreamdev.testtask.constants.FragmentArguments
import com.dreamdev.testtask.constants.VIEW_PAGER_ANIMATION_DURATION
import com.dreamdev.testtask.databinding.ActivityMainBinding
import com.dreamdev.testtask.enums.ItemsChangesType
import com.dreamdev.testtask.enums.ViewPagerAnimationState
import com.dreamdev.testtask.framents.NotificationGenerationFragment
import com.dreamdev.testtask.helpers.PageChangeCallback
import com.dreamdev.testtask.interfaces.ViewPagerController
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPagerController {
    private val TAG = "MainActivity"
    private var pagerAdapter: DynamicPagerAdapter? = null
    private var pageChangeCallback: PageChangeCallback? = null
    private var lastFragmentRemovingAction: Boolean = false
    private val disposables = CompositeDisposable()
    override fun layoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewPagerAdapter()
        initViewPager()
        initViewPagerCallBacks()
        addFragmentToTheEndOfViewPager()
        subscribeOnDynamicPagerItemsSetChange()
        subscribeOnPagerAnimation()
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
        pagerAdapter?.addFragment(
            provideNewNotificationGenerationFragment(
                pagerAdapter?.getNextFragmentSequenceNumber()
            )
        )
    }

    override fun removeItemFromTheEndOfViewPager() {
        if (pager.currentItem == pagerAdapter?.getLastFragmentPosition()) {
            gotToSecondLastFragment()
            lastFragmentRemovingAction = true
        } else {
            pagerAdapter?.removeLastFragment()
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
                            ItemsChangesType.ITEM_ADD -> {
                                goToFragment(dynamicPagerAdapter.getLastFragmentPosition())
                            }
                            else -> return@subscribe
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
                        pagerAdapter?.removeLastFragment()
                        lastFragmentRemovingAction = false
                    }
                }
            }
        }, ::logError))
    }


    private fun logError(e: Throwable) {
        Log.d(TAG, e.toString())
    }

}