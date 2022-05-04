package com.example.mediaapp.features.myspace.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentMusicMyPlaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.util.DataStore

class MusicFragment : Fragment() {
    private lateinit var binding : FragmentMusicMyPlaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicMyPlaceBinding.inflate(inflater, container, false)
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
        binding.rcvMySpaceFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderMusic.adapter = folderAdapter
        binding.rcvMySpaceFolderMusic.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = MySpaceMusicAdapter()
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileMusic.adapter = fileAdapter
        binding.rcvMySpaceFileMusic.setHasFixedSize(true)
    }
}