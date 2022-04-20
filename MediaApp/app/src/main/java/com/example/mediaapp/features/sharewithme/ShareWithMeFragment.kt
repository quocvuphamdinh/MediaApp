package com.example.mediaapp.features.sharewithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaapp.MainActivity
import com.example.mediaapp.databinding.FragmentShareWithMeBinding

class ShareWithMeFragment : Fragment(){
    private lateinit var binding : FragmentShareWithMeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).binding.toolbarMain.title = "Share with me"
    }
}