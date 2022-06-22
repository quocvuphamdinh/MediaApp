package com.example.mediaapp.api

import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MediaAPI {
    //user
    @POST("auth/register")
    suspend fun registerAccount(@Body user: User): Response<ResponseBody>

    @POST("auth/login")
    suspend fun login(@Body user: User): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("accounts/info")
    suspend fun getAccountInfo(@Header("Authorization") token: String): Response<User>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("accounts/change-info")
    suspend fun changeAccountInfo(
        @Body user: User,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("auth/check-password")
    suspend fun checkPassword(
        @Body user: User,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("auth/change-password")
    suspend fun changePassword(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    //directory
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("directories/{parentId}")
    suspend fun getFolderByParentId(
        @Path("parentId") parentId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("directories")
    suspend fun createDirectory(
        @Body directory: Directory,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @PUT("directories/update")
    suspend fun editDirectory(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("directories/add-favorite")
    suspend fun addDirectoryToFavorite(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("directories/add-to-share")
    suspend fun addDirectoryToShare(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("directories/deleted/{directoryId}")
    suspend fun deleteDirectory(
        @Path("directoryId") directoryId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("directories/shares-with-me/{directoryId}")
    suspend fun getFolderInShare(
        @Path("directoryId") directoryId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("directories/customer-delete-share/{directoryId}")
    suspend fun deleteDirectoryShareByCustomer(
        @Path("directoryId") directoryId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("directories/my-share/{parentId}")
    suspend fun getListFolderInMyShare(
        @Path("parentId") parentId: String,
        @Header("Authorization") token: String
    ): Response<List<Directory>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("directories/owner-delete-share/{directoryId}")
    suspend fun deleteDirectoryShareByOwner(
        @Path("directoryId") directoryId: String,
        @Query("receiverEmail") receiverEmail: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("directories/favorites/{directoryId}")
    suspend fun getListFolderInFavorite(
        @Path("directoryId") directoryId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("directories/delete-favorite/{directoryId}")
    suspend fun deleteDirectoryFromFavorite(
        @Path("directoryId") directoryId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    //file
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("files/{directoryId}")
    suspend fun getListFileByDirectory(
        @Path("directoryId") directoryId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Multipart
    @POST("files/upload/{directoryId}")
    suspend fun uploadFile(
        @Path("directoryId") directoryId: String,
        @Part multipartFile: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("files/delete/{fileId}")
    suspend fun deleteFile(
        @Path("fileId") fileId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("files/add-to-share")
    suspend fun addFileToShare(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("files/add-favorite")
    suspend fun addFileToFavorite(
        @Body body: HashMap<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("files/shares-with-me/{directoryId}")
    suspend fun getListFileInShare(
        @Path("directoryId") directoryId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("files/customer-delete-share/{fileId}")
    suspend fun deleteFileShareByCustomer(
        @Path("fileId") fileId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("files/my-share/{parentId}")
    suspend fun getListFileInMyShare(
        @Path("parentId") parentId: String,
        @Header("Authorization") token: String
    ): Response<List<File>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("files/owner-remove-share/{fileId}")
    suspend fun deleteFileShareByOwner(
        @Path("fileId") fileId: String,
        @Query("receiverEmail") receiverEmail: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("files/favorites/{directoryId}")
    suspend fun getListFileInFavorite(
        @Path("directoryId") directoryId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @DELETE("files/delete-favorites/{fileId}")
    suspend fun deleteFileFromFavorite(
        @Path("fileId") fileId: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("views/file/{fileId}")
    suspend fun getFile(
        @Path("fileId") fileId: String,
        @Header("Authorization") token: String
    ): Response<File>

    //search
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("search/{nameFile}")
    suspend fun searchFile(
        @Path("nameFile") nameFile: String,
        @Query("keySearch") keySearch: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}