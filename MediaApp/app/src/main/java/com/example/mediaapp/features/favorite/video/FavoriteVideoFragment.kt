package com.example.mediaapp.features.favorite.video

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
import com.example.mediaapp.databinding.FragmentVideoFavoriteBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.favorite.FavoriteViewModel
import com.example.mediaapp.features.favorite.FavoriteViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.DataStore

class FavoriteVideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoFavoriteBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : FileAdapter
    private val viewModel: FavoriteViewModel by activityViewModels {
        FavoriteViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoFavoriteBinding.inflate(inflater, container, false)
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
        BottomSheetOptionFragment(any is Directory, Constants.FAVORITE).apply {
            when(any){
                is Directory -> setTitleName(any.name)
                is File -> setTitleName(any.name)
            }
            setClickShare {
                viewModel.setDirectoryLongClick(any, 1)
                closeBottomSheet()
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(any, 2)
                closeBottomSheet()
            }
            setClickDownload {
                viewModel.setDirectoryLongClick(any, 3)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpLoadMoreInRecyclerView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFoldersAndFiles(4)
        }
        binding.rcvFavoriteFolderVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMovie+1, 4, true)
                }
            }
        })
        binding.rcvFavoriteFileVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                binding.rcvFavoriteFileVideo.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreMovies.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMovie++
            }else{
                binding.rcvFavoriteFolderVideo.setPadding(0,0,0,0)
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
                findNavController().navigate(R.id.action_favoriteFragment_to_videoDetailFragment)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvFavoriteFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileVideo.adapter = fileAdapter
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
        binding.rcvFavoriteFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderVideo.adapter = folderAdapter
    }
    private fun goToDirectoryDetail(directory: Directory){
        val bundle = Bundle()
        bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
        bundle.putString(Constants.DIRECTORY_NAME, directory.name)
        bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
        bundle.putInt(Constants.ROOT_TYPE, Constants.FAVORITE)
        findNavController().navigate(R.id.action_favoriteFragment_to_directoryDetailFragment4, bundle)
    }
}