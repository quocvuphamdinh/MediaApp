package com.example.mediaapp.features.myspace

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMySpaceBinding
import com.example.mediaapp.features.adapters.ViewPagerAdapter
import com.example.mediaapp.features.util.CreateDirectoryDialogFragment
import com.example.mediaapp.features.myspace.document.MySpaceFileFragment
import com.example.mediaapp.features.myspace.image.MySpaceImageFragment
import com.example.mediaapp.features.myspace.music.MySpaceMusicFragment
import com.example.mediaapp.features.myspace.video.MySpaceVideoFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.features.util.ShareFolderOrFileDialogFragment
import com.example.mediaapp.features.util.WarningDialogFragment
import com.example.mediaapp.models.File
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.util.Converters
import com.example.mediaapp.util.RealPathFileUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MySpaceFragment : Fragment() {
    private lateinit var binding: FragmentMySpaceBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var mActivityResult: ActivityResultLauncher<Intent>
    private var parentId: String? = null
    private var directory: Directory? = null
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private val viewModel: MySpaceViewModel by activityViewModels {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
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
        binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).binding.toolbarMain.title = "My Space"
        if (savedInstanceState != null) {
            val dialogCreateDirectory =
                parentFragmentManager.findFragmentByTag(Constants.CREATE_DIRECTORY_DIALOG_TAG) as CreateDirectoryDialogFragment?
            dialogCreateDirectory?.setClickCreate { value, radioValue ->
                if (value.isNotEmpty()) {
                    clickCreateDirectory(value, radioValue)
                    dialogCreateDirectory.cancelDialog()
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter your folder name !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setUpViewPagerWithTabLayout()
        subcribeToObservers()
        binding.fabAdd.setOnClickListener {
            viewModel.setVisibleFab(!viewModel.isShowFab.value!!)
        }
        binding.fabAddDirectory.setOnClickListener {
            showDialogCreateDirectory()
        }
        binding.fabAddFile.setOnClickListener {
            directory = null
            Constants.clickRequestPermissionToAccessFile(requireActivity()) { openFile() }
        }
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
        mActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                val intentData = result.data
                if (intentData != null) {
                    val uri = intentData.data
                    Log.d("uri", uri.toString())
                    uri?.let {
                        val path = RealPathFileUtil.getRealPathFromURI(requireContext(), it)
                        path?.let { p ->
                            viewModel.uploadFile(
                                p,
                                directory == null,
                                if (directory == null) null else directory
                            )
                        }
                    }
                }
            }
        )
    }

    private fun showDialogWarning(item: Any?) {
        WarningDialogFragment(
            "Are you sure ?",
            "Do you want to delete ${if (item is File) "file" else "directory"} ?"
        ).apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                viewModel.deleteDirectoryOrFile(item!!)
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }

    private fun addDirectoryToFavorite(any: Any) {
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        viewModel.addDirectoryOrFileToFavorite(any)
    }

    private fun openFile() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResult.launch(Intent.createChooser(intent, "Choose your file"))
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

    private fun showCreateDirectoryDialog(directory: Directory, nameYesButton: String) {
        CreateDirectoryDialogFragment(false, nameYesButton).apply {
            if (nameYesButton == "Rename") {
                setOldNameToEditText(directory.name)
            }
            setClickCreateWithoutRadioValue { value ->
                if (value.isNotEmpty()) {
                    if (nameYesButton == "Create") {
                        viewModel.createDirectory(Directory(value, directory.level, directory.id!!))
                    } else {
                        viewModel.editDirectory(directory, value)
                    }
                    cancelDialog()
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter your folder name !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.show(parentFragmentManager, Constants.CREATE_DIRECTORY_DIALOG_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPDF(byteString: String, nameFile: String) {
        val file = Converters.toFilePDFOrMP3OrMP4(requireContext(), byteString, nameFile, Constants.DOCUMENT, "Documents")

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
                    if (it is Directory) {
                        showCreateDirectoryDialog(it, "Create")
                        viewModel.option = 0
                    }
                }
                2 -> {
                    if (it is Directory) {
                        this@MySpaceFragment.directory = it
                        Constants.clickRequestPermissionToAccessFile(requireActivity()) { openFile() }
                        viewModel.option = 0
                    }
                }
                3 -> {
                    showDialogSearchAccount(it)
                    viewModel.option = 0
                }
                4 -> {
                    addDirectoryToFavorite(it)
                    viewModel.option = 0
                }
                5 -> {
                    if (it is Directory) {
                        showCreateDirectoryDialog(it, "Rename")
                        viewModel.option = 0
                    }
                }
                6 -> {
                    showDialogWarning(it)
                    viewModel.option = 0
                }
                7 -> {
                    if (it is File) {
                        viewModel.option = 0
                    }
                }
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
        })
        viewModel.isShowFab.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.fabAddDirectory.startAnimation(fromBottom)
                binding.fabAddFile.startAnimation(fromBottom)
                binding.fabAdd.startAnimation(rotateOpen)
                binding.fabAddDirectory.visibility = View.VISIBLE
                binding.fabAddFile.visibility = View.VISIBLE
            } else {
                binding.fabAddDirectory.startAnimation(toBottom)
                binding.fabAddFile.startAnimation(toBottom)
                binding.fabAdd.startAnimation(rotateClose)
                binding.fabAddDirectory.visibility = View.GONE
                binding.fabAddFile.visibility = View.GONE
            }
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialogCreateDirectory() {
        CreateDirectoryDialogFragment(true, "Create").apply {
            setClickCreate { value, radioValue ->
                if (value.isNotEmpty()) {
                    clickCreateDirectory(value, radioValue)
                    cancelDialog()
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter your folder name !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.show(parentFragmentManager, Constants.CREATE_DIRECTORY_DIALOG_TAG)
    }

    private fun clickCreateDirectory(value: String, radioValue: String) {
        var level = 0
        when (radioValue) {
            "Document" -> level = 1
            "Music" -> level = 2
            "Photo" -> level = 3
            "Movie" -> level = 4
        }
        parentId = viewModel.getParentId(level)
        if (parentId!!.isNotEmpty()) {
            viewModel.createDirectory(Directory(value, level, UUID.fromString(parentId)))
        } else {
            Toast.makeText(
                requireContext(),
                "Error happend, please try again !",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    override fun onPause() {
        super.onPause()
        viewModel.resetToast()
    }
}