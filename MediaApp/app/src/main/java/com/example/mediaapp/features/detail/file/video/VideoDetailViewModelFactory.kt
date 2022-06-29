package com.example.mediaapp.features.detail.file.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class VideoDetailViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown VideoDetailViewModel class")
    }
}