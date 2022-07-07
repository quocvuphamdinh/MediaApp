package com.example.mediaapp.features.favorite.music

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
import com.example.mediaapp.databinding.FragmentMusicFavoriteBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.features.favorite.FavoriteViewModel
import com.example.mediaapp.features.favorite.FavoriteViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants

class FavoriteMusicFragment : Fragment() {
    private lateinit var binding : FragmentMusicFavoriteBinding
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
        binding = FragmentMusicFavoriteBinding.inflate(inflater, container, false)
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
            viewModel.refreshFoldersAndFiles(2)
            (activity as HomeActivity).getAccountInfo()
        }
        binding.rcvFavoriteFolderMusic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMusic+1, 2, true)
                }
            }
        })
        binding.rcvFavoriteFileMusic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMusicFile+1, 2, false)
                }
            }
        })
    }
    private fun subcribeToObservers() {
        viewModel.isHaveMoreMusicsFile.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMusicFile++
            }else{
                binding.rcvFavoriteFileMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreMusics.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMusic++
            }else{
                binding.rcvFavoriteFolderMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.folderMusics.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            folderAdapter.submitList(it)
        })
        viewModel.fileMusics.observe(viewLifecycleOwner, Observer {
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
                findNavController().navigate(R.id.action_favoriteFragment_to_musicDetailFragment, bundle)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvFavoriteFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileMusic.adapter = fileAdapter
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
        binding.rcvFavoriteFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderMusic.adapter = folderAdapter
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