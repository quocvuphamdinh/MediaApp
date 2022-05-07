package com.example.mediaapp.features.myspace.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentVideoMySpaceBinding
import com.example.mediaapp.features.myspace.adapters.MySpaceMusicAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class MySpaceVideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoMySpaceBinding
    private lateinit var folderAdapter : MySpaceMusicAdapter
    private lateinit var fileAdapter : MySpaceMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFile()
        setUpRecyclerViewFolder()
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = MySpaceMusicAdapter(object : MySpaceMusicAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {

            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFolderVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderVideo.adapter = folderAdapter
        binding.rcvMySpaceFolderVideo.setHasFixedSize(true)
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = MySpaceMusicAdapter(object : MySpaceMusicAdapter.CLickItemDirectory{
            override fun clickItem(directory: Directory) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_videoDetailFragment)
            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileVideo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileVideo.adapter = fileAdapter
        binding.rcvMySpaceFileVideo.setHasFixedSize(true)
    }
}