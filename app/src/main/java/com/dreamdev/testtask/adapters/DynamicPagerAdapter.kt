package com.dreamdev.testtask.adapters

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dreamdev.testtask.enums.ItemsChangesType
import io.reactivex.Observable

class DynamicPagerAdapter(@androidx.annotation.NonNull fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val items = mutableListOf<Fragment>()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }


    fun setItems(newItems: List<Fragment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getNextFragmentSequenceNumber(): Int {
        return items.size + 1
    }

    fun getItem(position: Int): Fragment {
        return items[position]
    }

    fun addFragment(fragment: Fragment) {
        items.add(fragment)
        passToObservable(ItemsChangesType.ITEM_ADD)
    }

    fun removeLastFragment() {
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