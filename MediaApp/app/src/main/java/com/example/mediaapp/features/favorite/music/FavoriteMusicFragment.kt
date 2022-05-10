package com.example.mediaapp.features.favorite.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentMusicFavoriteBinding
import com.example.mediaapp.features.adapters.DirectotyAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class FavoriteMusicFragment : Fragment() {
    private lateinit var binding : FragmentMusicFavoriteBinding
    private lateinit var folderAdapter : DirectotyAdapter
    private lateinit var fileAdapter : DirectotyAdapter

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
        folderAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFolderMusic.adapter = folderAdapter
        binding.rcvFavoriteFolderMusic.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvFavoriteFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvFavoriteFileMusic.adapter = fileAdapter
        binding.rcvFavoriteFileMusic.setHasFixedSize(true)
    }
}