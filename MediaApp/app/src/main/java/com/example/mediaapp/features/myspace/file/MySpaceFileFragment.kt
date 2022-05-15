package com.example.mediaapp.features.myspace.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFileMySpaceBinding
import com.example.mediaapp.features.adapters.DirectotyAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.util.DataStore

class MySpaceFileFragment : Fragment() {
    private lateinit var binding: FragmentFileMySpaceBinding
    private lateinit var folderAdapter: DirectotyAdapter
    private lateinit var fileAdapter: DirectotyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileMySpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
    }

    private fun setUpRecyclerViewFile() {
        folderAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {
                findNavController().navigate(R.id.action_mySpaceFragment_to_fileDetailFragment)
            }
        })
        folderAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFolderFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFolderFile.adapter = folderAdapter
    }

    private fun setUpRecyclerViewFolder() {
        fileAdapter = DirectotyAdapter(object : DirectotyAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {

            }
        })
        fileAdapter.submitList(DataStore.getListDirectory())
        binding.rcvMySpaceFileFile.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMySpaceFileFile.adapter = fileAdapter
    }
}