package com.example.mediaapp.features.detail.file.document

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentFileDetailBinding
import com.example.mediaapp.features.MediaApplication

class FileDetailFragment : Fragment() {
    private lateinit var binding: FragmentFileDetailBinding
    private val viewModel: FileDetailViewModel by viewModels {
        FileDetailViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackFileDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}