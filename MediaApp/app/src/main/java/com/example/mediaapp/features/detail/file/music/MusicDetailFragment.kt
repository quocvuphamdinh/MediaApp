package com.example.mediaapp.features.detail.file.music

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicDetailBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.Converters


class MusicDetailFragment : Fragment() {
    private lateinit var binding: FragmentMusicDetailBinding
    private lateinit var animationRotate: ObjectAnimator
    private val viewModel: MusicDetailViewModel by viewModels {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackMusicDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        getDataFile()
        subscribeToObservers()
        setUpAnimationDisk()

        binding.imagePlayButton.setOnClickListener {
            viewModel.playMusic(!viewModel.isPlay.value!!)
            if (viewModel.isFirstTimePlay.value!!) {
                animationRotate.start()
                viewModel.setFirstTimePlay()
            }
        }
    }

    private fun getDataFile() {
        val bundle = arguments
        bundle?.let {
            val fileId = it.getString(Constants.FILE_DETAIL)
            viewModel.getFile(fileId!!)
        }
    }

    private fun setUpAnimationDisk() {
        animationRotate =
            ObjectAnimator.ofFloat(binding.imageViewDiskMusicDetail, View.ROTATION, 0f, 360f)
                .setDuration(3000)
        animationRotate.repeatCount = Animation.INFINITE
        animationRotate.interpolator = LinearInterpolator()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openMP3(byteString: String, nameFile: String) {
        val file =
            Converters.toFilePDFOrMP3OrMP4(requireContext(), byteString, nameFile, Constants.MUSIC, "Audio")

        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )

        val mediaPlayer = MediaPlayer.create(requireContext(), uri)
        mediaPlayer.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeToObservers() {
        viewModel.fileImage.observe(viewLifecycleOwner, Observer {
            openMP3(it.content!!, it.name)
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            //binding.prbLoad.visibility = View.GONE
        })
        viewModel.isPlay.observe(viewLifecycleOwner, Observer {
            if (it) {
                animationRotate.resume()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_pause)
            } else {
                animationRotate.pause()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_play)
            }
        })
    }

}