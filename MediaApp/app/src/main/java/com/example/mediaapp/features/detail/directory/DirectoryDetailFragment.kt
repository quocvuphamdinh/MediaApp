package com.example.mediaapp.features.detail.directory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDirectoryDetailBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.DirectoryAndFileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.CreateDirectoryDialogFragment
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.features.util.SearchAccountDialogFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.MediaApplication
import java.util.*

class DirectoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentDirectoryDetailBinding
    private lateinit var directoryDetailAdapter: DirectoryAndFileAdapter
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private var parentId: String? =null
    private var name: String? = null
    private var level = 0
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

        binding.imageViewBackDirectoryDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageViewMoreOptions.setOnClickListener {
            showBottomSheetOption(null)
        }
    }

    private fun setUpSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinner_data))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOption.adapter = spinnerAdapter
        binding.spinnerOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        Log.d("hi", viewModel.isShowLoading.toString())
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
            viewModel.getFoldersAndFilesByParentFolder(parentId!!, false, true)
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
        }
    }

    private fun showBottomSheetOption(directory: Directory?){
        BottomSheetOptionFragment().apply {
            if(directory!=null){
                setTitleName(directory!!.name)
            }else{
                setTitleName(binding.textViewTitleDirectoryDetail.text.toString())
            }
            setClickCreateNewFolder {
                showDialogCreateDirectory(directory, "Create")
                closeBottomSheet()
            }
            setClickCreateNewFile {
                closeBottomSheet()
            }
            setClickShare {
                showDialogSearchAccount(directory)
                closeBottomSheet()
            }
            setClickAddToFavorite {
                addDirectoryToFavorite(directory)
                closeBottomSheet()
            }
            setClickEdit {
                showDialogCreateDirectory(directory, "Rename")
                closeBottomSheet()
            }
            setClickDelete {
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }
    private fun showDialogSearchAccount(directory: Directory?){
        SearchAccountDialogFragment()
            .apply {
                setTextChangeListener { keyword ->
                    viewModel.getAccountsByKeyword(keyword, accounts)
                }
                setClickAccountItem { user ->
                    loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    viewModel.addDirectoryToShare(directory?.id?.toString() ?: parentId!!,
                        user.id.toString(), "${user.firstName} ${user.lastName}")
                }
            }
            .show(parentFragmentManager, Constants.SEARCH_DIALOG_ACCOUNT_TAG)
    }

    private fun addDirectoryToFavorite(directory: Directory?){
        loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
        if(directory==null){
            viewModel.addDirectoryToFavorite(parentId!!)
        }else{
            viewModel.addDirectoryToFavorite(directory.id.toString())
        }
    }

    private fun showDialogCreateDirectory(childDirectory: Directory?, nameYesButton: String){
        CreateDirectoryDialogFragment(false, nameYesButton).apply {
            if(nameYesButton=="Rename"){
                setOldNameToEditText(childDirectory?.name ?: binding.textViewTitleDirectoryDetail.text.toString())
            }
            setClickCreateWithoutRadioValue { value ->
                if(value.isNotEmpty()){
                    if(nameYesButton=="Create"){
                        clickCreateDirectory(value, childDirectory)
                    }else{
                        viewModel.editDirectory(childDirectory?.id?.toString() ?: parentId!!, value)
                        mySpaceViewModel.updateDirectoriesAfterEdit(childDirectory?.id?.toString() ?: parentId!!, value, level)
                        if(childDirectory==null) {
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
                            val bundle = Bundle()
                            bundle.putString(Constants.DIRECTORY_ID, item.id.toString())
                            bundle.putString(Constants.DIRECTORY_NAME, item.name)
                            bundle.putInt(Constants.DIRECTORY_LEVEL, item.level)
                            findNavController().navigate(R.id.action_directoryDetailFragment_self, bundle)
                        }
                        is File -> {
                            Toast.makeText(requireContext(), item.accountId.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    when(item){
                        is Directory ->showBottomSheetOption(item)
                    }
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