package com.example.mediaapp.features.myspace.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicMySpaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class MySpaceMusicFragment : Fragment() {
    private lateinit var binding : FragmentMusicMySpaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicMySpaceBinding.inflate(inflater, container, false)
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

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderMusic.adapter = folderAdapter
        binding.rcvMySpaceFolderMusic.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = MySpaceMusicAdapter(object : MySpaceMusicAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_musicDetailFragment)
            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileMusic.adapter = fileAdapter
        binding.rcvMySpaceFileMusic.setHasFixedSize(true)
    }
}