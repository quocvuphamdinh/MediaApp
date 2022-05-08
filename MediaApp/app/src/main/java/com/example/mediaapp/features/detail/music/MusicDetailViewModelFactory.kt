package com.example.mediaapp.features.detail.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class MusicDetailViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown MusicDetailViewModel class")
    }
}