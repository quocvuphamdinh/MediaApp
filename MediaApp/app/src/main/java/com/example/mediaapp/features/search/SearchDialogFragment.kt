package com.example.mediaapp.features.search

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentDialogSearchBinding
import com.example.mediaapp.features.adapters.DirectoryAndFileAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.DataStore
import com.example.mediaapp.util.MediaApplication

class SearchDialogFragment : DialogFragment() {
    private lateinit var binding : FragmentDialogSearchBinding
    private lateinit var fileAdapter: DirectoryAndFileAdapter
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory((activity?.application as MediaApplication).repository)
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_search, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            setUpRecyclerViewSearch()
        }
    }

    private fun setUpRecyclerViewSearch(){
        fileAdapter = DirectoryAndFileAdapter(object : DirectoryAndFileAdapter.ClickItemDirectoryAndFile{
            override fun clickItem(item: Any?, isHaveOptions: Boolean) {

            }
        }, 1)
        fileAdapter.submitList(DataStore.getListFile())
        binding.rcvSearch.layoutManager= LinearLayoutManager(requireContext())
        binding.rcvSearch.adapter = fileAdapter
        binding.rcvSearch.setHasFixedSize(true)
    }
}