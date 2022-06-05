package com.example.mediaapp.features.sharewithme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class ShareWithMeViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShareWithMeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShareWithMeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ShareWithMeViewModel class")
    }
}