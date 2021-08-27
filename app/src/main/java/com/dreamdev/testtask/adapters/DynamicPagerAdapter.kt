package com.dreamdev.testtask.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class DynamicPagerAdapter(@androidx.annotation.NonNull fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val items = mutableListOf<Fragment>()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }

    fun getNextFragmentSequenceNumber(): Int {
        return items.size + 1
    }

    fun getItem(position: Int): Fragment {
        return items[position]
    }

    fun addFragment(fragment: Fragment) {
        items.add(fragment)
        notifyDataSetChanged()
    }

    fun removeLastFragment() {
        items.removeLast()
        notifyDataSetChanged()
    }

    fun getLastFragmentPosition(): Int {
        return items.size - 1
    }


}