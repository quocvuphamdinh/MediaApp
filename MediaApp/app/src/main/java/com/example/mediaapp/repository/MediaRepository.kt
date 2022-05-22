package com.example.mediaapp.repository

import android.content.SharedPreferences
import com.example.mediaapp.api.MediaAPI
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants
import okhttp3.ResponseBody
import retrofit2.Response

class MediaRepository(private val mediaAPI:MediaAPI, private val sharedPreferences: SharedPreferences) {

    //remote
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