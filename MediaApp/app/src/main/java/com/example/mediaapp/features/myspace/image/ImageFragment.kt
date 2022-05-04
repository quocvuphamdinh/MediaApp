package com.example.mediaapp.features.myspace.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentImageMyPlaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.util.DataStore

class ImageFragment : Fragment() {
    private lateinit var binding : FragmentImageMyPlaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageMyPlaceBinding.inflate(inflater, container, false)
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
        binding.rcvMySpaceFolderImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderImage.adapter = folderAdapter
        binding.rcvMySpaceFolderImage.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = MySpaceMusicAdapter()
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileImage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileImage.adapter = fileAdapter
        binding.rcvMySpaceFileImage.setHasFixedSize(true)
    }
}