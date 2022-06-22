package com.example.mediaapp.features.detail.file.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.File
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch

class FileDetailViewModel(private val repository: MediaRepository): ViewModel() {

    private var _fileImage: MutableLiveData<File> = MutableLiveData()
    val fileImage: LiveData<File>
    get() = _fileImage

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    fun getFile(fileId: String) = viewModelScope.launch {
        try {
            val response = repository.getFile(fileId)
            if(response.isSuccessful){
                _fileImage.postValue(response.body())
                _success.postValue(true)
            }else{
                _toast.postValue("Get file failed !")
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
}