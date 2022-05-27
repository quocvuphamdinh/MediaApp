package com.example.mediaapp.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

object Constants {
    const val BASE_URL = "https://media-api-balong.herokuapp.com/api/"
    const val SHARED_PREFERENCE_NAME = "mediaSharedPref"
    const val USER_TOKEN = "USER_TOKEN"
    const val FIRST_TIME_LOGIN = "FIRST_TIME_LOGIN"
    const val FIRST_TIME_USE_APP = "FIRST_TIME_USE_APP"
    const val SEARCH_DIALOG_TAG = "SEARCH_DIALOG_TAG"
    const val LOADING_DIALOG_TAG = "LOADING_DIALOG_TAG"
    const val CREATE_DIRECTORY_DIALOG_TAG = "CREATE_DIRECTORY_DIALOG_TAG"
    const val BOTTOM_SHEET_OPTION_TAG = "BOTTOM_SHEET_OPTION_TAG"
    const val SEARCH_DIALOG_ACCOUNT_TAG = "SEARCH_DIALOG_ACCOUNT_TAG"
    const val WARNING_DIALOG = "WARNING_DIALOG"
    const val DIRECTORY_ID = "DIRECTORY_ID"
    const val DIRECTORY_NAME = "DIRECTORY_NAME"
    const val DIRECTORY_LEVEL = "DIRECTORY_LEVEL"
    const val ROOT_FOLDER_ID = "572eec8a-bd43-11ec-9d64-0242ac120002"
    const val DIRECTORY_TYPE = 0
    const val FILE_TYPE = 1
    const val REQUEST_CODE_OPEN_FILE = 1
    const val DOCUMENT = "application/pdf"
    const val MUSIC = "audio/mpeg"
    const val PHOTO = "image/jpeg"
    const val MOVIE = "video/mp4"
    const val MY_SPACE = 1
    const val SHARE_WITH_ME = 2
    const val FAVORITE = 3
    const val ROOT_TYPE = "ROOT_TYPE"

    fun clickRequestPermissionToAccessFile(activity: Activity, clickOpenFile: () -> Unit){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if((activity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                &&(activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
                clickOpenFile()
            }else{
                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                activity.requestPermissions(permission, REQUEST_CODE_OPEN_FILE)
            }
        }else{
            clickOpenFile()
        }
    }
}