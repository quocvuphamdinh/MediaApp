package com.example.mediaapp.features.myspace.document

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFileMySpaceBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.MediaApplication

class MySpaceFileFragment : Fragment() {
    private lateinit var binding: FragmentFileMySpaceBinding
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: MySpaceViewModel by activityViewModels  {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
        subcribeToObservers()
        setUpLoadMoreInRecyclerView()
    }
    private fun showBottomSheetOption(any: Any){
        BottomSheetOptionFragment(any is Directory, Constants.MY_SPACE).apply {
            when(any){
                is Directory -> setTitleName(any.name)
                is File -> setTitleName(any.name)
            }
            setClickCreateNewFolder {
                viewModel.setDirectoryLongClick(any, 1)
                closeBottomSheet()
            }
            setClickCreateNewFile {
                viewModel.setDirectoryLongClick(any, 2)
                closeBottomSheet()
            }
            setClickShare {
                viewModel.setDirectoryLongClick(any, 3)
                closeBottomSheet()
            }
            setClickAddToFavorite {
                viewModel.setDirectoryLongClick(any, 4)
                closeBottomSheet()
            }
            setClickEdit {
                viewModel.setDirectoryLongClick(any, 5)
                closeBottomSheet()
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(any, 6)
                closeBottomSheet()
            }
            setClickDownload {
                viewModel.setDirectoryLongClick(any, 7)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpLoadMoreInRecyclerView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFoldersAndFiles(1)
        }
        binding.rcvMySpaceFolderFile.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageDocument+1, 1, true)
                }
            }
        })
        binding.rcvMySpaceFileFile.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){
                    viewModel.loadMore(viewModel.pageDocumentFile+1, 1, false)
                }
            }
        })
    }

    private fun subcribeToObservers() {
        viewModel.isHaveMoreDocumentsFile.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageDocumentFile++
            }else{
                binding.rcvMySpaceFileFile.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreDocuments.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageDocument++
            }else{
                binding.rcvMySpaceFolderFile.setPadding(0,0,0,0)
            }
        })
        viewModel.folderDocuments.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            folderAdapter.submitList(it)
        })
        viewModel.fileDocuments.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            fileAdapter.submitList(it)
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory {
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_fileDetailFragment)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvMySpaceFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileFile.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {
                val bundle = Bundle()
                bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
                bundle.putString(Constants.DIRECTORY_NAME, directory.name)
                bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
                bundle.putInt(Constants.ROOT_TYPE, Constants.MY_SPACE)
                findNavController().navigate(R.id.action_mySpaceFragment_to_directoryDetailFragment, bundle)
            }

            override fun longClickItem(directory: Directory){
                showBottomSheetOption(directory)
            }
        })
        binding.rcvMySpaceFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderFile.adapter = folderAdapter
    }
}