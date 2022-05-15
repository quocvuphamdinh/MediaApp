package com.example.mediaapp.features.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMySpaceBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.myspace.file.MySpaceFileFragment
import com.example.mediaapp.features.myspace.image.MySpaceImageFragment
import com.example.mediaapp.features.myspace.music.MySpaceMusicFragment
import com.example.mediaapp.features.myspace.video.MySpaceVideoFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MySpaceFragment : Fragment() {
    private lateinit var binding: FragmentMySpaceBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "My Space"

        setUpViewPagerWithTabLayout()

        binding.tabLayoutMyPlace.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.tabLayoutMyPlace.setSelectedTabIndicatorColor(resources.getColor(R.color.tab1_indicator_color))
                    1 -> binding.tabLayoutMyPlace.setSelectedTabIndicatorColor(resources.getColor(R.color.tab2_indicator_color))
                    2 -> binding.tabLayoutMyPlace.setSelectedTabIndicatorColor(resources.getColor(R.color.tab3_indicator_color))
                    3 -> binding.tabLayoutMyPlace.setSelectedTabIndicatorColor(resources.getColor(R.color.tab4_indicator_color))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPagerAdapter = ViewPagerAdapter(
            requireActivity(), listOf(
                MySpaceMusicFragment(),
                MySpaceVideoFragment(),
                MySpaceImageFragment(),
                MySpaceFileFragment()
            )
        )
        binding.viewPagerMyPlace.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayoutMyPlace, binding.viewPagerMyPlace) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.tab1)
                1 -> tab.setIcon(R.drawable.tab2)
                2 -> tab.setIcon(R.drawable.tab3)
                3 -> tab.setIcon(R.drawable.tab4)
            }
        }.attach()
    }
}