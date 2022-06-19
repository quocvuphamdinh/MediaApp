package com.example.mediaapp.features.profile.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class ChangePasswordViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ChangePasswordViewModel class")
    }
}