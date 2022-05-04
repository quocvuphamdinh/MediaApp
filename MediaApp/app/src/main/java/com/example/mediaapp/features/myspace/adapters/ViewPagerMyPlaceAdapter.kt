package com.example.mediaapp.features.myspace.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mediaapp.features.myspace.file.MySpaceFileFragment
import com.example.mediaapp.features.myspace.image.MySpaceImageFragment
import com.example.mediaapp.features.myspace.music.MySpaceMusicFragment
import com.example.mediaapp.features.myspace.video.MySpaceVideoFragment

class ViewPagerMyPlaceAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return MySpaceMusicFragment()
            1-> return MySpaceVideoFragment()
            2-> return MySpaceImageFragment()
            3-> return MySpaceFileFragment()
        }
        return MySpaceMusicFragment()
    }
}