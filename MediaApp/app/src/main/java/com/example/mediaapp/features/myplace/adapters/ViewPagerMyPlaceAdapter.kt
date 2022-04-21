package com.example.mediaapp.features.myplace.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mediaapp.features.myplace.file.FileFragment
import com.example.mediaapp.features.myplace.image.ImageFragment
import com.example.mediaapp.features.myplace.music.MusicFragment
import com.example.mediaapp.features.myplace.video.VideoFragment

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