package com.example.mediaapp.features.detail.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaapp.repository.MediaRepository

class MusicDetailViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    private var _isPlay : MutableLiveData<Boolean> = MutableLiveData(false)
    val isPlay : LiveData<Boolean>
    get() = _isPlay

    private var _isFirstTimePlay : MutableLiveData<Boolean> = MutableLiveData(true)
    val isFirstTimePlay : LiveData<Boolean>
    get() = _isFirstTimePlay

    fun playMusic(value : Boolean){
        _isPlay.postValue(value)
    }
    fun setFirstTimePlay(){
        _isFirstTimePlay.postValue(false)
    }
}