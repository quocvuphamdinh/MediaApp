package com.example.mediaapp.api

import com.example.mediaapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy { Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build() }
    val api by lazy { retrofit.create(MediaAPI::class.java) }
}