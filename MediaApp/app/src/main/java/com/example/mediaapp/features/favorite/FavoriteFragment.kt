package com.example.mediaapp.features.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFavoriteBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.favorite.document.FavoriteFileFragment
import com.example.mediaapp.features.favorite.image.FavoriteImageFragment
import com.example.mediaapp.features.favorite.music.FavoriteMusicFragment
import com.example.mediaapp.features.favorite.video.FavoriteVideoFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FavoriteFragment : Fragment() {

    private lateinit var binding : FragmentFavoriteBinding
    private lateinit var viewPagerMyPlaceAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "Favorite"
        setUpViewPagerWithTabLayout()

        binding.tabLayoutFavorite.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0-> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab1_indicator_color))
                    1-> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab2_indicator_color))
                    2-> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab3_indicator_color))
                    3-> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab4_indicator_color))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
    private fun setUpViewPagerWithTabLayout() {
        viewPagerMyPlaceAdapter = ViewPagerAdapter(requireActivity(), listOf(
            FavoriteMusicFragment(),
            FavoriteVideoFragment(),
            FavoriteImageFragment(),
            FavoriteFileFragment()
        ))
        binding.viewPagerFavorite.adapter = viewPagerMyPlaceAdapter
        TabLayoutMediator(binding.tabLayoutFavorite, binding.viewPagerFavorite) { tab, position ->
            when(position){
                0-> tab.setIcon(R.drawable.tab1)
                1-> tab.setIcon(R.drawable.tab2)
                2-> tab.setIcon(R.drawable.tab3)
                3-> tab.setIcon(R.drawable.tab4)
            }
        }.attach()
    }
}