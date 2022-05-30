package com.example.mediaapp.features.myspace.music

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
import com.example.mediaapp.databinding.FragmentMusicMySpaceBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.DataStore
import com.example.mediaapp.util.MediaApplication

class MySpaceMusicFragment : Fragment() {
    private lateinit var binding: FragmentMusicMySpaceBinding
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
        binding = FragmentMusicMySpaceBinding.inflate(inflater, container, false)
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
    private fun showBottomSheetOption(directory: Directory){
        BottomSheetOptionFragment(true, Constants.MY_SPACE).apply {
            setTitleName(directory.name)
            setClickCreateNewFolder {
                viewModel.setDirectoryLongClick(directory, 1)
                closeBottomSheet()
            }
            setClickCreateNewFile {
                viewModel.setDirectoryLongClick(directory, 2)
                closeBottomSheet()
            }
            setClickShare {
                viewModel.setDirectoryLongClick(directory, 3)
                closeBottomSheet()
            }
            setClickAddToFavorite {
                viewModel.setDirectoryLongClick(directory, 4)
                closeBottomSheet()
            }
            setClickEdit {
                viewModel.setDirectoryLongClick(directory, 5)
                closeBottomSheet()
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(directory, 6)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpLoadMoreInRecyclerView() {
        binding.rcvMySpaceFolderMusic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pageMusic+1, 2, true)
                }
            }
        })
        binding.rcvMySpaceFileMusic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                binding.rcvMySpaceFileMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreMusics.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMusic++
            }else{
                binding.rcvMySpaceFolderMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.folderMusics.observe(viewLifecycleOwner, Observer {
            folderAdapter.submitList(it)
        })
        viewModel.fileMusics.observe(viewLifecycleOwner, Observer {
            fileAdapter.submitList(it)
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory {
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_musicDetailFragment)
            }
        })
        binding.rcvMySpaceFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileMusic.adapter = fileAdapter
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

            override fun longClickItem(directory: Directory) {
                showBottomSheetOption(directory)
            }
        })
        binding.rcvMySpaceFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderMusic.adapter = folderAdapter
    }
}