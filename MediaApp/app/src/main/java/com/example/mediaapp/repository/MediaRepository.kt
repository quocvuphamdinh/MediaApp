package com.example.mediaapp.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.mediaapp.api.MediaAPI
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class MediaRepository(private val mediaAPI:MediaAPI, private val sharedPreferences: SharedPreferences) {

    //remote
    suspend fun deleteFile(fileId: String) = mediaAPI.deleteFile(fileId, "Bearer ${getUserToken()}")

    suspend fun uploadFile(directoryId: String, path: String, level: Int): Response<ResponseBody>{
        val file = File(path)
        var type =""
        when(level){
            0 -> type = "multipart/form-data"
            1 -> type = Constants.DOCUMENT
            2 -> type = Constants.MUSIC
            3 -> type = Constants.PHOTO
            4 -> type = Constants.MOVIE
        }
        Log.d("levelvu_directoryid", directoryId)
        Log.d("level_type", type)
        val requestBodyFile = RequestBody.create(MediaType.parse(type), file)
        val multipartFile = MultipartBody.Part.createFormData("multipartFile", file.name, requestBodyFile)
        val requestBodyDisplayName = RequestBody.create(MediaType.parse("multipart/form-data"), file.name)
        val requestBodyLevel = RequestBody.create(MediaType.parse("multipart/form-data"), level.toString())
        return mediaAPI.uploadFile(directoryId, multipartFile, requestBodyDisplayName, requestBodyLevel, "Bearer ${getUserToken()}")
    }

    suspend fun deleteDirectory(directoryId: String) = mediaAPI.deleteDirectory(directoryId, "Bearer ${getUserToken()}")

    suspend fun addDirectoryToShare(directoryId: String, userId: String): Response<ResponseBody> {
        val body = HashMap<String, String>()
        body["directoryId"] = directoryId
        body["userId"] = userId
        return mediaAPI.addDirectoryToShare(body, "Bearer ${getUserToken()}")
    }

    suspend fun getAccountsByKeyword(keyword: String) = mediaAPI.getAccountsByKeyword(keyword, "Bearer ${getUserToken()}")

    suspend fun addDirectoryToFavorite(directoryId: String): Response<ResponseBody> {
        val body = HashMap<String, String>()
        body["directoryId"] = directoryId
        return mediaAPI.addDirectoryToFavorite(body, "Bearer ${getUserToken()}")
    }

    suspend fun editDirectory(directoryId: String, newName: String): Response<ResponseBody> {
        val body = HashMap<String, String>()
        body["directoryId"] = directoryId
        body["name"] = newName
        return mediaAPI.editDirectory(body, "Bearer ${getUserToken()}")
    }

    suspend fun createDirectory(directory: Directory) = mediaAPI.createDirectory(directory, "Bearer ${getUserToken()}")

    suspend fun getListFileByDirectory(directoryId: String, page: Int, pageSize: Int) = mediaAPI.getListFileByDirectory(directoryId, page, pageSize, "Bearer ${getUserToken()}")

    suspend fun getFolderByParentId(parentId: String, page: Int, pageSize: Int) = mediaAPI.getFolderByParentId(parentId, page, pageSize,"Bearer ${getUserToken()}")

    suspend fun registerAccount(user: User) = mediaAPI.registerAccount(user)

    suspend fun login(user: User) = mediaAPI.login(user)

    //local
    private fun getUserToken():String{
        return sharedPreferences.getString(Constants.USER_TOKEN, "")!!
    }

    fun saveFirstTimeUseAppToSharedPref(){
        sharedPreferences.edit().putBoolean(Constants.FIRST_TIME_USE_APP, false).apply()
    }

    fun writeAccountDataToSharedPref(token:String){
        sharedPreferences.edit()
            .putString(Constants.USER_TOKEN, token)
            .putBoolean(Constants.FIRST_TIME_LOGIN, false)
            .apply()
    }
    fun getFirstTimeLogin():Boolean{
        return sharedPreferences.getBoolean(Constants.FIRST_TIME_LOGIN, true)
    }

    fun getFirstTimeUseApp():Boolean{
        return sharedPreferences.getBoolean(Constants.FIRST_TIME_USE_APP, true)
    }

    fun removePersonalDataFromSharedPref(){
        sharedPreferences.edit()
            .remove(Constants.USER_TOKEN)
            .remove(Constants.FIRST_TIME_LOGIN)
            .apply()
    }
}