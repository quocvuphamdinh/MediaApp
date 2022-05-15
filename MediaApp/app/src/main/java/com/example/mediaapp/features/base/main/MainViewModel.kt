package com.example.mediaapp.features.base.main

import androidx.lifecycle.ViewModel
import com.example.mediaapp.repository.MediaRepository

class MainViewModel(private val repository: MediaRepository) : ViewModel() {

    fun getFirstTimeLogin() = repository.getFirstTimeLogin()
}