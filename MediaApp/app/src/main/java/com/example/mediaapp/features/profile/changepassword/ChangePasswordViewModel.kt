package com.example.mediaapp.features.profile.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.ResponseUtil
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.Exception

class ChangePasswordViewModel(private val repository: MediaRepository): ViewModel() {

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    private var _successCheckPassword: MutableLiveData<Boolean> = MutableLiveData()
    val successCheckPassword: LiveData<Boolean>
        get() = _successCheckPassword

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    var token = ""

    fun checkPassword(password: String) = viewModelScope.launch {
        try {
            val response = repository.checkPassword(User("", "", "", "", password, ""))
            token = ResponseUtil.handlingResponseReturnWithValue(response, "", _toast, "token")
            if(token != "ERROR"){
                _successCheckPassword.postValue(true)
            }else{
                _successCheckPassword.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _successCheckPassword.postValue(false)
        }
    }

    fun changePassword(newPassword: String, confirmPassword: String) = viewModelScope.launch {
        try {
            val response = repository.changePassword(token, newPassword, confirmPassword)
            ResponseUtil.handlingResponse2(response, "Change password successfully !", _toast, _success)
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
}