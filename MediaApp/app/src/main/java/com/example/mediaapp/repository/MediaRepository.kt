package com.example.mediaapp.repository

import android.content.SharedPreferences
import com.example.mediaapp.api.MediaAPI
import com.example.mediaapp.models.User
import com.example.mediaapp.util.Constants

class MediaRepository(private val mediaAPI:MediaAPI, private val sharedPreferences: SharedPreferences) {

    //remote
    suspend fun registerAccount(user: User) = mediaAPI.registerAccount(user)

    suspend fun login(user: User) = mediaAPI.login(user)


    //local
    fun writeAccountDataToSharedPref(token:String){
        sharedPreferences.edit()
            .putString(Constants.USER_TOKEN, token)
            //.putBoolean(Constants.FIRST_TIME_LOGIN, false)
            .apply()
    }
    fun getFirstTimeLogin():Boolean{
        return sharedPreferences.getBoolean(Constants.FIRST_TIME_LOGIN, true)
    }

    fun removePersonalDataFromSharedPref(){
        sharedPreferences.edit()
            .remove(Constants.USER_TOKEN)
            //.remove(Constants.FIRST_TIME_LOGIN)
            .apply()
    }
}