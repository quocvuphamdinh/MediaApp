package com.example.mediaapp.api

import com.example.mediaapp.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MediaAPI {
    @POST("auth/register")
    suspend fun registerAccount(@Body user: User) : Response<ResponseBody>

    @POST("auth/login")
    suspend fun login(@Body user: User) : Response<ResponseBody>
}