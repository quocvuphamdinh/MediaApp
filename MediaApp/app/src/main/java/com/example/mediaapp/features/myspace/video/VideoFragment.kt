package com.example.mediaapp.features.myspace.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentVideoMyPlaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.util.DataStore

class VideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoMyPlaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoMyPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFile()
        setUpRecyclerViewFolder()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = MySpaceMusicAdapter()
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderVideo.adapter = folderAdapter
        binding.rcvMySpaceFolderVideo.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = MySpaceMusicAdapter()
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileVideo.adapter = fileAdapter
        binding.rcvMySpaceFileVideo.setHasFixedSize(true)
    }
}