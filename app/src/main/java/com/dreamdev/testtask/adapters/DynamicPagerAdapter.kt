package com.dreamdev.testtask.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dreamdev.testtask.enums.ItemsChangesAction
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

    fun startAdditionFragmentAction(fragment: Fragment) {
         passToObservable(ItemsChangesAction.Addition(fragment))
    }

    fun addFragment(fragment: Fragment) {
        items.add(fragment)
        notifyDataSetChanged()
    }

    fun startRemovingLastFragmentAction() {
        passToObservable(ItemsChangesAction.Removing(getItem(getLastFragmentPosition())))
    }


    fun removeLastFragment() {
        items.removeLast()
        Log.d("Remove", "Removing is ended")
        notifyDataSetChanged()
    }

    fun getLastFragmentPosition(): Int {
        return items.size - 1
    }


    val itemsChangedObservable: Observable<ItemsChangesAction<Fragment>>
    init {
        itemsChangedObservable = Observable.create<ItemsChangesAction<Fragment>> { subscription ->
            passToObservable = { itemsChangedType ->
                if(itemsChangedType != null) subscription.onNext(itemsChangedType)
            }
        }
    }

    private var passToObservable: (ItemsChangesAction<Fragment>?) -> Unit = {}


}