package com.example.mediaapp.features.myspace.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mediaapp.features.myspace.file.FileFragment
import com.example.mediaapp.features.myspace.image.ImageFragment
import com.example.mediaapp.features.myspace.music.MusicFragment
import com.example.mediaapp.features.myspace.video.VideoFragment

class ViewPagerMyPlaceAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return MusicFragment()
            1-> return VideoFragment()
            2-> return ImageFragment()
            3-> return FileFragment()
        }
        return MusicFragment()
    }
}