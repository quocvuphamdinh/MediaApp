package com.example.mediaapp.features.detail.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentFileDetailBinding

class FileDetailFragment : Fragment() {
    private lateinit var binding : FragmentFileDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackFileDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}