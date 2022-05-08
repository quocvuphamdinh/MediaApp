package com.example.mediaapp.features.sharewithme.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.databinding.FragmentVideoShareWithMeBinding
import com.example.mediaapp.features.adapters.DirectotyAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class ShareWithMeVideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoShareWithMeBinding
    private lateinit var folderAdapter : DirectotyAdapter
    private lateinit var fileAdapter : DirectotyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFile()
        setUpRecyclerViewFolder()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFolderVideo.adapter = folderAdapter
        binding.rcvShareWithMeFolderVideo.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvShareWithMeFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFileVideo.adapter = fileAdapter
        binding.rcvShareWithMeFileVideo.setHasFixedSize(true)
    }
}