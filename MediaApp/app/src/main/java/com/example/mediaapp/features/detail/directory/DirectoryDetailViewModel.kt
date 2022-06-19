package com.example.mediaapp.features.detail.directory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.ResponseUtil.convertToListDirectory
import com.example.mediaapp.util.ResponseUtil.convertToListFile
import com.example.mediaapp.util.ResponseUtil.handlingResponse
import com.example.mediaapp.util.ResponseUtil.handlingResponse2
import kotlinx.coroutines.launch
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

    fun uploadFile(directoryId: String, path: String) = viewModelScope.launch {
        try {
            val response = mediaRepository.uploadFile(directoryId, path)
            handlingResponse2(response, "Upload file successfully !", _toast, _success)
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun deleteDirectoryOrFile(any: Any?, parentId: String?) = viewModelScope.launch {
        try {
            when(any){
                null -> {
                    val response = mediaRepository.deleteDirectory(parentId!!)
                    handlingResponse2(response, "Delete directory successfully !", _toast, _success)
                }
                is Directory -> {
                    val response = mediaRepository.deleteDirectory(any.id.toString())
                    handlingResponse2(response, "Delete directory successfully !", _toast, _success)
                }
                is File -> {
                    val response = mediaRepository.deleteFile(any.id.toString())
                    handlingResponse2(response, "Delete file successfully !", _toast, _success)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun addDirectoryOrFileToShare(any: Any?, emailReceiver: String, name: String, parentId: String?) = viewModelScope.launch {
        try {
            when(any){
                null ->{
                    val response = mediaRepository.addDirectoryToShare(parentId!!, emailReceiver)
                    handlingResponse2(response, "Share directory to $name successfully !", _toast, _success)
                }
                is Directory -> {
                    val response = mediaRepository.addDirectoryToShare(any.id.toString(), emailReceiver)
                    handlingResponse2(response, "Share directory to $name successfully !", _toast, _success)
                }
                is File -> {
                    val response = mediaRepository.addFileToShare(any.id.toString(), emailReceiver)
                    handlingResponse2(response, "Share file to $name successfully !", _toast, _success)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }


    fun addDirectoryOrFileToFavorite(any: Any?, parentId: String?) = viewModelScope.launch {
        try {
            when(any){
                null -> {
                    val response = mediaRepository.addDirectoryToFavorite(parentId!!)
                    handlingResponse2(response, "Add to favorite successfully !", _toast, _success)
                }
                is Directory -> {
                    val response = mediaRepository.addDirectoryToFavorite(any.id.toString())
                    handlingResponse2(response, "Add to favorite successfully !", _toast, _success)
                }
                is File -> {
                    val response = mediaRepository.addFileToFavorite(any.id.toString())
                    handlingResponse2(response, "Add file to favorite successfully !", _toast, _success)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun editDirectory(any: Any?, newName: String, parentId: String?) = viewModelScope.launch {
        try {
            when(any){
                null -> {
                    val response = mediaRepository.editDirectory(parentId!!, newName)
                    handlingResponse2(response, "Edit directory successfully !", _toast, _success)
                }
                is Directory -> {
                    val response = mediaRepository.editDirectory(any.id.toString(), newName)
                    handlingResponse2(response, "Edit directory successfully !", _toast, _success)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            handlingResponse2(response, "Create directory successfully !", _toast, _success)
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun loadMore(parentId: String, page:Int, type: Int, rootType: Int) = viewModelScope.launch{
        var list: List<Any> = ArrayList()
        when(type){
            Constants.DIRECTORY_TYPE ->{
                when(rootType){
                    Constants.MY_SPACE, Constants.MY_SHARE -> list = convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(parentId, page, 10), _toast))
                    Constants.SHARE_WITH_ME -> list = convertToListDirectory(handlingResponse(mediaRepository.getFolderInShare(parentId, page, 10), _toast))
                    Constants.FAVORITE -> list = convertToListDirectory(handlingResponse(mediaRepository.getListFolderInFavorite(parentId, page, 10), _toast))
                }
                if(list.isNotEmpty()&& _foldersAndFiles.value?.containsAll(list) == false){
                    _isHaveMoreFolders.postValue(true)
                }else{
                    _isHaveMoreFolders.postValue(false)
                }
                Log.d("pageDetailFolders", page.toString())
            }
            Constants.FILE_TYPE ->{
                when(rootType){
                    Constants.MY_SPACE, Constants.MY_SHARE -> list = convertToListDirectory(handlingResponse(mediaRepository.getListFileByDirectory(parentId, page, 10), _toast))
                    Constants.SHARE_WITH_ME -> list = convertToListDirectory(handlingResponse(mediaRepository.getListFileInShare(parentId, page, 10), _toast))
                    Constants.FAVORITE -> list = convertToListDirectory(handlingResponse(mediaRepository.getListFileInFavorite(parentId, page, 10), _toast))
                }
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

    fun getFoldersAndFilesByParentFolder(parentId: String, isFirstTimeLoad: Boolean, isDirectoryType: Boolean, rootType: Int) = viewModelScope.launch {
        try {
            when(isDirectoryType){
                true -> {
                    if(isFirstTimeLoad){
                        when(rootType){
                            Constants.MY_SPACE, Constants.MY_SHARE -> {
                                val response = mediaRepository.getFolderByParentId(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListDirectory(handlingResponse(response, _toast)))
                            }
                            Constants.SHARE_WITH_ME -> {
                                val response = mediaRepository.getFolderInShare(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListDirectory(handlingResponse(response, _toast)))
                            }
                            Constants.FAVORITE -> {
                                val response = mediaRepository.getListFolderInFavorite(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListDirectory(handlingResponse(response, _toast)))
                            }
                        }
                    }else{
                        val list= ArrayList<Directory>()
                        when(rootType){
                            Constants.MY_SPACE, Constants.MY_SHARE -> {
                                for (i in 0..currentPageFolder){
                                    list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(parentId, i, 10), _toast)))
                                }
                            }
                            Constants.SHARE_WITH_ME -> {
                                for (i in 0..currentPageFolder){
                                    list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getFolderInShare(parentId, i, 10), _toast)))
                                }
                            }
                            Constants.FAVORITE -> {
                                for (i in 0..currentPageFolder){
                                    list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getListFolderInFavorite(parentId, i, 10), _toast)))
                                }
                            }
                        }
                        _foldersAndFiles.postValue(list)
                    }
                }
                false -> {
                    if(isFirstTimeLoad){
                        when(rootType){
                            Constants.MY_SPACE, Constants.MY_SHARE->{
                                val response = mediaRepository.getListFileByDirectory(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListFile(handlingResponse(response, _toast)))
                            }
                            Constants.SHARE_WITH_ME ->{
                                val response = mediaRepository.getListFileInShare(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListFile(handlingResponse(response, _toast)))
                            }
                            Constants.FAVORITE ->{
                                val response = mediaRepository.getListFileInFavorite(parentId, 0, 10)
                                _foldersAndFiles.postValue(convertToListFile(handlingResponse(response, _toast)))
                            }
                        }
                    }else{
                        val list = ArrayList<File>()
                        when(rootType){
                            Constants.MY_SPACE, Constants.MY_SHARE -> {
                                for (i in 0..currentPageFile){
                                    list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(parentId, i, 10), _toast)))
                                }
                            }
                            Constants.SHARE_WITH_ME -> {
                                for (i in 0..currentPageFile){
                                    list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileInShare(parentId, i, 10), _toast)))
                                }
                            }
                            Constants.FAVORITE -> {
                                for(i in 0..currentPageFile){
                                    list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileInFavorite(parentId, i, 10), _toast)))
                                }
                            }
                        }
                        _foldersAndFiles.postValue(list)
                    }
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }
    fun clearToast() = _toast.postValue("")
}