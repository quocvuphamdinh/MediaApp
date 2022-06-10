package com.example.mediaapp.features.myshare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMyShareBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.features.myshare.document.MyShareFileFragment
import com.example.mediaapp.features.myshare.image.MyShareImageFragment
import com.example.mediaapp.features.myshare.music.MyShareMusicFragment
import com.example.mediaapp.features.myshare.video.MyShareVideoFragment
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.ViewReceiverDialogFragment
import com.example.mediaapp.features.util.WarningDialogFragment
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.MediaApplication
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyShareFragment: Fragment() {
    private lateinit var binding: FragmentMyShareBinding
    private lateinit var viewPagerMyShareAdapter: ViewPagerAdapter
    private val viewModel: MySpaceViewModel by activityViewModels {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "My Share"
        setUpViewPagerWithTabLayout()

        binding.tabLayoutMyShare.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0-> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab1_indicator_color))
                    1-> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab2_indicator_color))
                    2-> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab3_indicator_color))
                    3-> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab4_indicator_color))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        subcribeToObserver()
    }
    private fun showDialogViewReceiver(item: Any){
        ViewReceiverDialogFragment().apply {

        }.show(parentFragmentManager, Constants.VIEW_RECEIVER_DIALOG_TAG)
    }
    private fun showDialogWarning(item: Any){
        WarningDialogFragment("Are you sure ?", "Do you want to remove the share of this ${if (item is File) "file" else "directory"} ?").apply {
            setClickYes {

            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }

    private fun subcribeToObserver() {
        viewModel.directoryAndFileLongClick.observe(viewLifecycleOwner, Observer {
            when(viewModel.option){
                1 -> {
                    showDialogWarning(it)
                    viewModel.option = 0
                }
                2 -> {
                    showDialogViewReceiver(it)
                    viewModel.option = 0
                }
            }
        })
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPagerMyShareAdapter = ViewPagerAdapter(requireActivity(), listOf(
            MyShareMusicFragment(),
            MyShareVideoFragment(),
            MyShareImageFragment(),
            MyShareFileFragment()
        ))
        binding.viewPagerMyShare.adapter = viewPagerMyShareAdapter
        TabLayoutMediator(binding.tabLayoutMyShare, binding.viewPagerMyShare) { tab, position ->
            when(position){
                0-> tab.setIcon(R.drawable.tab1)
                1-> tab.setIcon(R.drawable.tab2)
                2-> tab.setIcon(R.drawable.tab3)
                3-> tab.setIcon(R.drawable.tab4)
            }
        }.attach()
    }
}