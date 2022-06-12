package com.example.mediaapp.features.myshare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class MyShareViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyShareViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyShareViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown MyShareViewModel class")
    }
}