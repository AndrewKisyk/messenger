package com.dreamdev.testtask.adapters

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dreamdev.testtask.enums.ItemsChangesType
import com.dreamdev.testtask.framents.NotificationGenerationFragment
import io.reactivex.Observable

class DynamicPagerAdapter(@androidx.annotation.NonNull fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val items = mutableListOf<NotificationGenerationFragment>()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): NotificationGenerationFragment {
        return items[position]
    }

    fun setItems(newItems: List<NotificationGenerationFragment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getNextFragmentSequenceNumber(): Int {
        return items.size + 1
    }

    fun getItem(position: Int): NotificationGenerationFragment {
        return items[position]
    }

    fun addFragment(fragment: NotificationGenerationFragment) {
        items.add(fragment)
        passToObservable(ItemsChangesType.ITEM_ADD)
    }

    fun removeLastFragment() {
        val fragment = getItem(getLastFragmentPosition())
        fragment.cancelThisFragmetNotification()
        fragment.onDestroy()
        getItem(getLastFragmentPosition()).onDestroy()
        items.removeLast()
        passToObservable(ItemsChangesType.ITEM_REMOVED)
    }

    fun getLastFragmentPosition(): Int {
        return items.size - 1
    }

    val itemsChangedObservable: Observable<ItemsChangesType>
    init {
        itemsChangedObservable = Observable.create<ItemsChangesType> { subscription ->
            passToObservable = { itemsChangedType ->
               subscription.onNext(itemsChangedType)
            }
        }.doAfterNext {notifyDataSetChanged()}
    }

    private var passToObservable: (ItemsChangesType) -> Unit = {}


}