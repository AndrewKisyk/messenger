package com.dreamdev.testtask.helpers


import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.dreamdev.testtask.enums.ViewPagerAnimationState
import io.reactivex.Observable

class PageChangeCallback : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE -> doAfterPagerAnimation(ViewPagerAnimationState.ANIMATION_ENDS)
        }
    }

    val pagerAnimationObservable: Observable<ViewPagerAnimationState>

    init {
        pagerAnimationObservable = Observable.create { subscription ->
            doAfterPagerAnimation = {
                subscription.onNext(it)
            }
        }
    }

    private var doAfterPagerAnimation: (ViewPagerAnimationState) -> Unit = {}

}