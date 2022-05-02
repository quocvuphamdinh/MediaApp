package com.example.mediaapp.api

import com.example.mediaapp.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*
import kotlin.collections.HashMap

interface MediaAPI {
    @POST("/api/auth/register")
    suspend fun registerAccount(@Body user: User) : Response<HashMap<String, String>>

    @POST("/api/auth/login")
    suspend fun login(@Body user: User) : Response<ResponseBody>
}