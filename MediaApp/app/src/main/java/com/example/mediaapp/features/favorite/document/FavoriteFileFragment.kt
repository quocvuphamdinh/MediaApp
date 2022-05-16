package com.example.mediaapp.features.favorite.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFileFavoriteBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class FavoriteFileFragment : Fragment() {
    private lateinit var binding : FragmentFileFavoriteBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?) {
                findNavController().navigate(R.id.action_favoriteFragment_to_fileDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileFile.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?) {

            }
        }, R.layout.my_space_music_item_row, false)
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderFile.adapter = fileAdapter
    }
}