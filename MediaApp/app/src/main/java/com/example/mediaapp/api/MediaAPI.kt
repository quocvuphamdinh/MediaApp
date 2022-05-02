package com.example.mediaapp.api

import com.example.mediaapp.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MediaAPI {
    @POST("/api/auth/register")
    suspend fun registerAccount(@Body user: User) : Response<ResponseBody>

    @POST("/api/auth/login")
    suspend fun login(@Body user: User) : Response<ResponseBody>
}