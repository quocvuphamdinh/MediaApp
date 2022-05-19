package com.example.mediaapp.features.deleted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.features.base.home.HomeActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDeletedBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class DeleteFragment : Fragment() {
    private lateinit var binding : FragmentDeletedBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.toolbarMain.title = "Deleted"

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }
    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?, isHaveOptions: Boolean) {
                findNavController().navigate(R.id.action_deleteFragment_to_fileDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvDeletedFolderFile.layoutManager=GridLayoutManager(requireContext(), 2)
        binding.rcvDeletedFolderFile.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?, isHaveOptions: Boolean) {
                findNavController().navigate(R.id.action_deleteFragment_to_imageDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvDeletedFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvDeletedFileFile.adapter = fileAdapter
    }
}