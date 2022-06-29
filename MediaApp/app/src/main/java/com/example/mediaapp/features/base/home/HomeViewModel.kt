package com.example.mediaapp.features.base.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.File
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {
    init {
        getAccountInfo()
    }

    var isDownloadFile = false

    private var _userInfo: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _userInfo

    private var _isLoadFile: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadFile: LiveData<Boolean>
        get() = _isLoadFile

    private var _fileImage: MutableLiveData<File> = MutableLiveData()
    val fileImage: LiveData<File>
        get() = _fileImage

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    fun getFile(fileId: String) = viewModelScope.launch {
        try {
            isDownloadFile = true
            _isLoadFile.postValue(true)
            val response = repository.getFile(fileId)
            if (response.isSuccessful) {
                _fileImage.postValue(response.body())
            } else {
                _toast.postValue("Get file failed !")
            }
            _isLoadFile.postValue(false)
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _isLoadFile.postValue(false)
        }
    }

    fun getAccountInfo() = viewModelScope.launch {
        try {
            val response = repository.getAccountInfo()
            if (response.isSuccessful) {
                val user = response.body()
                user?.color = repository.getColorAvatar()
                _userInfo.postValue(user!!)
            }
        } catch (e: Exception) {
            Log.d("error", e.message.toString())
        }
    }
}