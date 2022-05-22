package com.example.mediaapp.features.sharewithme.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentImageShareWithMeBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.DataStore

class ShareWithMeImageFragment : Fragment() {
    private lateinit var binding: FragmentImageShareWithMeBinding
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory {
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_shareWithMeFragment_to_imageDetailFragment)
            }
        })
        fileAdapter.submitList(DataStore.getListFile())
        binding.rcvShareWithMeFileImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFileImage.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFolderImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFolderImage.adapter = folderAdapter
    }
}