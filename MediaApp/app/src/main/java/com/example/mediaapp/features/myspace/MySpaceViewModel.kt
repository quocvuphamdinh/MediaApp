package com.example.mediaapp.features.myspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class MySpaceViewModel(private val mediaRepository: MediaRepository): ViewModel() {

    private var _folderRoots: MutableLiveData<List<Directory>> = MutableLiveData()
    val folderRoots: LiveData<List<Directory>>
    get() = _folderRoots

    private var _folderDocuments: MutableLiveData<List<Directory>> = MutableLiveData()
    val folderDocuments: LiveData<List<Directory>>
    get() = _folderDocuments

    private var _folderMusics: MutableLiveData<List<Directory>> = MutableLiveData()
    val folderMusics: LiveData<List<Directory>>
        get() = _folderMusics

    private var _folderPhotos: MutableLiveData<List<Directory>> = MutableLiveData()
    val folderPhotos: LiveData<List<Directory>>
        get() = _folderPhotos

    private var _folderMovies: MutableLiveData<List<Directory>> = MutableLiveData()
    val folderMovies: LiveData<List<Directory>>
        get() = _folderMovies

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            if(response.isSuccessful){
                _toast.postValue("Create directory successfully !")
                _success.postValue(true)
                getFolderRoots()
            }else{
                _toast.postValue("Error happened !")
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    private fun convertToListDirectory(response: ResponseBody?): List<Directory> {
        response?.let {
            val json = JSONObject(response.charStream().readText())
            return Gson().fromJson(json.getString("items"), Array<Directory>::class.java).toList()
        }
        return ArrayList()
    }

    fun getFolderRoots() = viewModelScope.launch {
        try{
            val response = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            if(response.isSuccessful){
                val list = convertToListDirectory(response.body())
                _folderRoots.postValue(list)
                _folderDocuments.postValue(getFolders(mediaRepository.getFolderByParentId(list[0].id.toString(), 0, 10)))
                _folderMusics.postValue(getFolders(mediaRepository.getFolderByParentId(list[1].id.toString(), 0, 10)))
                _folderPhotos.postValue(getFolders(mediaRepository.getFolderByParentId(list[2].id.toString(), 0, 10)))
                _folderMovies.postValue(getFolders(mediaRepository.getFolderByParentId(list[3].id.toString(), 0, 10)))
                _toast.postValue("")
            }else{
                _toast.postValue("Error")
            }
        }catch (e:Exception){
            _toast.postValue(e.message.toString())
        }
    }
    private fun getFolders(response: Response<ResponseBody>): List<Directory> {
        if(response.isSuccessful) {
            return convertToListDirectory(response.body())
        }
        _toast.postValue("Error")
        return ArrayList()
    }
}