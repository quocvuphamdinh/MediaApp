package com.example.mediaapp.api

import com.example.mediaapp.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*
import kotlin.collections.HashMap

interface MediaAPI {
    @POST("/api/auth/register")
    suspend fun registerAccount(@Body user: User) : Response<HashMap<String, String>>
}