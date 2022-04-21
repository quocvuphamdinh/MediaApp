package com.example.mediaapp.features.myplace.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.databinding.FragmentImageMyPlaceBinding

class ImageFragment : Fragment() {

    private lateinit var binding : FragmentImageMyPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageMyPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }
}