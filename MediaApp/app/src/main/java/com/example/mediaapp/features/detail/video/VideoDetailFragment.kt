package com.example.mediaapp.features.detail.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentVideoDetailBinding

class VideoDetailFragment : Fragment() {
    private lateinit var binding : FragmentVideoDetailBinding
    private lateinit var mediaController: MediaController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVideoView()
        binding.imageViewBackVideoDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageViewButtonPlay.setOnClickListener {
            clickPlayVideo()
        }
    }

    private fun clickPlayVideo(){
        binding.imageViewButtonPlay.visibility = View.INVISIBLE
        binding.videoViewVideoDetail.start()
        mediaController.visibility = View.VISIBLE
    }

    private fun setUpVideoView(){
        mediaController = MediaController(requireContext())
        mediaController.visibility = View.INVISIBLE
        val uri = Uri.parse("https://khoapham.vn/download/vuoncaovietnam.mp4")
        binding.videoViewVideoDetail.setVideoURI(uri)
        binding.videoViewVideoDetail.setMediaController(mediaController)
    }
}