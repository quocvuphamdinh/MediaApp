package com.example.mediaapp.features.myshare.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentFileMyShareBinding
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

class MyShareFileFragment: Fragment() {
    private lateinit var binding: FragmentFileMyShareBinding
    private lateinit var directoryAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: MySpaceViewModel by activityViewModels {
        MySpaceViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileMyShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDirectoryAdapter()
        setUpFileAdapter()
    }

    private fun showBottomSheetOption(any: Any) {
        BottomSheetOptionFragment(any is Directory, Constants.MY_SHARE).apply {
            when(any){
                is Directory -> setTitleName(any.name)
                is File -> setTitleName(any.name)
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(any, 1)
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

            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        fileAdapter.submitList(DataStore.getListFile())
        binding.rcvMyShareFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMyShareFileFile.adapter = fileAdapter
    }

    private fun setUpDirectoryAdapter() {
        directoryAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }

            override fun longClickItem(directory: Directory) {
                showBottomSheetOption(directory)
            }
        })
        directoryAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMyShareFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMyShareFolderFile.adapter = directoryAdapter
    }
}