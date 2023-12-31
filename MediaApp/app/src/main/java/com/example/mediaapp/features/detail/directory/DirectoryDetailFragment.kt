package com.example.mediaapp.features.detail.directory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDirectoryDetailBinding
import com.example.mediaapp.features.adapters.DirectoryAndFileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.*
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.util.FileUtil
import com.example.mediaapp.util.RealPathFileUtil
import java.util.*

class DirectoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentDirectoryDetailBinding
    private lateinit var directoryDetailAdapter: DirectoryAndFileAdapter
    private lateinit var mActivityResult: ActivityResultLauncher<Intent>
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private var parentId: String? = null
    private var childId: String? = null
    private var name: String? = null
    private var level = 0
    private var rootType: Int = 0
    private var isScrolling = false
    private val viewModel: DirectoryDetailViewModel by viewModels {
        DirectoryDetailViewModelFactory((activity?.application as MediaApplication).repository)
    }
    private val mySpaceViewModel: MySpaceViewModel by activityViewModels {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDirectoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isPause) {
            viewModel.isShowLoading = false
            viewModel.isPause = false
        }
        setUpSpinner()
        setUpRecyclerView()
        sucribeToObservers()
        setUpLoadMoreInRecyclerView()
        mActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                val intentData = result.data
                if (intentData != null) {
                    val uri = intentData.data
                    uri?.let {
                        val path = RealPathFileUtil.getRealPathFromURI(requireContext(), it)
                        path?.let { p ->
                            loadingDialogFragment.show(
                                parentFragmentManager,
                                Constants.LOADING_DIALOG_TAG
                            )
                            viewModel.uploadFile(if (childId == null) parentId!! else childId!!, p)
                        }
                    }
                }
            }
        )

        binding.imageViewBackDirectoryDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageViewMoreOptions.setOnClickListener {
            showBottomSheetOption(null)
        }
    }

    private fun openFile() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResult.launch(Intent.createChooser(intent, "Choose your file"))
    }

    private fun setUpSpinner() {
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.spinner_data)
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOption.adapter = spinnerAdapter
        binding.spinnerOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        if (viewModel.isShowLoading) {
                            binding.prbLoad2.visibility = View.VISIBLE
                        }
                        if (viewModel.currentPageFolder > 0) {
                            getDirectoryData(false, true)
                        } else {
                            getDirectoryData(true, true)
                        }
                        if (!viewModel.isShowLoading) {
                            viewModel.isShowLoading = true
                        }
                    }
                    1 -> {
                        if (viewModel.currentPageFile > 0) {
                            getDirectoryData(false, false)
                        } else {
                            getDirectoryData(false, false)
                        }
                        if (!viewModel.isShowLoading) {
                            viewModel.isShowLoading = true
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setUpLoadMoreInRecyclerView() {
        binding.rcvDirectoryDetail.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && isScrolling) {
                    when (binding.spinnerOption.selectedItemPosition) {
                        0 -> {
                            if ((viewModel.isHaveMoreFolders.value == true || viewModel.isHaveMoreFolders.value == null) && isScrolling) {
                                binding.prbLoad.visibility = View.VISIBLE
                            }
                            viewModel.loadMore(
                                parentId!!,
                                viewModel.currentPageFolder + 1,
                                Constants.DIRECTORY_TYPE,
                                rootType
                            )
                        }
                        1 -> {
                            if ((viewModel.isHaveMoreFiles.value == true || viewModel.isHaveMoreFiles.value == null) && isScrolling) {
                                binding.prbLoad.visibility = View.VISIBLE
                            }
                            viewModel.loadMore(
                                parentId!!,
                                viewModel.currentPageFile + 1,
                                Constants.FILE_TYPE,
                                rootType
                            )
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sucribeToObservers() {
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
        viewModel.isHaveMoreFiles.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.prbLoad.visibility = View.GONE
                viewModel.currentPageFile++
            } else {
                binding.prbLoad.visibility = View.GONE
                binding.rcvDirectoryDetail.setPadding(0, 0, 0, 0)
            }
        })
        viewModel.isHaveMoreFolders.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.prbLoad.visibility = View.GONE
                viewModel.currentPageFolder++
            } else {
                binding.prbLoad.visibility = View.GONE
                binding.rcvDirectoryDetail.setPadding(0, 0, 0, 0)
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if (parentId == null) {
                findNavController().popBackStack()
            } else {
                viewModel.getFoldersAndFilesByParentFolder(
                    parentId!!, false,
                    binding.spinnerOption.selectedItemPosition == 0, rootType
                )
            }
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.foldersAndFiles.observe(viewLifecycleOwner, Observer {
            binding.prbLoad2.visibility = View.GONE
            directoryDetailAdapter.submitList(it)
        })
    }

    private fun getDirectoryData(isFirstTimeLoad: Boolean, isGetListDirectory: Boolean) {
        val bundle = arguments
        bundle?.let {
            parentId = it.getString(Constants.DIRECTORY_ID)
            rootType = it.getInt(Constants.ROOT_TYPE)
            if (rootType == Constants.MY_SHARE) {
                binding.imageViewMoreOptions.visibility = View.GONE
            } else {
                binding.imageViewMoreOptions.visibility = View.VISIBLE
            }
            if (isFirstTimeLoad) {
                if (isGetListDirectory) {
                    viewModel.getFoldersAndFilesByParentFolder(parentId!!, true, true, rootType)
                } else {
                    viewModel.getFoldersAndFilesByParentFolder(
                        parentId!!,
                        true,
                        false,
                        rootType
                    )
                }
            } else {
                if (isGetListDirectory) {
                    viewModel.getFoldersAndFilesByParentFolder(parentId!!, false, true, rootType)
                } else {
                    viewModel.getFoldersAndFilesByParentFolder(
                        parentId!!,
                        false,
                        false,
                        rootType
                    )
                }
            }
            if (!viewModel.isPause) {
                name = it.getString(Constants.DIRECTORY_NAME)
            }
            binding.textViewTitleDirectoryDetail.text = name
            level = it.getInt(Constants.DIRECTORY_LEVEL)
        }
    }

    private fun showBottomSheetOption(any: Any?) {
        BottomSheetOptionFragment(any is Directory || any == null, rootType, true).apply {
            if ((any != null && any is Directory)) {
                setTitleName(any.name)
            } else if (any != null && any is File) {
                setTitleName(any.name)
            } else {
                setTitleName(binding.textViewTitleDirectoryDetail.text.toString())
            }
            if (any is Directory || any == null) {
                setClickCreateNewFolder {
                    showDialogCreateDirectory(any, "Create")
                    closeBottomSheet()
                }
                setClickCreateNewFile {
                    childId = if (any is Directory) any.id?.toString() else null
                    Log.d("level", if (childId == null) "null" else childId!!)
                    Constants.clickRequestPermissionToAccessFile(requireActivity()) { openFile() }
                    closeBottomSheet()
                }
            }
            setClickShare {
                showDialogSearchAccount(any)
                closeBottomSheet()
            }
            setClickAddToFavorite {
                addDirectoryToFavorite(any)
                closeBottomSheet()
            }
            setClickEdit {
                if (any is Directory || any == null) {
                    showDialogCreateDirectory(any, "Rename")
                    closeBottomSheet()
                }
            }
            setClickDelete {
                showDialogWarning(any)
                closeBottomSheet()
            }
            if (any is File) {
                setClickDownload {
                    (activity as HomeActivity).getFile(any)
                    closeBottomSheet()
                }
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }

    private fun showDialogSearchAccount(item: Any?) {
        ShareFolderOrFileDialogFragment()
            .apply {
                setClickToShare { email ->
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    viewModel.addDirectoryOrFileToShare(item, email, email, parentId)
                    closeDialog()
                }
            }
            .show(parentFragmentManager, Constants.SHARE_DIALOG_TAG)
    }

    private fun addDirectoryToFavorite(any: Any?) {
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        viewModel.addDirectoryOrFileToFavorite(any, parentId)
    }

    private fun showDialogWarning(item: Any?) {
        WarningDialogFragment(
            "Are you sure ?",
            "Do you want to delete ${if (item is File) "file" else "directory"}"
        ).apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                when (rootType) {
                    Constants.MY_SPACE -> {
                        viewModel.deleteDirectoryOrFile(item, parentId)
                        if (item == null) {
                            mySpaceViewModel.updateDirectoriesAfterEdit(parentId!!, "", level, true)
                            parentId = null
                        }
                    }
                }
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }

    private fun showDialogCreateDirectory(any: Any?, nameYesButton: String) {
        CreateDirectoryDialogFragment(false, nameYesButton).apply {
            if (nameYesButton == "Rename" && any is Directory) {
                setOldNameToEditText(any.name)
            } else if (nameYesButton == "Rename" && any == null) {
                setOldNameToEditText(binding.textViewTitleDirectoryDetail.text.toString())
            }
            setClickCreateWithoutRadioValue { value ->
                if (value.isNotEmpty()) {
                    if (nameYesButton == "Create") {
                        clickCreateDirectory(value, any)
                    } else if (nameYesButton == "Rename") {
                        viewModel.editDirectory(any, value, parentId)
                        if (any == null) {
                            mySpaceViewModel.updateDirectoriesAfterEdit(
                                parentId!!,
                                value,
                                level,
                                false
                            )
                            name = value
                            binding.textViewTitleDirectoryDetail.text = name
                        }
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

    private fun clickCreateDirectory(value: String, any: Any?) {
        if (any != null && any is Directory) {
            viewModel.createDirectory(Directory(value, level, any.id!!))
        } else {
            viewModel.createDirectory(Directory(value, level, UUID.fromString(parentId)))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerView() {
        val rootType2 = arguments?.getInt(Constants.ROOT_TYPE)
        directoryDetailAdapter =
            DirectoryAndFileAdapter(object : DirectoryAndFileAdapter.ClickItemDirectoryAndFile {
                override fun clickItem(item: Any?, isHaveOptions: Boolean) {
                    if (!isHaveOptions) {
                        when (item) {
                            is Directory -> {
                                val bundle = Bundle()
                                bundle.putString(Constants.DIRECTORY_ID, item.id.toString())
                                bundle.putString(Constants.DIRECTORY_NAME, item.name)
                                bundle.putInt(Constants.DIRECTORY_LEVEL, item.level)
                                bundle.putInt(Constants.ROOT_TYPE, rootType)
                                when (rootType) {
                                    Constants.MY_SPACE -> findNavController().navigate(
                                        R.id.action_directoryDetailFragment_self,
                                        bundle
                                    )
                                    Constants.SHARE_WITH_ME -> findNavController().navigate(
                                        R.id.action_directoryDetailFragment2_self,
                                        bundle
                                    )
                                    Constants.MY_SHARE -> findNavController().navigate(
                                        R.id.action_directoryDetailFragment3_self,
                                        bundle
                                    )
                                    Constants.FAVORITE -> findNavController().navigate(
                                        R.id.action_directoryDetailFragment4_self,
                                        bundle
                                    )
                                }
                            }
                            is File -> {
                                viewModel.isPause = true
                                onClickFile(item)
                            }
                        }
                    } else {
                        showBottomSheetOption(item)
                    }
                }
            }, rootType2!!)
        binding.rcvDirectoryDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvDirectoryDetail.adapter = directoryDetailAdapter
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

        val uri = FileUtil.toUri(requireContext(), file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }


    private fun onClickFile(file: File) {
        when (level) {
            1 -> viewModel.getFile(file.id.toString())
            2 -> {
                val bundle = Bundle()
                bundle.putString(Constants.FILE_DETAIL, file.id.toString())
                when (rootType) {
                    Constants.MY_SPACE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment_to_musicDetailFragment,
                        bundle
                    )
                    Constants.SHARE_WITH_ME -> findNavController().navigate(
                        R.id.action_directoryDetailFragment2_to_musicDetailFragment,
                        bundle
                    )
                    Constants.FAVORITE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment4_to_musicDetailFragment,
                        bundle
                    )
                    Constants.MY_SHARE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment3_to_musicDetailFragment2,
                        bundle
                    )
                }
            }
            3 -> {
                val bundle = Bundle()
                bundle.putString(Constants.FILE_DETAIL, file.id.toString())
                when (rootType) {
                    Constants.MY_SPACE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment_to_imageDetailFragment,
                        bundle
                    )
                    Constants.SHARE_WITH_ME -> findNavController().navigate(
                        R.id.action_directoryDetailFragment2_to_imageDetailFragment,
                        bundle
                    )
                    Constants.FAVORITE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment4_to_imageDetailFragment,
                        bundle
                    )
                    Constants.MY_SHARE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment3_to_imageDetailFragment2,
                        bundle
                    )
                }
            }
            4 -> {
                val bundle = Bundle()
                bundle.putString(Constants.FILE_DETAIL, file.id.toString())
                when (rootType) {
                    Constants.MY_SPACE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment_to_videoDetailFragment,
                        bundle
                    )
                    Constants.SHARE_WITH_ME -> findNavController().navigate(
                        R.id.action_directoryDetailFragment2_to_videoDetailFragment,
                        bundle
                    )
                    Constants.FAVORITE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment4_to_videoDetailFragment,
                        bundle
                    )
                    Constants.MY_SHARE -> findNavController().navigate(
                        R.id.action_directoryDetailFragment3_to_videoDetailFragment2,
                        bundle
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearToast()
        viewModel.isPause = true
        viewModel.isShowLoading = false
    }
}
