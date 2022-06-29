package com.example.mediaapp.features.myshare.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentImageMyShareBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.myshare.MyShareViewModel
import com.example.mediaapp.features.myshare.MyShareViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.base.home.HomeActivity

class MyShareImageFragment: Fragment() {
    private lateinit var binding: FragmentImageMyShareBinding
    private lateinit var directoryAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: MyShareViewModel by activityViewModels {
        MyShareViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageMyShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDirectoryAdapter()
        setUpFileAdapter()
        subcribeToObservers()
        setUpLoadMoreInRecyclerView()
    }
    private fun setUpLoadMoreInRecyclerView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFoldersAndFiles(3)
            (activity as HomeActivity).getAccountInfo()
        }
    }

    private fun subcribeToObservers() {
        viewModel.folderPhotos.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            directoryAdapter.submitList(it)
        })
        viewModel.filePhotos.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            fileAdapter.submitList(it)
        })
    }

    private fun showBottomSheetOption(any: Any) {
        BottomSheetOptionFragment(any is Directory, Constants.MY_SHARE).apply {
            when(any){
                is Directory -> setTitleName(any.name)
                is File -> setTitleName(any.name)
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(any, 1)
                closeBottomSheet()
            }
            setClickViewReceiver {
                viewModel.setDirectoryLongClick(any, 2)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }

    private fun setUpFileAdapter() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory{
            override fun clickItem(file: File) {
                val bundle = Bundle()
                bundle.putString(Constants.FILE_DETAIL, file.id.toString())
                findNavController().navigate(R.id.action_myShareFragment_to_imageDetailFragment2, bundle)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvMyShareFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMyShareFileFile.adapter = fileAdapter
    }

    private fun setUpDirectoryAdapter() {
        directoryAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                goToDirectoryDetail(directory)
            }

            override fun longClickItem(directory: Directory) {
                showBottomSheetOption(directory)
            }
        })
        binding.rcvMyShareFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMyShareFolderFile.adapter = directoryAdapter
    }
    private fun goToDirectoryDetail(directory: Directory){
        val bundle = Bundle()
        bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
        bundle.putString(Constants.DIRECTORY_NAME, directory.name)
        bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
        bundle.putInt(Constants.ROOT_TYPE, Constants.MY_SHARE)
        findNavController().navigate(
            R.id.action_myShareFragment_to_directoryDetailFragment3,
            bundle
        )
    }
}