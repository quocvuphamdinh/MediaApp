package com.example.mediaapp.features.detail.file.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.File
import com.example.mediaapp.repository.MediaRepository
import kotlinx.coroutines.launch

class MusicDetailViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
    private var _fileImage: MutableLiveData<File> = MutableLiveData()
    val fileImage: LiveData<File>
        get() = _fileImage

    private var _isPlay: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPlay: LiveData<Boolean>
        get() = _isPlay

    private var _isFirstTimePlay: MutableLiveData<Boolean> = MutableLiveData(true)
    val isFirstTimePlay: LiveData<Boolean>
        get() = _isFirstTimePlay

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    fun playMusic(value: Boolean) {
        _isPlay.postValue(value)
    }

    fun setFirstTimePlay() {
        _isFirstTimePlay.postValue(false)
    }

    fun getFile(fileId: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.getFile(fileId)
            if (response.isSuccessful) {
                _fileImage.postValue(response.body())
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