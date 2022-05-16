package com.example.mediaapp.features.myspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class MySpaceViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySpaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MySpaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown MySpaceViewModel class")
    }
}