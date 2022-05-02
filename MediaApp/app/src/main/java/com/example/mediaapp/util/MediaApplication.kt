package com.example.mediaapp.util

import android.app.Application
import com.example.mediaapp.api.RetrofitInstance
import com.example.mediaapp.repository.MediaRepository

class MediaApplication : Application() {
    private val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE) }
    val repository by lazy { MediaRepository(RetrofitInstance.api, sharedPreferences) }
}