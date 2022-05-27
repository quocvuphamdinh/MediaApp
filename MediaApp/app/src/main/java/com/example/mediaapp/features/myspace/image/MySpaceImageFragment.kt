package com.example.mediaapp.features.myspace.image

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
import com.example.mediaapp.databinding.FragmentImageMySpaceBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.DataStore
import com.example.mediaapp.util.MediaApplication

class MySpaceImageFragment : Fragment() {
    private lateinit var binding: FragmentImageMySpaceBinding
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
        binding = FragmentImageMySpaceBinding.inflate(inflater, container, false)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpLoadMoreInRecyclerView() {
        binding.rcvMySpaceFolderImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pagePhoto+1, 3, true)
                }
            }
        })
        binding.rcvMySpaceFileImage.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore(viewModel.pagePhotoFile+1, 3, false)
                }
            }
        })
    }

    private fun subcribeToObservers() {
        viewModel.isHaveMorePhotosFile.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pagePhotoFile++
            }else{
                binding.rcvMySpaceFileImage.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMorePhotos.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pagePhoto++
            }else{
                binding.rcvMySpaceFolderImage.setPadding(0,0,0,0)
            }
        })
        viewModel.folderPhotos.observe(viewLifecycleOwner, Observer {
            folderAdapter.submitList(it)
        })
        viewModel.filePhotos.observe(viewLifecycleOwner, Observer {
            fileAdapter.submitList(it)
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory {
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_imageDetailFragment)
            }
        })
        binding.rcvMySpaceFileImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileImage.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {
                val bundle = Bundle()
                bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
                bundle.putString(Constants.DIRECTORY_NAME, directory.name)
                bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
                bundle.putInt(Constants.ROOT_TYPE, Constants.MY_SPACE)
                findNavController().navigate(
                    R.id.action_mySpaceFragment_to_directoryDetailFragment,
                    bundle
                )
            }
        })
        binding.rcvMySpaceFolderImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderImage.adapter = folderAdapter
    }
}