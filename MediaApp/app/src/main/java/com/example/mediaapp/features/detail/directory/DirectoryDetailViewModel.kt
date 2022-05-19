package com.example.mediaapp.features.detail.directory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.repository.MediaRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DirectoryDetailViewModel(private val mediaRepository: MediaRepository): ViewModel() {

    private var _folders: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())
    val folders: LiveData<List<Directory>>
    get() = _folders

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    private var _isHaveMore: MutableLiveData<Boolean> = MutableLiveData()
    val isHaveMore: LiveData<Boolean>
    get() = _isHaveMore

    var isPause = false
    var currentPage = 0

    fun loadMore(currentPage: Int, parentId: String) = viewModelScope.launch{
        val list = getFolders(mediaRepository.getFolderByParentId(parentId, currentPage, 10))
        if(list.isNotEmpty()&& _folders.value?.containsAll(list) == false){
            _isHaveMore.postValue(true)
        }else{
            _isHaveMore.postValue(false)
        }
        Log.d("pageDetailloadmore", currentPage.toString())
        _folders.postValue(_folders.value?.plus(list)?.distinctBy { it.id })
    }

    fun editDirectory(directoryId: String, newName: String) = viewModelScope.launch {
        try {
            val hashMap = HashMap<String, String>()
            hashMap["directoryId"] = directoryId
            hashMap["name"] = newName
            val response = mediaRepository.editDirectory(hashMap)
            if(response.isSuccessful){
                _toast.postValue("Edit directory successfully !")
                _success.postValue(true)
            }else{
                val jObjError = JSONObject(response.errorBody()?.toString()!!)
                _toast.postValue(jObjError.getJSONObject("error").getString("message"))
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            if(response.isSuccessful){
                _toast.postValue("Create directory successfully !")
                _success.postValue(true)
            }else{
                val jObjError = JSONObject(response.errorBody()?.toString()!!)
                _toast.postValue(jObjError.getJSONObject("error").getString("message"))
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

    fun getFoldersByParentFolder(parentId: String, isFirstTimeLoad: Boolean) = viewModelScope.launch {
        try {
            if(isFirstTimeLoad){
                val response = mediaRepository.getFolderByParentId(parentId, currentPage, 10)
                _folders.postValue(getFolders(response))
            }else{
                val list = ArrayList<Directory>()
                for (i in 0..currentPage){
                    list.addAll(getFolders(mediaRepository.getFolderByParentId(parentId, i, 10)))
                }
                _folders.postValue(list)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }
    private fun getFolders(response: Response<ResponseBody>): List<Directory> {
        return if(response.isSuccessful) {
            _toast.postValue("")
            convertToListDirectory(response.body())
        }else{
            val jObjError = JSONObject(response.errorBody()?.toString()!!)
            _toast.postValue(jObjError.getJSONObject("error").getString("message"))
            ArrayList()
        }
    }
    fun clearToast() = _toast.postValue("")
}