package com.example.mediaapp.api

import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MediaAPI {
    //user
    @POST("auth/register")
    suspend fun registerAccount(@Body user: User) : Response<ResponseBody>

    @POST("auth/login")
    suspend fun login(@Body user: User) : Response<ResponseBody>

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @GET("accounts")
    suspend fun getAccountsByKeyword(@Query("keyword") keyword: String, @Header("Authorization") token:String) : Response<List<User>>

    //directory
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

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @POST("directories/add-favorite")
    suspend fun addDirectoryToFavorite(@Body body: HashMap<String, String>, @Header("Authorization") token:String): Response<ResponseBody>

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @POST("directories/add-to-share")
    suspend fun addDirectoryToShare(@Body body: HashMap<String, String>, @Header("Authorization") token:String): Response<ResponseBody>

    //file
    @Headers( "Content-Type: application/json;charset=UTF-8")
    @GET("files/{directoryId}")
    suspend fun getListFileByDirectory(@Path("directoryId") directoryId:String,
                                       @Query("page") page:Int,
                                       @Query("pageSize") pageSize:Int,
                                       @Header("Authorization") token:String): Response<ResponseBody>
}