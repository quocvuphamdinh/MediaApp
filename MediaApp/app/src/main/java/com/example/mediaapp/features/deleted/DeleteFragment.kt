package com.example.mediaapp.features.deleted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.MainActivity
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDeletedBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class DeleteFragment : Fragment() {
    private lateinit var binding : FragmentDeletedBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

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

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }
    private fun setUpRecyclerViewFolder() {
        folderAdapter = MySpaceMusicAdapter(object : MySpaceMusicAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                findNavController().navigate(R.id.action_deleteFragment_to_fileDetailFragment)
            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvDeletedFolderFile.layoutManager=GridLayoutManager(requireContext(), 2)
        binding.rcvDeletedFolderFile.adapter = folderAdapter
        binding.rcvDeletedFolderFile.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = MySpaceMusicAdapter(object : MySpaceMusicAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                findNavController().navigate(R.id.action_deleteFragment_to_imageDetailFragment)
            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvDeletedFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvDeletedFileFile.adapter = fileAdapter
        binding.rcvDeletedFileFile.setHasFixedSize(true)
    }
}