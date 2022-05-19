package com.example.mediaapp.api

import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MediaAPI {
    @POST("auth/register")
    suspend fun registerAccount(@Body user: User) : Response<ResponseBody>

    @POST("auth/login")
    suspend fun login(@Body user: User) : Response<ResponseBody>

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @GET("directories/{parentId}")
    suspend fun getFolderByParentId(@Path("parentId") parentId:String,
                                    @Query("page") page:Int,
                                    @Query("pageSize") pageSize:Int,
                                    @Header("Authorization") token:String): Response<ResponseBody>
    @Headers( "Content-Type: application/json;charset=UTF-8")
    @POST("directories")
    suspend fun createDirectory(@Body directory: Directory, @Header("Authorization") token:String): Response<ResponseBody>

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @PUT("directories/update")
    suspend fun editDirectory(@Body body: HashMap<String, String>, @Header("Authorization") token:String): Response<ResponseBody>
}