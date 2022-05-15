package com.example.mediaapp.features.onboarding

import androidx.lifecycle.ViewModel
import com.example.mediaapp.repository.MediaRepository

class OnboardingViewModel(private val repository: MediaRepository) : ViewModel() {

    fun saveFirstTimeUseAppToSharedPref() = repository.saveFirstTimeUseAppToSharedPref()
    fun getFirstTimeUseApp() = repository.getFirstTimeUseApp()
}