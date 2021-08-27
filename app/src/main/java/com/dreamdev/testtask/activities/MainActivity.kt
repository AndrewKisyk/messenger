package com.dreamdev.testtask.activities


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.dreamdev.testtask.R
import com.dreamdev.testtask.adapters.DynamicPagerAdapter
import com.dreamdev.testtask.base.BaseActivity
import com.dreamdev.testtask.constants.FragmentArguments
import com.dreamdev.testtask.constants.VIEW_PAGER_ANIMATION_DURATION
import com.dreamdev.testtask.databinding.ActivityMainBinding
import com.dreamdev.testtask.framents.NotificationGenerationFragment
import com.dreamdev.testtask.interfaces.ViewPagerController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPagerController {
    private var pagerAdapter: DynamicPagerAdapter? = null
    override fun layoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewPagerAdapter()
        initViewPager()
        addFragmentToTheEndOfViewPager()
    }

    private fun initViewPager() {
        pager.adapter = pagerAdapter
    }

    private fun initViewPagerAdapter() {
        pagerAdapter = DynamicPagerAdapter(supportFragmentManager, lifecycle)
    }

    override fun addFragmentToTheEndOfViewPager() {
        pagerAdapter?.addFragment(
            provideNewNotificationGenerationFragment(
                pagerAdapter?.getNextFragmentSequenceNumber()
            )
        )

        pagerAdapter?.let { goToFragment(it.getLastFragmentPosition()) }
    }

    override fun removeItemFromTheEndOfViewPager() {
        pagerAdapter?.let { pagerAdapter ->
            if (pager.currentItem == pagerAdapter.getLastFragmentPosition()) {
                goToFragment(pagerAdapter.getLastFragmentPosition() - 1)
                Handler(Looper.getMainLooper()).postDelayed({
                    pagerAdapter.removeLastFragment()
                }, VIEW_PAGER_ANIMATION_DURATION)
            } else {
                pagerAdapter.removeLastFragment()
            }
        }
    }

    private fun goToFragment(fragmentSequenceNumber: Int) {
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

}