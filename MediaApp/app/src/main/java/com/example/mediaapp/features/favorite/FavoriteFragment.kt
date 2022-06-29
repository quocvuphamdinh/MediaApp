package com.example.mediaapp.features.favorite

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFavoriteBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.favorite.document.FavoriteFileFragment
import com.example.mediaapp.features.favorite.image.FavoriteImageFragment
import com.example.mediaapp.features.favorite.music.FavoriteMusicFragment
import com.example.mediaapp.features.favorite.video.FavoriteVideoFragment
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.features.util.ShareFolderOrFileDialogFragment
import com.example.mediaapp.features.util.WarningDialogFragment
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.FileUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
    private lateinit var viewPagerMyPlaceAdapter: ViewPagerAdapter
    private val viewModel: FavoriteViewModel by activityViewModels {
        FavoriteViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "Favorite"
        setUpViewPagerWithTabLayout()

        binding.tabLayoutFavorite.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab1_indicator_color))
                    1 -> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab2_indicator_color))
                    2 -> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab3_indicator_color))
                    3 -> binding.tabLayoutFavorite.setSelectedTabIndicatorColor(resources.getColor(R.color.tab4_indicator_color))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        subcribeToObservers()
    }

    private fun showDialogWarning(item: Any?) {
        WarningDialogFragment(
            "Are you sure ?",
            "Do you want to remove favorite of this ${if (item is File) "file" else "directory"} ?"
        ).apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                viewModel.deleteDirectoryOrFileFromFavorite(item!!)
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }

    private fun showDialogSearchAccount(item: Any) {
        ShareFolderOrFileDialogFragment()
            .apply {
                setClickToShare { email ->
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    viewModel.addDirectoryOrFileToShare(item, email, email)
                    closeDialog()
                }
            }
            .show(parentFragmentManager, Constants.SHARE_DIALOG_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPDF(byteString: String, nameFile: String) {
        val file = FileUtil.toFilePDFOrMP3OrMP4(
            requireContext(),
            byteString,
            nameFile,
            Constants.DOCUMENT,
            "Documents"
        )

        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subcribeToObservers() {
        viewModel.isLoadFile.observe(viewLifecycleOwner, Observer {
            if (it) {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            } else {
                loadingDialogFragment.cancelDialog()
            }
        })
        viewModel.fileImage.observe(viewLifecycleOwner, Observer {
            if (viewModel.isOpenFile) {
                openPDF(it.content!!, it.name)
                viewModel.isOpenFile = false
            }
        })
        viewModel.directoryAndFileLongClick.observe(viewLifecycleOwner, Observer {
            when (viewModel.option) {
                1 -> {
                    showDialogSearchAccount(it)
                    viewModel.option = 0
                }
                2 -> {
                    showDialogWarning(it)
                    viewModel.option = 0
                }
                3 -> {
                    if (it is File) {
                        (activity as HomeActivity).getFile(it)
                        viewModel.option = 0
                    }
                }
            }
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
        })
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPagerMyPlaceAdapter = ViewPagerAdapter(
            requireActivity(), listOf(
                FavoriteMusicFragment(),
                FavoriteVideoFragment(),
                FavoriteImageFragment(),
                FavoriteFileFragment()
            )
        )
        binding.viewPagerFavorite.adapter = viewPagerMyPlaceAdapter
        TabLayoutMediator(binding.tabLayoutFavorite, binding.viewPagerFavorite) { tab, position ->
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