package com.example.mediaapp.util

import android.app.Application
import com.example.mediaapp.api.RetrofitInstance
import com.example.mediaapp.repository.MediaRepository

class MediaApplication : Application() {
    val repository by lazy { MediaRepository(RetrofitInstance.api) }
}