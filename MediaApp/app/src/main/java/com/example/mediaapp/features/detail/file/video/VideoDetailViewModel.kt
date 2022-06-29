package com.example.mediaapp.features.detail.file.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.File
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch

class VideoDetailViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
    private var _fileVideo: MutableLiveData<File> = MutableLiveData()
    val fileVideo: LiveData<File>
        get() = _fileVideo

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    fun getFile(fileId: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.getFile(fileId)
            if (response.isSuccessful) {
                _fileVideo.postValue(response.body())
                _success.postValue(true)
            } else {
                _toast.postValue("Get file failed !")
                _success.postValue(false)
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
}