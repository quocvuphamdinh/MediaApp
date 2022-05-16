package com.example.mediaapp.features.myspace.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentImageMySpaceBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.create_directory.CreateDirectoryDialogFragment
import com.example.mediaapp.features.myspace.MySpaceViewModel
import com.example.mediaapp.features.myspace.MySpaceViewModelFactory
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.DataStore
import com.example.mediaapp.util.LoadingDialogFragment
import com.example.mediaapp.util.MediaApplication
import java.util.*

class MySpaceImageFragment : Fragment() {
    private lateinit var binding: FragmentImageMySpaceBinding
    private lateinit var loadingDialogFragment: LoadingDialogFragment
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: DirectoryAdapter
    private var parentId:String?=null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialogFragment = LoadingDialogFragment()

        if(savedInstanceState!=null){
            val dialogCreateDirectory = parentFragmentManager.findFragmentByTag(Constants.CREATE_DIRECTORY_DIALOG_TAG) as CreateDirectoryDialogFragment?
            dialogCreateDirectory?.setClickCreate { value ->
                viewModel.createDirectory(Directory(value, 3, UUID.fromString(parentId)))
                dialogCreateDirectory.cancelDialog()
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            }
        }

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
        subcribeToObservers()
    }

    private fun subcribeToObservers() {
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
        })
        viewModel.folderPhotos.observe(viewLifecycleOwner, Observer {
            folderAdapter.submitList(it)
        })
        viewModel.folderRoots.observe(viewLifecycleOwner, Observer {
            parentId = it[2].id.toString()
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory?) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_imageDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        binding.rcvMySpaceFileImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileImage.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory?) {
                if(directory==null){
                    showDialogCreateDirectory()
                }else{
                    findNavController().navigate(R.id.action_mySpaceFragment_to_directoryDetailFragment)
                }
            }
        }, R.layout.my_space_music_item_row, false)
        binding.rcvMySpaceFolderImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderImage.adapter = folderAdapter
    }

    private fun showDialogCreateDirectory(){
        CreateDirectoryDialogFragment().apply {
            setClickCreate { value ->
                viewModel.createDirectory(Directory(value, 3, UUID.fromString(parentId)))
                cancelDialog()
                loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            }
        }.show(parentFragmentManager, Constants.CREATE_DIRECTORY_DIALOG_TAG)
    }
}