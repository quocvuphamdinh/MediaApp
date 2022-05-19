package com.example.mediaapp.features.favorite.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicFavoriteBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class FavoriteMusicFragment : Fragment() {
    private lateinit var binding : FragmentMusicFavoriteBinding
    private lateinit var folderAdapter : DirectoryAdapter
    private lateinit var fileAdapter : DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?, isHaveOptions: Boolean) {
                findNavController().navigate(R.id.action_favoriteFragment_to_musicDetailFragment)
            }
        }, R.layout.my_space_music_item_row, false)
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileMusic.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory?, isHaveOptions: Boolean) {

            }
        }, R.layout.my_space_music_item_row, false)
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderMusic.adapter = fileAdapter
    }
}