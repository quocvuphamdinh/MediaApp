package com.example.mediaapp.features.sharewithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentShareWithMeBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.sharewithme.document.ShareWithMeFileFragment
import com.example.mediaapp.features.sharewithme.image.ShareWithMeImageFragment
import com.example.mediaapp.features.sharewithme.music.ShareWithMeMusicFragment
import com.example.mediaapp.features.sharewithme.video.ShareWithMeVideoFragment
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.features.util.WarningDialogFragment
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ShareWithMeFragment : Fragment() {
    private lateinit var binding: FragmentShareWithMeBinding
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private lateinit var viewPagerShareWithMeAdapter: ViewPagerAdapter
    private val viewModel: ShareWithMeViewModel by activityViewModels {
        ShareWithMeViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFolderRoots()
    }
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
        subcribeToObservers()

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
    private fun showDialogWarning(item: Any?){
        WarningDialogFragment("Are you sure ?", "Do you want to delete ${if (item is File) "file" else "directory"} ?").apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                viewModel.deleteDirectoryOrFileShareByCustomer(item!!)
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }

    private fun subcribeToObservers() {
        viewModel.directoryAndFileLongClick.observe(viewLifecycleOwner, Observer {
            when(viewModel.option){
                1 -> {
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    viewModel.addFileOrDirectoryToFavorite(it)
                    viewModel.option = 0
                }
                2 -> {
                    showDialogWarning(it)
                    viewModel.option = 0
                }
            }
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
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

    override fun onPause() {
        super.onPause()
        viewModel.resetToast()
    }
}