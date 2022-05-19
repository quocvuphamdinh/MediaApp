package com.example.mediaapp.features.detail.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class DirectoryDetailViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectoryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectoryDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown DirectoryDetailViewModel class")
    }
}