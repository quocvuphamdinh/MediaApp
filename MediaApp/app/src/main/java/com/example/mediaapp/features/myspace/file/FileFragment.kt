package com.example.mediaapp.features.myspace.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentFileMyPlaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.util.DataStore

class FileFragment : Fragment() {
    private lateinit var binding : FragmentFileMyPlaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileMyPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = MySpaceMusicAdapter()
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderFile.adapter = folderAdapter
        binding.rcvMySpaceFolderFile.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = MySpaceMusicAdapter()
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileFile.adapter = fileAdapter
        binding.rcvMySpaceFileFile.setHasFixedSize(true)
    }
}