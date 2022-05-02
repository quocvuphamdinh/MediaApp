package com.example.mediaapp.features.splash

import androidx.lifecycle.ViewModel
import com.example.mediaapp.repository.MediaRepository

class SplashViewModel(private val repository: MediaRepository) : ViewModel() {

    fun getFirstTimeLogin() = repository.getFirstTimeLogin()
}