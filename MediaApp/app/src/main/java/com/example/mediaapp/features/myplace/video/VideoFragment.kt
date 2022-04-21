package com.example.mediaapp.features.myplace.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.databinding.FragmentVideoMyPlaceBinding

class VideoFragment : Fragment() {
    private lateinit var binding : FragmentVideoMyPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoMyPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }
}