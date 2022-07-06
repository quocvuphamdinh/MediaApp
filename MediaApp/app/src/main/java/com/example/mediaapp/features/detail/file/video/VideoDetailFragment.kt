package com.example.mediaapp.features.detail.file.video

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentVideoDetailBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.features.util.LoadingDialogFragment
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.FileUtil

class VideoDetailFragment : Fragment() {
    private lateinit var binding: FragmentVideoDetailBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }
    private lateinit var mediaController: MediaController
    private val viewModel: VideoDetailViewModel by viewModels {
        VideoDetailViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackVideoDetail.setOnClickListener {
            binding.videoViewVideoDetail.setVideoURI(null)
            findNavController().popBackStack()
        }
        binding.imageViewButtonPlay.setOnClickListener {
            clickPlayVideo()
        }
        getDataFile()
        subcribeToObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subcribeToObservers() {
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialogFragment.cancelDialog()
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.fileVideo.observe(viewLifecycleOwner, Observer {
            initVideoMP4(it.content!!, it.name)
        })
    }

    private fun getDataFile() {
        val bundle = arguments
        bundle?.let {
            loadingDialogFragment.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
            val fileId = it.getString(Constants.FILE_DETAIL)
            viewModel.getFile(fileId!!)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initVideoMP4(byteString: String, nameFile: String){
        val file =
            FileUtil.toFilePDFOrMP3OrMP4(
                requireContext(),
                byteString,
                nameFile,
                Constants.MOVIE,
                "Videos"
            )

        val uri = FileUtil.toUri(requireContext(), file)
        setUpVideoView(uri)
    }

    private fun clickPlayVideo() {
        binding.imageViewButtonPlay.visibility = View.INVISIBLE
        binding.videoViewVideoDetail.start()
        mediaController.visibility = View.VISIBLE
    }

    private fun setUpVideoView(uri: Uri) {
        mediaController = MediaController(requireContext())
        mediaController.visibility = View.INVISIBLE
        binding.videoViewVideoDetail.setVideoURI(uri)
        binding.videoViewVideoDetail.setMediaController(mediaController)
    }
}