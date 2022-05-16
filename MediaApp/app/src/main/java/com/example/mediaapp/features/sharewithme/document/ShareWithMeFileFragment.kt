package com.example.mediaapp.features.sharewithme.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFileShareWithMeBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class ShareWithMeFileFragment : Fragment() {
    private lateinit var binding: FragmentFileShareWithMeBinding
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory?) {
                findNavController().navigate(R.id.action_shareWithMeFragment_to_fileDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFileFile.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory?) {

            }
        }, R.layout.my_space_music_item_row, false)
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFolderFile.adapter = fileAdapter
    }
}