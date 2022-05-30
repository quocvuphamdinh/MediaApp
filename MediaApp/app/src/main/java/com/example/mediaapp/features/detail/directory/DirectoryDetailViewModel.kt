package com.example.mediaapp.features.detail.directory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.models.User
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class DirectoryDetailViewModel(private val mediaRepository: MediaRepository): ViewModel() {

    private var _foldersAndFiles: MutableLiveData<List<Any>> = MutableLiveData(ArrayList())
    val foldersAndFiles: LiveData<List<Any>>
    get() = _foldersAndFiles

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    private var _isHaveMoreFolders: MutableLiveData<Boolean> = MutableLiveData()
    val isHaveMoreFolders: LiveData<Boolean>
    get() = _isHaveMoreFolders

    private var _isHaveMoreFiles: MutableLiveData<Boolean> = MutableLiveData()
    val isHaveMoreFiles: LiveData<Boolean>
    get() = _isHaveMoreFiles

    var currentPageFolder = 0
    var currentPageFile = 0
    var isPause = false
    var isShowLoading = true

    fun deleteFile(fileId: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.deleteFile(fileId)
            handlingResponse2(response, "Delete file successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun uploadFile(directoryId: String, path: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.uploadFile(directoryId, path)
            handlingResponse2(response, "Upload file successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun deleteDirectory(directoryId: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.deleteDirectory(directoryId)
            handlingResponse2(response, "Delete directory successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun addDirectoryToShare(directoryId: String, userId: String, name: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.addDirectoryToShare(directoryId, userId)
            handlingResponse2(response, "Share to $name successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun getAccountsByKeyword(keyword: String, accounts: MutableLiveData<List<User>>) = viewModelScope.launch {
        try {
            val response = mediaRepository.getAccountsByKeyword(keyword)
            if(response.body()==null){
                accounts.postValue(ArrayList())
            }else{
                accounts.postValue(response.body())
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }

    fun addDirectoryToFavorite(directoryId: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.addDirectoryToFavorite(directoryId)
            handlingResponse2(response, "Add to favorite successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun editDirectory(directoryId: String, newName: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.editDirectory(directoryId, newName)
            handlingResponse2(response, "Edit directory successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            handlingResponse2(response, "Create directory successfully !")
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun loadMore(parentId: String, page:Int, type: Int) = viewModelScope.launch{
        var list: List<Any> = ArrayList()
        when(type){
            Constants.DIRECTORY_TYPE ->{
                list = convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(parentId, page, 10)))
                if(list.isNotEmpty()&& _foldersAndFiles.value?.containsAll(list) == false){
                    _isHaveMoreFolders.postValue(true)
                }else{
                    _isHaveMoreFolders.postValue(false)
                }
                Log.d("pageDetailFolders", page.toString())
            }
            Constants.FILE_TYPE ->{
                list = convertToListDirectory(handlingResponse(mediaRepository.getListFileByDirectory(parentId, page, 10)))
                if(list.isNotEmpty()&& _foldersAndFiles.value?.containsAll(list) == false){
                    _isHaveMoreFiles.postValue(true)
                }else{
                    _isHaveMoreFiles.postValue(false)
                }
                Log.d("pageDetailFiles", page.toString())
            }
        }
        _foldersAndFiles.postValue(_foldersAndFiles.value?.plus(list)?.distinctBy {
            when(it){
                is Directory ->  it.id
                is File -> it.id
                else -> throw IllegalArgumentException("Invalid view type")
            }
        })
    }

    fun getFoldersAndFilesByParentFolder(parentId: String, isFirstTimeLoad: Boolean, isDirectoryType: Boolean) = viewModelScope.launch {
        try {
            when(isDirectoryType){
                true -> {
                    if(isFirstTimeLoad){
                        val response = mediaRepository.getFolderByParentId(parentId, 0, 10)
                        _foldersAndFiles.postValue(convertToListDirectory(handlingResponse(response)))
                    }else{
                        val list= ArrayList<Directory>()
                        for (i in 0..currentPageFolder){
                            list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(parentId, i, 10))))
                        }
                        _foldersAndFiles.postValue(list)
                    }
                }
                false -> {
                    if(isFirstTimeLoad){
                        val response = mediaRepository.getListFileByDirectory(parentId, 0, 10)
                        _foldersAndFiles.postValue(convertToListFile(handlingResponse(response)))
                    }else{
                        val list = ArrayList<File>()
                        for (i in 0..currentPageFile){
                            list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(parentId, i, 10))))
                        }
                        _foldersAndFiles.postValue(list)
                    }
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }
    private fun handlingResponse2(response: Response<ResponseBody>, successToast: String){
        if(response.isSuccessful){
            _toast.postValue(successToast)
            _success.postValue(true)
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            _toast.postValue(jObjError.getString("message"))
            _success.postValue(false)
        }
    }
    private fun handlingResponse(response: Response<ResponseBody>): ResponseBody?{
        return if(response.isSuccessful){
            _toast.postValue("")
            response.body()
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            _toast.postValue(jObjError.getString("message"))
            null
        }
    }
    private fun convertToListDirectory(response: ResponseBody?): List<Directory> {
        response?.let {
            val json = JSONObject(response.charStream().readText())
            return Gson().fromJson(json.getString("items"), Array<Directory>::class.java).toList()
        }
        return ArrayList()
    }
    private fun convertToListFile(response: ResponseBody?): List<File> {
        response?.let {
            val json = JSONObject(response.charStream().readText())
            return Gson().fromJson(json.getString("items"), Array<File>::class.java).toList()
        }
        return ArrayList()
    }
    fun clearToast() = _toast.postValue("")
}