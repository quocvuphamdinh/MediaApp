package com.example.mediaapp.features.detail.directory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.CreateDirectoryDialogFragment
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.MediaApplication
import java.util.*

class DirectoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentDirectoryDetailBinding
    private lateinit var directoryDetailAdapter: DirectoryAdapter
    private val loadingDialogFragment: LoadingDialogFragment by lazy { LoadingDialogFragment() }
    private var parentId: String? =null
    private var level = 0
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
        if(viewModel.isPause){
            getDirectoryData(false)
        }else{
            getDirectoryData(true)
        }
        sucribeToObservers()
        setUpLoadMoreInRecyclerView()

        binding.imageViewBackDirectoryDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageViewMoreOptions.setOnClickListener {
            showBottomSheetOption(null)
        }
    }

    private fun setUpLoadMoreInRecyclerView() {
        binding.rcvDirectoryDetail.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    if(viewModel.isHaveMore.value==true || viewModel.isHaveMore.value==null){
                        binding.prbLoad.visibility = View.VISIBLE
                        recyclerView.scrollToPosition(viewModel.folders.value!!.size - 1)
                    }
                    viewModel.loadMore(viewModel.currentPage+1, parentId!!)
                }
            }
        })
    }

    private fun sucribeToObservers() {
        viewModel.isHaveMore.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.prbLoad.visibility = View.GONE
                viewModel.currentPage++
            }else{
                binding.prbLoad.visibility = View.GONE
                binding.rcvDirectoryDetail.setPadding(0,0,0,0)
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
            viewModel.getFoldersByParentFolder(parentId!!, false)
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.folders.observe(viewLifecycleOwner, Observer {
            directoryDetailAdapter.submitList(it)
        })
    }

    private fun getDirectoryData(isFirstTimeLoad: Boolean) {
        val bundle = arguments
        bundle?.let {
            parentId = it.getString(Constants.DIRECTORY_ID)
            if(isFirstTimeLoad){
                viewModel.getFoldersByParentFolder(parentId!!, true)
            }else{
                viewModel.getFoldersByParentFolder(parentId!!, false)
            }
            val name = it.getString(Constants.DIRECTORY_NAME)
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
                        if(childDirectory==null) binding.textViewTitleDirectoryDetail.text = value
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
        directoryDetailAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?, isHaveOptions: Boolean) {
                if(!isHaveOptions){
                    val bundle = Bundle()
                    bundle.putString(Constants.DIRECTORY_ID, directory!!.id.toString())
                    bundle.putString(Constants.DIRECTORY_NAME, directory.name)
                    bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
                    findNavController().navigate(R.id.action_directoryDetailFragment_self, bundle)
                }else{
                    showBottomSheetOption(directory)
                }
            }
        }, R.layout.directory_item_row, true)
        binding.rcvDirectoryDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvDirectoryDetail.adapter = directoryDetailAdapter
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearToast()
        viewModel.isPause = true
    }
}