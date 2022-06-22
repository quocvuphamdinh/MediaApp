package com.example.mediaapp.features.detail.file.image

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mediaapp.databinding.FragmentImageDetailBinding
import com.example.mediaapp.features.MediaApplication
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.Converters

class ImageDetailFragment : Fragment() {
    private lateinit var binding : FragmentImageDetailBinding
    private val viewModel: ImageDetailViewModel by viewModels {
        ImageDetailViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.prbLoad.visibility = View.VISIBLE
        binding.imageViewBackImageDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        getFileData()
        subcribeToObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subcribeToObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            binding.prbLoad.visibility = View.GONE
        })
        viewModel.fileImage.observe(viewLifecycleOwner, Observer {
            binding.imageViewDetail.setImageBitmap(Converters.toBitmap(it.content!!))
        })
    }

    private fun getFileData() {
        val bundle = arguments
        bundle?.let {
            val fileId = it.getString(Constants.FILE_DETAIL)
            viewModel.getFile(fileId!!)
        }
    }
}