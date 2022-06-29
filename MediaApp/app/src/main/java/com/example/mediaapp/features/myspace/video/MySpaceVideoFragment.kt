package com.example.mediaapp.features.myspace.video

import android.os.Build
import android.os.Bundle
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
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentVideoMySpaceBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.base.home.HomeActivity

class MySpaceVideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoMySpaceBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : FileAdapter
    private val viewModel: MySpaceViewModel by activityViewModels  {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFile()
        setUpRecyclerViewFolder()
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
            viewModel.refreshFoldersAndFiles(4)
            (activity as HomeActivity).getAccountInfo()
        }
        binding.rcvMySpaceFolderVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMovie+1, 4, true)
                }
            }
        })
        binding.rcvMySpaceFileVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMovieFile+1, 4, false)
                }
            }
        })
    }

    private fun subcribeToObservers() {
        viewModel.isHaveMoreMoviesFile.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMovieFile++
            }else{
                binding.rcvMySpaceFileVideo.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreMovies.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMovie++
            }else{
                binding.rcvMySpaceFolderVideo.setPadding(0,0,0,0)
            }
        })
        viewModel.folderMovies.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            folderAdapter.submitList(it)
        })
        viewModel.fileMovies.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            fileAdapter.submitList(it)
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory{
            override fun clickItem(file: File) {
                val bundle = Bundle()
                bundle.putString(Constants.FILE_DETAIL, file.id.toString())
                findNavController().navigate(R.id.action_mySpaceFragment_to_videoDetailFragment, bundle)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvMySpaceFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileVideo.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                goToDirectoryDetail(directory)
            }

            override fun longClickItem(directory: Directory) {
                showBottomSheetOption(directory)
            }
        })
        binding.rcvMySpaceFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderVideo.adapter = folderAdapter
    }
    private fun goToDirectoryDetail(directory: Directory){
        val bundle = Bundle()
        bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
        bundle.putString(Constants.DIRECTORY_NAME, directory.name)
        bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
        bundle.putInt(Constants.ROOT_TYPE, Constants.MY_SPACE)
        findNavController().navigate(R.id.action_mySpaceFragment_to_directoryDetailFragment, bundle)
    }
}