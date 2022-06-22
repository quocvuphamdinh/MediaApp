package com.example.mediaapp.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProfileViewModel(private val repository: MediaRepository) : ViewModel() {
    init {
        getAccountInfo()
    }

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

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
                _toast.postValue("")
                _success.postValue(true)
            }else{
                val jObjError = JSONObject(response.errorBody()?.string()!!)
                _toast.postValue(jObjError.getString("message"))
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun removeUserDataFromSharedPref() = repository.removePersonalDataFromSharedPref()
}