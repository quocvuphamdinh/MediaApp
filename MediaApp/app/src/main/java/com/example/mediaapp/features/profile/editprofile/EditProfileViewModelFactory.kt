package com.example.mediaapp.features.profile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaapp.repository.MediaRepository

class EditProfileViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown EditProfileViewModel class")
    }
}