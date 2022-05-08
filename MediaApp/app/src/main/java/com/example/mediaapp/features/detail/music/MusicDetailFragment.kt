package com.example.mediaapp.features.detail.music

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicDetailBinding
import com.example.mediaapp.util.MediaApplication


class MusicDetailFragment : Fragment() {
    private lateinit var binding : FragmentMusicDetailBinding
    private lateinit var animationRotate : ObjectAnimator
    private val viewModel : MusicDetailViewModel by viewModels {
        MusicDetailViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackMusicDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        subscribeToObservers()
        setUpAnimationDisk()

        binding.imagePlayButton.setOnClickListener {
            viewModel.playMusic(!viewModel.isPlay.value!!)
            if(viewModel.isFirstTimePlay.value!!){
                animationRotate.start()
                viewModel.setFirstTimePlay()
            }
        }
    }
    private fun setUpAnimationDisk(){
        animationRotate = ObjectAnimator.ofFloat(binding.imageViewDiskMusicDetail, View.ROTATION, 0f, 360f)
            .setDuration(3000)
        animationRotate.repeatCount = Animation.INFINITE
        animationRotate.interpolator = LinearInterpolator()
    }

    private fun subscribeToObservers() {
        viewModel.isPlay.observe(viewLifecycleOwner, Observer {
            if(it){
                animationRotate.resume()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_pause)
            }else{
                animationRotate.pause()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_play)
            }
        })
    }

}