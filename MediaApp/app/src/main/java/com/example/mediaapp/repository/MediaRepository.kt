package com.example.mediaapp.repository

import com.example.mediaapp.api.MediaAPI
import com.example.mediaapp.models.User

class MediaRepository(val mediaAPI:MediaAPI) {

    suspend fun registerAccount(user: User) = mediaAPI.registerAccount(user)
}