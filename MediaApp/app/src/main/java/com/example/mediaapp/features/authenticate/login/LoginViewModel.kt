package com.example.mediaapp.features.authenticate.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
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
            if(response.isSuccessful){
                val result = response.body()
                val jsonObj = JSONObject(result!!.charStream().readText())
                repository.writeAccountDataToSharedPref(jsonObj.getString("token"))
                _loginNotify.postValue("Login successfully !")
                _isSuccess.postValue(true)
            }else{
                _loginNotify.postValue("Login failed !")
                _isSuccess.postValue(false)
            }
        }catch (e : Exception){
            _loginNotify.postValue(e.message.toString())
            _isSuccess.postValue(false)
        }
    }
}