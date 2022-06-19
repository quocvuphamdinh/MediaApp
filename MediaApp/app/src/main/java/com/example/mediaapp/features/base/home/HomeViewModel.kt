package com.example.mediaapp.features.base.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {
    init {
        getAccountInfo()
    }

    private var _userInfo: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
    get() = _userInfo

    fun getAccountInfo() = viewModelScope.launch {
        try {
            val response = repository.getAccountInfo()
            if(response.isSuccessful){
                val user = response.body()
                user?.color = repository.getColorAvatar()
                _userInfo.postValue(user!!)
            }
        }catch (e: Exception){
            Log.d("error", e.message.toString())
        }
    }
}