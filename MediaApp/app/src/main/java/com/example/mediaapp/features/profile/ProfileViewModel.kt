package com.example.mediaapp.features.profile

import androidx.lifecycle.ViewModel
import com.example.mediaapp.repository.MediaRepository

class ProfileViewModel(private val repository: MediaRepository) : ViewModel() {

    fun removeUserDataFromSharedPref() = repository.removePersonalDataFromSharedPref()
}