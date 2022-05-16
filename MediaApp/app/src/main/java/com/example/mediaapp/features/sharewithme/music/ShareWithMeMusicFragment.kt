package com.example.mediaapp.features.sharewithme.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicShareWithMeBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class ShareWithMeMusicFragment : Fragment() {
    private lateinit var binding: FragmentMusicShareWithMeBinding
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicShareWithMeBinding.inflate(inflater, container, false)
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
                findNavController().navigate(R.id.action_shareWithMeFragment_to_musicDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFileMusic.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory?) {

            }
        }, R.layout.my_space_music_item_row, false)
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFolderMusic.adapter = fileAdapter
    }
}