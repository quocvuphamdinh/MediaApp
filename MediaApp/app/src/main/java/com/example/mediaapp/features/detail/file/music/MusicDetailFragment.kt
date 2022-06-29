package com.example.mediaapp.features.detail.file.music

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
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
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.FileUtil
import java.text.SimpleDateFormat


class MusicDetailFragment : Fragment() {
    private lateinit var binding: FragmentMusicDetailBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler = Handler()
    private lateinit var animationRotate: ObjectAnimator
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
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

        getDataFile()
        subscribeToObservers()
        setUpAnimationDisk()

        binding.imageViewBackMusicDetail.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            findNavController().popBackStack()
        }

        binding.imagePlayButton.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                animationRotate.start()
                mediaPlayer.start()
                animationRotate.resume()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_pause)
            } else {
                mediaPlayer.pause()
                animationRotate.pause()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_play)
            }
        }
        binding.imageSkipBack.setOnClickListener {
            seekMusic(false)
        }
        binding.imageSkipNext.setOnClickListener {
            seekMusic(true)
        }
    }

    private fun seekMusic(option: Boolean) {
        if (option) {
            binding.seekBarMusicDetail.progress =
                binding.seekBarMusicDetail.progress + 5000
            if (binding.seekBarMusicDetail.progress >= binding.seekBarMusicDetail.max) {
                binding.seekBarMusicDetail.progress = 0
            }
        } else {
            binding.seekBarMusicDetail.progress =
                binding.seekBarMusicDetail.progress - 5000
            if (binding.seekBarMusicDetail.progress <= 0) {
                binding.seekBarMusicDetail.progress = 0
            }
        }
        mediaPlayer.seekTo(binding.seekBarMusicDetail.progress)
        binding.textViewStartTimeMusicDetail.text =
            getFormattedDurationMusic(mediaPlayer.currentPosition)
    }

    private fun setUpSeekBar() {
        binding.seekBarMusicDetail.progress = 0
        binding.seekBarMusicDetail.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        runnable = Runnable {
            binding.seekBarMusicDetail.progress = mediaPlayer.currentPosition
            binding.textViewStartTimeMusicDetail.text =
                getFormattedDurationMusic(mediaPlayer.currentPosition)
            handler.postDelayed(runnable, 1000)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.pause()
                animationRotate.pause()
                binding.imagePlayButton.setImageResource(R.drawable.ic_button_play)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFormattedDurationMusic(value: Int): String {
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        return simpleDateFormat.format(value)
    }

    private fun getDataFile() {
        val bundle = arguments
        bundle?.let {
            loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
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
    fun initMusicMP3(byteString: String, nameFile: String) {
        val file =
            FileUtil.toFilePDFOrMP3OrMP4(
                requireContext(),
                byteString,
                nameFile,
                Constants.MUSIC,
                "Audio"
            )

        val uri = FileUtil.toUri(requireContext(), file)

        mediaPlayer = MediaPlayer.create(requireContext(), uri)

        binding.textViewEndTimeMusicDetail.text = getFormattedDurationMusic(mediaPlayer.duration)
        binding.seekBarMusicDetail.max = mediaPlayer.duration
        binding.textViewNameMusicDetail.text = nameFile

        setUpSeekBar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeToObservers() {
        viewModel.fileImage.observe(viewLifecycleOwner, Observer {
            initMusicMP3(it.content!!, it.name)
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
        })
    }
}