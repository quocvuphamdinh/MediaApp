package com.example.mediaapp.features.profile.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.ResponseUtil
import kotlinx.coroutines.launch

class EditProfileViewModel(private val repository: MediaRepository): ViewModel() {

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    fun changeAccountInfo(firstName: String, lastName: String) = viewModelScope.launch {
        try {
            if(firstName.isNotEmpty() && lastName.isNotEmpty()){
                val user = User(firstName, "", lastName, "", "", "")
                val response = repository.changeAccountInfo(user)
                ResponseUtil.handlingResponse2(response, "Change account info successfully !", _toast, _success)
            }else{
                _toast.postValue("Firstname and lastname must not be empty !")
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
}