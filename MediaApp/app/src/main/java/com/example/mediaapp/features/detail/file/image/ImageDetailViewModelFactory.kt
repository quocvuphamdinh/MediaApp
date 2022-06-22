package com.example.mediaapp.features.detail.file.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class ImageDetailViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ImageDetailViewModel class")
    }
}