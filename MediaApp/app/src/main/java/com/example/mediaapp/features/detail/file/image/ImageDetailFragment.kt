package com.example.mediaapp.features.detail.file.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaapp.databinding.FragmentImageDetailBinding
import com.example.mediaapp.features.detail.file.document.ImageDetailAdapter
import com.example.mediaapp.util.DataStore

class ImageDetailFragment : Fragment() {
    private lateinit var binding : FragmentImageDetailBinding
    private lateinit var imageAdapter : ImageDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewImageDetail()

        binding.imageViewBackImageDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerViewImageDetail() {
        imageAdapter = ImageDetailAdapter()
        imageAdapter.submitList(DataStore.getListImage())
        binding.rcvImageDetail.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvImageDetail.adapter = imageAdapter
        binding.rcvImageDetail.setHasFixedSize(true)
    }
}