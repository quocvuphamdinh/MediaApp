package com.example.mediaapp.features.authenticate.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.ResponseUtil
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel(val repository: MediaRepository) : ViewModel() {

    private var _loginNotify : MutableLiveData<String> = MutableLiveData()
    val loginNotify : LiveData<String>
    get() = _loginNotify

    private var _isSuccess : MutableLiveData<Boolean> = MutableLiveData(false)
    val isSuccess : LiveData<Boolean>
    get() = _isSuccess

    fun login(user: User) = viewModelScope.launch {
        try{
            val response = repository.login(user)
            val token = ResponseUtil.handlingResponseReturnWithValue(response, "Login successfully !", _loginNotify, "token", true, "Login failed !")
            if(token != "ERROR"){
                repository.writeAccountDataToSharedPref(token, user.color!!)
                _isSuccess.postValue(true)
            }else{
                _isSuccess.postValue(false)
            }
        }catch (e : Exception){
            _loginNotify.postValue(e.message.toString())
            _isSuccess.postValue(false)
        }
    }
}