package com.example.mediaapp.repository

import android.content.SharedPreferences
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
import java.net.URLConnection

class MediaRepository(private val mediaAPI:MediaAPI, private val sharedPreferences: SharedPreferences) {

    //remote
    suspend fun changePassword(token: String, newPassword: String, confirmPassword: String): Response<ResponseBody>{
        val body = HashMap<String, String>()
        body["token"] = token
        body["newPassword"] = newPassword
        body["confirmPassword"] = confirmPassword
        return mediaAPI.changePassword(body, "Bearer ${getUserToken()}")
    }

    suspend fun checkPassword(user: User) = mediaAPI.checkPassword(user, "Bearer ${getUserToken()}")

    suspend fun changeAccountInfo(user: User) = mediaAPI.changeAccountInfo(user, "Bearer ${getUserToken()}")

    suspend fun getAccountInfo() = mediaAPI.getAccountInfo("Bearer ${getUserToken()}")

    suspend fun deleteDirectoryFromFavorite(directoryId: String) = mediaAPI.deleteDirectoryFromFavorite(directoryId, "Bearer ${getUserToken()}")

    suspend fun deleteFileFromFavorite(fileId: String) = mediaAPI.deleteFileFromFavorite(fileId, "Bearer ${getUserToken()}")

    suspend fun getListFolderInFavorite(directoryId: String, page: Int, pageSize: Int) = mediaAPI.getListFolderInFavorite(directoryId, page, pageSize, "Bearer ${getUserToken()}")

    suspend fun getListFileInFavorite(directoryId: String, page: Int, pageSize: Int) = mediaAPI.getListFileInFavorite(directoryId, page, pageSize, "Bearer ${getUserToken()}")

    suspend fun deleteFileShareByOwner(fileId: String, receiverEmail: String) = mediaAPI.deleteFileShareByOwner(fileId, receiverEmail, "Bearer ${getUserToken()}")

    suspend fun deleteDirectoryShareByOwner(directoryId: String, receiverEmail: String) = mediaAPI.deleteDirectoryShareByOwner(directoryId, receiverEmail, "Bearer ${getUserToken()}")

    suspend fun getListFolderInMyShare(parentId: String) = mediaAPI.getListFolderInMyShare(parentId, "Bearer ${getUserToken()}")

    suspend fun getListFileInMyShare(parentId: String) = mediaAPI.getListFileInMyShare(parentId, "Bearer ${getUserToken()}")

    suspend fun deleteFileShareByCustomer(fileId: String) = mediaAPI.deleteFileShareByCustomer(fileId, "Bearer ${getUserToken()}")

    suspend fun getListFileInShare(directoryId: String, page: Int, pageSize: Int) = mediaAPI.getListFileInShare(directoryId, page, pageSize, "Bearer ${getUserToken()}")

    suspend fun deleteDirectoryShareByCustomer(directoryId: String) = mediaAPI.deleteDirectoryShareByCustomer(directoryId, "Bearer ${getUserToken()}")

    suspend fun addFileToFavorite(fileId: String): Response<ResponseBody>{
        val body = HashMap<String, String>()
        body["fileId"] = fileId
        return mediaAPI.addFileToFavorite(body, "Bearer ${getUserToken()}")
    }

    suspend fun addFileToShare(fileId: String, emailReceiver: String): Response<ResponseBody>{
        val body = HashMap<String, String>()
        body["fileId"] = fileId
        body["emailReceiver"] = emailReceiver
        return mediaAPI.addFileToShare(body, "Bearer ${getUserToken()}")
    }

    suspend fun getFolderInShare(directoryId: String, page: Int, pageSize: Int) = mediaAPI.getFolderInShare(directoryId, page, pageSize,"Bearer ${getUserToken()}")

    suspend fun deleteFile(fileId: String) = mediaAPI.deleteFile(fileId, "Bearer ${getUserToken()}")

    suspend fun uploadFile(directoryId: String, path: String): Response<ResponseBody>{
        val file = File(path)
        val mimeType: String = URLConnection.guessContentTypeFromName(file.name)
        val requestBodyFile = RequestBody.create(MediaType.parse(mimeType), file)
        val multipartFile = MultipartBody.Part.createFormData("multipartFile", file.name, requestBodyFile)
        return mediaAPI.uploadFile(directoryId, multipartFile, "Bearer ${getUserToken()}")
    }

    suspend fun deleteDirectory(directoryId: String) = mediaAPI.deleteDirectory(directoryId, "Bearer ${getUserToken()}")

    suspend fun addDirectoryToShare(directoryId: String, emailReceiver: String): Response<ResponseBody> {
        val body = HashMap<String, String>()
        body["directoryId"] = directoryId
        body["emailReceiver"] = emailReceiver
        return mediaAPI.addDirectoryToShare(body, "Bearer ${getUserToken()}")
    }

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

    fun writeAccountDataToSharedPref(token:String, color: Int){
        sharedPreferences.edit()
            .putString(Constants.USER_TOKEN, token)
            .putBoolean(Constants.FIRST_TIME_LOGIN, false)
            .putInt(Constants.COLOR_AVATAR, color)
            .apply()
    }
    fun getColorAvatar(): Int{
        return sharedPreferences.getInt(Constants.COLOR_AVATAR, 0)
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
            .remove(Constants.COLOR_AVATAR)
            .apply()
    }
}