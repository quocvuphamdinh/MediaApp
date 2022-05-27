package com.example.mediaapp.features.detail.directory

import android.content.Intent
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
import com.example.mediaapp.util.MediaApplication
import com.example.mediaapp.util.RealPathFileUtil
import java.util.*

class DirectoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentDirectoryDetailBinding
    private lateinit var directoryDetailAdapter: DirectoryAndFileAdapter
    private lateinit var mActivityResult: ActivityResultLauncher<Intent>
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private var parentId: String? =null
    private var childId: String? = null
    private var name: String? = null
    private var level = 0
    private var rootType : Int = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setUpSpinner()
        sucribeToObservers()
        setUpLoadMoreInRecyclerView()
        mActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult>{ result ->
                val intentData = result.data
                if (intentData!=null){
                    val uri = intentData.data
                    uri?.let {
                        val path = RealPathFileUtil.getRealPathFromURI(requireContext(), it)
                        path?.let { p->
                            loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                            viewModel.uploadFile(if(childId==null) parentId!! else childId!! , level, p)
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
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinner_data))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOption.adapter = spinnerAdapter
        binding.spinnerOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        if(viewModel.isShowLoading){
                            binding.prbLoad2.visibility = View.VISIBLE
                        }
                        if(viewModel.currentPageFolder>0){
                            getDirectoryData(false)
                        }else{
                            getDirectoryData(true)
                        }
                        viewModel.isShowLoading = true
                    }
                    1 -> {
                        if(viewModel.isShowLoading){
                            binding.prbLoad2.visibility = View.VISIBLE
                        }
                        if(viewModel.currentPageFile>0){
                            viewModel.getFoldersAndFilesByParentFolder(parentId!!, false, false)
                        }else{
                            viewModel.getFoldersAndFilesByParentFolder(parentId!!, true, false)
                        }
                        viewModel.isShowLoading = true
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setUpLoadMoreInRecyclerView() {
        binding.rcvDirectoryDetail.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)&& isScrolling) {
                    when(binding.spinnerOption.selectedItemPosition){
                        0 ->{
                            if((viewModel.isHaveMoreFolders.value==true || viewModel.isHaveMoreFolders.value==null) && isScrolling){
                                binding.prbLoad.visibility = View.VISIBLE
                            }
                            viewModel.loadMore(parentId!!, viewModel.currentPageFolder+1, Constants.DIRECTORY_TYPE)
                        }
                        1->{
                            if((viewModel.isHaveMoreFiles.value==true || viewModel.isHaveMoreFiles.value==null) && isScrolling){
                                binding.prbLoad.visibility = View.VISIBLE
                            }
                            viewModel.loadMore(parentId!!, viewModel.currentPageFile+1, Constants.FILE_TYPE)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState== RecyclerView.SCROLL_STATE_DRAGGING){
                    isScrolling = true
                }else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    isScrolling = false
                }
            }
        })
    }

    private fun sucribeToObservers() {
        viewModel.isHaveMoreFiles.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.prbLoad.visibility = View.GONE
                viewModel.currentPageFile++
            }else{
                binding.prbLoad.visibility = View.GONE
                binding.rcvDirectoryDetail.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreFolders.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.prbLoad.visibility = View.GONE
                viewModel.currentPageFolder++
            }else{
                binding.prbLoad.visibility = View.GONE
                binding.rcvDirectoryDetail.setPadding(0,0,0,0)
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            if(parentId==null){
                findNavController().popBackStack()
            }else{
                viewModel.getFoldersAndFilesByParentFolder(parentId!!, false,
                    binding.spinnerOption.selectedItemPosition==0
                )
            }
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.foldersAndFiles.observe(viewLifecycleOwner, Observer {
            binding.prbLoad2.visibility = View.GONE
            directoryDetailAdapter.submitList(it)
        })
    }

    private fun getDirectoryData(isFirstTimeLoad: Boolean) {
        val bundle = arguments
        bundle?.let {
            parentId = it.getString(Constants.DIRECTORY_ID)
            if(isFirstTimeLoad){
                viewModel.getFoldersAndFilesByParentFolder(parentId!!, true, true)
            }else{
                viewModel.getFoldersAndFilesByParentFolder(parentId!!, false, true)
            }
            if(!viewModel.isPause){
                name = it.getString(Constants.DIRECTORY_NAME)
            }
            binding.textViewTitleDirectoryDetail.text = name
            level = it.getInt(Constants.DIRECTORY_LEVEL)
            rootType = it.getInt(Constants.ROOT_TYPE)
        }
    }

    private fun showBottomSheetOption(any: Any?){
        BottomSheetOptionFragment(any is Directory, rootType).apply {
            if((any!=null && any is Directory)){
                setTitleName(any.name)
            }else if(any!=null && any is File){
                setTitleName(any.name)
            } else{
                setTitleName(binding.textViewTitleDirectoryDetail.text.toString())
            }
            if(any is Directory){
                setClickCreateNewFolder {
                    showDialogCreateDirectory(any, "Create")
                    closeBottomSheet()
                }
                setClickCreateNewFile {
                    childId = any.id?.toString()
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
                showDialogCreateDirectory(any, "Rename")
                closeBottomSheet()
            }
            setClickDelete {
                showDialogWarning(any)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }
    private fun showDialogSearchAccount(item: Any?){
        SearchAccountDialogFragment()
            .apply {
                setTextChangeListener { keyword ->
                    viewModel.getAccountsByKeyword(keyword, accounts)
                }
                setClickAccountItem { user ->
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    when(item){
                        null -> viewModel.addDirectoryToShare(parentId!!, user.id.toString(), "${user.firstName} ${user.lastName}")
                        is Directory -> viewModel.addDirectoryToShare(item.id.toString(), user.id.toString(), "${user.firstName} ${user.lastName}")
                        is File -> {}
                    }
                }
            }
            .show(parentFragmentManager, Constants.SEARCH_DIALOG_ACCOUNT_TAG)
    }

    private fun addDirectoryToFavorite(any: Any?){
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        when(any){
            null -> viewModel.addDirectoryToFavorite(parentId!!)
            is Directory ->  viewModel.addDirectoryToFavorite(any.id.toString())
            is File -> {}
        }
    }

    private fun showDialogWarning(item: Any?){
        WarningDialogFragment("Are you sure ?", "Do you want to delete ${if (item is File) "file" else "directory"}").apply {
            setClickYes {
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                when(item){
                    null -> {
                        viewModel.deleteDirectory(parentId.toString())
                        mySpaceViewModel.updateDirectoriesAfterEdit(parentId!!, "", level, true)
                        parentId = null
                    }
                    is Directory -> viewModel.deleteDirectory(item.id.toString())
                    is File -> viewModel.deleteFile(item.id.toString())
                }
            }
        }.show(parentFragmentManager, Constants.WARNING_DIALOG)
    }
    private fun showDialogCreateDirectory(item: Any?, nameYesButton: String){
        CreateDirectoryDialogFragment(false, nameYesButton).apply {
            if(nameYesButton=="Rename"){
                when(item){
                    null -> setOldNameToEditText(binding.textViewTitleDirectoryDetail.text.toString())
                    is Directory -> setOldNameToEditText(item.name)
                    is File -> setOldNameToEditText(item.name)
                }
            }
            setClickCreateWithoutRadioValue { value ->
                if(value.isNotEmpty()){
                    if(nameYesButton=="Create" && item is Directory){
                        clickCreateDirectory(value, item)
                    }else{
                        when(item){
                            null ->  {
                                viewModel.editDirectory(parentId!!, value)
                                mySpaceViewModel.updateDirectoriesAfterEdit(parentId!!, value, level, false)
                            }
                            is Directory ->  viewModel.editDirectory(item.id.toString(), value)
                            is File -> {}
                        }
                        if(item==null) {
                            name = value
                            binding.textViewTitleDirectoryDetail.text = name
                        }
                    }
                    cancelDialog()
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                }else{
                    Toast.makeText(requireContext(), "Please enter your folder name !", Toast.LENGTH_SHORT).show()
                }
            }
        }.show(parentFragmentManager, Constants.CREATE_DIRECTORY_DIALOG_TAG)
    }

    private fun clickCreateDirectory(value:String, directory: Directory?){
        if(directory!=null){
            viewModel.createDirectory(Directory(value, level, directory.id!!))
        }else{
            viewModel.createDirectory(Directory(value, level, UUID.fromString(parentId)))
        }
    }

    private fun setUpRecyclerView() {
        directoryDetailAdapter = DirectoryAndFileAdapter(object : DirectoryAndFileAdapter.ClickItemDirectoryAndFile{
            override fun clickItem(item: Any?, isHaveOptions: Boolean) {
                if(!isHaveOptions){
                    when(item){
                        is Directory -> {
                            Log.d("id", item.id.toString())
                            val bundle = Bundle()
                            bundle.putString(Constants.DIRECTORY_ID, item.id.toString())
                            bundle.putString(Constants.DIRECTORY_NAME, item.name)
                            bundle.putInt(Constants.DIRECTORY_LEVEL, item.level)
                            bundle.putInt(Constants.ROOT_TYPE, rootType)
                            findNavController().navigate(R.id.action_directoryDetailFragment_self, bundle)
                        }
                        is File -> {
                            Toast.makeText(requireContext(), item.accountId.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    showBottomSheetOption(item)
                }
            }
        })
        binding.rcvDirectoryDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvDirectoryDetail.adapter = directoryDetailAdapter
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearToast()
        viewModel.isPause = true
        viewModel.isShowLoading = false
    }
}