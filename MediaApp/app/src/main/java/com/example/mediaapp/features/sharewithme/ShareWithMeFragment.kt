package com.example.mediaapp.features.sharewithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentShareWithMeBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.sharewithme.file.ShareWithMeFileFragment
import com.example.mediaapp.features.sharewithme.image.ShareWithMeImageFragment
import com.example.mediaapp.features.sharewithme.music.ShareWithMeMusicFragment
import com.example.mediaapp.features.sharewithme.video.ShareWithMeVideoFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ShareWithMeFragment : Fragment() {
    private lateinit var binding: FragmentShareWithMeBinding
    private lateinit var viewPagerShareWithMeAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "Share with me"

        setUpViewPagerWithTabLayout()

        binding.tabLayoutShareWithMe.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.tabLayoutShareWithMe.setSelectedTabIndicatorColor(
                        resources.getColor(
                            R.color.tab1_indicator_color
                        )
                    )
                    1 -> binding.tabLayoutShareWithMe.setSelectedTabIndicatorColor(
                        resources.getColor(
                            R.color.tab2_indicator_color
                        )
                    )
                    2 -> binding.tabLayoutShareWithMe.setSelectedTabIndicatorColor(
                        resources.getColor(
                            R.color.tab3_indicator_color
                        )
                    )
                    3 -> binding.tabLayoutShareWithMe.setSelectedTabIndicatorColor(
                        resources.getColor(
                            R.color.tab4_indicator_color
                        )
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPagerShareWithMeAdapter = ViewPagerAdapter(
            requireActivity(), listOf(
                ShareWithMeMusicFragment(),
                ShareWithMeVideoFragment(),
                ShareWithMeImageFragment(),
                ShareWithMeFileFragment()
            )
        )
        binding.viewPagerShareWithMe.adapter = viewPagerShareWithMeAdapter
        TabLayoutMediator(
            binding.tabLayoutShareWithMe,
            binding.viewPagerShareWithMe
        ) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.tab1)
                1 -> tab.setIcon(R.drawable.tab2)
                2 -> tab.setIcon(R.drawable.tab3)
                3 -> tab.setIcon(R.drawable.tab4)
            }
        }.attach()
    }
}