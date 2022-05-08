package com.example.mediaapp.features.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val fragments:List<Fragment>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return fragments[0]
            1-> return fragments[1]
            2-> return fragments[2]
            3-> return fragments[3]
        }
        return fragments[0]
    }
}