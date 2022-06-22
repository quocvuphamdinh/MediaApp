package com.example.mediaapp.features.detail.file.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class FileDetailViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown FileDetailViewModel class")
    }
}