package com.example.mediaapp.features

import android.app.Application
import com.example.mediaapp.api.RetrofitInstance
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants

class MediaApplication : Application() {
    private val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE) }
    val repository by lazy { MediaRepository(RetrofitInstance.api, sharedPreferences) }
}