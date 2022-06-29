package com.example.mediaapp.features.myshare

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
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMyShareBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.features.myshare.document.MyShareFileFragment
import com.example.mediaapp.features.myshare.image.MyShareImageFragment
import com.example.mediaapp.features.myshare.music.MyShareMusicFragment
import com.example.mediaapp.features.myshare.video.MyShareVideoFragment
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.features.util.ViewReceiverDialogFragment
import com.example.mediaapp.features.util.WarningDialogFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.util.FileUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyShareFragment : Fragment() {
    private lateinit var binding: FragmentMyShareBinding
    private lateinit var viewPagerMyShareAdapter: ViewPagerAdapter
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private val viewModel: MyShareViewModel by activityViewModels {
        MyShareViewModelFactory((activity?.application as MediaApplication).repository)
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
        binding = FragmentMyShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "My Share"
        setUpViewPagerWithTabLayout()

        binding.tabLayoutMyShare.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab1_indicator_color))
                    1 -> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab2_indicator_color))
                    2 -> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab3_indicator_color))
                    3 -> binding.tabLayoutMyShare.setSelectedTabIndicatorColor(resources.getColor(R.color.tab4_indicator_color))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        subcribeToObserver()
    }

    private fun showDialogViewReceiver(item: Any, isDelete: Boolean) {
        ViewReceiverDialogFragment(
            when (item) {
                is Directory -> item.receivers
                is File -> item.receivers
                else -> listOf()
            }, isDelete
        ).apply {
            if (isDelete) {
                setCLickItem { user ->
                    showWarningDialog(user, item)
                    closeDialog()
                }
            }
        }.show(parentFragmentManager, Constants.VIEW_RECEIVER_DIALOG_TAG)
    }

    private fun showWarningDialog(user: User, item: Any) {
        WarningDialogFragment(
            "Are you sure ?",
            "Do you want to delete the directory shared with ${user.firstName + user.lastName} ?"
        ).apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                viewModel.deleteDirectoryOrFileByOwner(item, user)
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
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
    private fun subcribeToObserver() {
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
                    showDialogViewReceiver(it, true)
                    viewModel.option = 0
                }
                2 -> {
                    showDialogViewReceiver(it, false)
                    viewModel.option = 0
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
        viewPagerMyShareAdapter = ViewPagerAdapter(
            requireActivity(), listOf(
                MyShareMusicFragment(),
                MyShareVideoFragment(),
                MyShareImageFragment(),
                MyShareFileFragment()
            )
        )
        binding.viewPagerMyShare.adapter = viewPagerMyShareAdapter
        TabLayoutMediator(binding.tabLayoutMyShare, binding.viewPagerMyShare) { tab, position ->
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