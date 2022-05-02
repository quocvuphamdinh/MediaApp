package com.example.mediaapp.features.authenticate.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel(val repository: MediaRepository) : ViewModel() {

    private var _successRegister : MutableLiveData<String> = MutableLiveData()
    val successRegister : LiveData<String>
    get() = _successRegister

    private var _errorToast : MutableLiveData<String> = MutableLiveData()
    val errorToast : LiveData<String>
    get() = _errorToast

    private var _success : MutableLiveData<Boolean> = MutableLiveData(false)
    val success : LiveData<Boolean>
    get() = _success

    fun registerAccount(user: User) = viewModelScope.launch {
        val response = repository.registerAccount(user)
        if(response.isSuccessful){
            val result = response.body()
            val jsonObj = JSONObject(result!!.charStream().readText())
            _successRegister.postValue(jsonObj.getString("message"))
            _success.postValue(true)
        }else{
            val errObj = JSONObject(response.errorBody()!!.charStream().readText())
            _successRegister.postValue(errObj.getString("message"))
            _success.postValue(false)
            Log.d("api", errObj.toString())
        }
    }
    fun validateRegister(user: User):Boolean{
        if(user.firstName.isEmpty()){
            _errorToast.postValue("Firstname không được để trống")
            return false
        }
        if(user.lastName.isEmpty()){
            _errorToast.postValue("Lastname không được để trống")
            return false
        }
        if(user.username.isEmpty()){
            _errorToast.postValue("Username không được để trống")
            return false
        }
        if(user.email.isEmpty()){
            _errorToast.postValue("Email không được để trống")
            return false
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()){
            _errorToast.postValue("Email không đúng định dạng")
            return false
        }
        if (user.password.isEmpty()){
            _errorToast.postValue("Password không được để trống")
            return false
        }
        if(user.confirmPassword.isEmpty()){
            _errorToast.postValue("Confirm password không được để trống")
            return false
        }
        if(user.confirmPassword != user.password){
            _errorToast.postValue("Password và confirm password phải giống nhau")
            return false
        }
        return true
    }
}