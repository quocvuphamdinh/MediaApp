package com.example.mediaapp.features.favorite.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentVideoFavoriteBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.DataStore

class FavoriteVideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoFavoriteBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFile()
        setUpRecyclerViewFolder()
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory{
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_favoriteFragment_to_videoDetailFragment)
            }
        })
        fileAdapter.submitList(DataStore.getListFile())
        binding.rcvFavoriteFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileVideo.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderVideo.adapter = folderAdapter
    }
}