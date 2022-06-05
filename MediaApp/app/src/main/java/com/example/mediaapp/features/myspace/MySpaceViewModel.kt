package com.example.mediaapp.features.myspace

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

class MySpaceViewModel(private val mediaRepository: MediaRepository): ViewModel() {
    private var _folderRoots: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())

    private var _folderDocuments: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())
    val folderDocuments: LiveData<List<Directory>>
    get() = _folderDocuments

    private var _folderMusics: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())
    val folderMusics: LiveData<List<Directory>>
        get() = _folderMusics

    private var _folderPhotos: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())
    val folderPhotos: LiveData<List<Directory>>
        get() = _folderPhotos

    private var _folderMovies: MutableLiveData<List<Directory>> = MutableLiveData(ArrayList())
    val folderMovies: LiveData<List<Directory>>
        get() = _folderMovies

    private var _fileDocuments: MutableLiveData<List<File>> = MutableLiveData(ArrayList())
    val fileDocuments: LiveData<List<File>>
        get() = _fileDocuments

    private var _fileMusics: MutableLiveData<List<File>> = MutableLiveData(ArrayList())
    val fileMusics: LiveData<List<File>>
        get() = _fileMusics

    private var _filePhotos: MutableLiveData<List<File>> = MutableLiveData(ArrayList())
    val filePhotos: LiveData<List<File>>
        get() = _filePhotos

    private var _fileMovies: MutableLiveData<List<File>> = MutableLiveData(ArrayList())
    val fileMovies: LiveData<List<File>>
        get() = _fileMovies

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
    get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
    get() = _success

    private var _isShowFab: MutableLiveData<Boolean> = MutableLiveData(false)
    val isShowFab: LiveData<Boolean>
    get() = _isShowFab

    private var _isHaveMoreDocuments: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreDocuments: LiveData<Boolean>
    get() = _isHaveMoreDocuments

    private var _isHaveMoreMusics: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreMusics: LiveData<Boolean>
    get() = _isHaveMoreMusics

    private var _isHaveMorePhotos: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMorePhotos: LiveData<Boolean>
    get() = _isHaveMorePhotos

    private var _isHaveMoreMovies: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreMovies: LiveData<Boolean>
    get() = _isHaveMoreMovies

    private var _isHaveMoreDocumentsFile: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreDocumentsFile: LiveData<Boolean>
        get() = _isHaveMoreDocumentsFile

    private var _isHaveMoreMusicsFile: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreMusicsFile: LiveData<Boolean>
        get() = _isHaveMoreMusicsFile

    private var _isHaveMorePhotosFile: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMorePhotosFile: LiveData<Boolean>
        get() = _isHaveMorePhotosFile

    private var _isHaveMoreMoviesFile: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHaveMoreMoviesFile: LiveData<Boolean>
        get() = _isHaveMoreMoviesFile

    private var _directoryAndFileLongClick: MutableLiveData<Any> = MutableLiveData()
    val directoryAndFileLongClick: LiveData<Any>
    get() = _directoryAndFileLongClick

    var pageDocument = 0
    var pageMusic = 0
    var pagePhoto = 0
    var pageMovie = 0

    var pageDocumentFile = 0
    var pageMusicFile = 0
    var pagePhotoFile = 0
    var pageMovieFile = 0

    var option = 1
    fun setDirectoryLongClick(any: Any, option: Int) {
        _directoryAndFileLongClick.postValue(any)
        this.option = option
    }
    fun resetToast() = _toast.postValue("")
    fun setVisibleFab(value: Boolean){
        _isShowFab.postValue(value)
    }

    fun getParentId(position: Int): String{
        _folderRoots.value?.let {
            return it[position-1].id.toString()
        }
        return ""
    }
    private fun refresh(listDirectoryMutable: MutableLiveData<List<Directory>>, listFileMutable: MutableLiveData<List<File>>, listDirectoryNew: List<Directory>, listFileNew: List<File>){
        listDirectoryMutable.postValue(listDirectoryNew)
        listFileMutable.postValue(listFileNew)
    }
    fun refreshFoldersAndFiles(level: Int) = viewModelScope.launch {
        try {
            val response = mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), 0, 10)
            val response2 = mediaRepository.getListFileByDirectory(_folderRoots.value!![level-1].id.toString(), 0, 10)
            when(level){
                1 -> {
                    refresh(_folderDocuments, _fileDocuments, convertToListDirectory(handlingResponse(response)), convertToListFile(handlingResponse(response2)))
                    pageDocument = 0
                    pageDocumentFile = 0
                }
                2-> {
                    refresh(_folderMusics, _fileMusics, convertToListDirectory(handlingResponse(response)), convertToListFile(handlingResponse(response2)))
                    pageMusic = 0
                    pageMusicFile = 0
                }
                3-> {
                    refresh(_folderPhotos, _filePhotos, convertToListDirectory(handlingResponse(response)), convertToListFile(handlingResponse(response2)))
                    pagePhoto = 0
                    pagePhotoFile = 0
                }
                4 -> {
                    refresh(_folderMovies, _fileMovies, convertToListDirectory(handlingResponse(response)), convertToListFile(handlingResponse(response2)))
                    pageMovie = 0
                    pageMovieFile = 0
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }
    fun uploadFile(path: String, isFolderRoot: Boolean, directory: Directory?) = viewModelScope.launch {
        try {
            val level = when(path.substring(path.lastIndexOf("."))){
                Constants.MUSIC_EXTENSION -> 2
                Constants.PHOTO_EXTENSION -> 3
                Constants.MOVIE_EXTENSION -> 4
                Constants.DOCUMENT_EXTENSION -> 1
                else -> 0
            }
            if(level!=0){
                var directoryId = ""
                directoryId = if(isFolderRoot){
                    _folderRoots.value!![level-1].id.toString()
                }else{
                    directory?.id.toString()
                }
                val response = mediaRepository.uploadFile(directoryId, path)
                handlingResponse2(response, "Upload file successfully !")
                when(level){
                    1 -> updateFilesAfterUpload(_fileDocuments, level, pageDocumentFile)
                    2 -> updateFilesAfterUpload(_fileMusics, level, pageMusicFile)
                    3 -> updateFilesAfterUpload(_filePhotos, level, pagePhotoFile)
                    4 -> updateFilesAfterUpload(_fileMovies, level, pageMovieFile)
                }
            }else{
                _toast.postValue("File type is not accepted !!")
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun updateFilesAfterUpload(listMutable: MutableLiveData<List<File>>, level: Int, currentPage: Int) = viewModelScope.launch{
        try {
            val list: MutableList<File> = ArrayList()
            for(i in 0..currentPage){
                list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(_folderRoots.value!![level-1].id.toString(), i, 10))))
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
            listMutable.postValue(listMutable.value)
        }
    }
    fun deleteDirectoryOrFile(any: Any) = viewModelScope.launch {
        try {
            when(any){
                is Directory ->{
                    val response = mediaRepository.deleteDirectory(any.id.toString())
                    handlingResponse2(response, "Delete directory successfully !")
                    updateDirectoriesAfterEdit(any.id.toString(), "", any.level, true)
                }
                is File -> {
                    val response = mediaRepository.deleteFile(any.id.toString())
                    handlingResponse2(response, "Delete file successfully !")
                    updateFilesAfterEdit(any.id.toString(), "", any.type, true)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun addDirectoryOrFileToShare(any: Any, email: String, name: String) = viewModelScope.launch {
        try {
            when(any){
                is Directory -> {
                    val response = mediaRepository.addDirectoryToShare(any.id.toString(), email)
                    handlingResponse2(response, "Share directory to $name successfully !")
                }
                is File -> {
                    val response = mediaRepository.addFileToShare(any.id.toString(), email)
                    handlingResponse2(response, "Share file to $name successfully !")
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    fun addDirectoryOrFileToFavorite(any: Any) = viewModelScope.launch {
        try {
            when(any){
                is Directory -> {
                    val response = mediaRepository.addDirectoryToFavorite(any.id.toString())
                    handlingResponse2(response, "Add directory to favorite successfully !")
                }
                is File -> {
                    val response = mediaRepository.addFileToFavorite(any.id.toString())
                    handlingResponse2(response, "Add file to favorite successfully !")
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun update2(list: MutableLiveData<List<File>>, id: String, newName: String, isDelete: Boolean){
        val oldFile = list.value!!.find { it.id == UUID.fromString(id) }
        oldFile?.let {
            val newFile = it.copy(name = newName, displayName = it.displayName, size = it.size, directoryId = it.directoryId, type = it.type)
            newFile.id = it.id
            val mutableList = list.value!!.toMutableList()
            if(!isDelete){
                mutableList[list.value!!.indexOf(it)] = newFile
            }else{
                mutableList.remove(it)
            }
            list.postValue(mutableList.toList())
        }
    }
    private fun update(list: MutableLiveData<List<Directory>>, id: String, newName: String, level: Int, isDelete: Boolean){
        val oldDirectory = list.value!!.find { it.id == UUID.fromString(id) }
        oldDirectory?.let {
            val newDirectory = it.copy(name = newName, level = level, parentId = it.parentId)
            newDirectory.id = it.id
            val mutableList = list.value!!.toMutableList()
            if(!isDelete){
                mutableList[list.value!!.indexOf(it)] = newDirectory
            }else{
                mutableList.remove(it)
            }
            list.postValue(mutableList.toList())
        }
    }
    private fun updateFilesAfterEdit(id: String, newName: String, type: String, isDelete: Boolean){
        when(type){
            Constants.DOCUMENT -> update2(_fileDocuments, id, newName, isDelete)
            Constants.MUSIC -> update2(_fileMusics, id, newName, isDelete)
            Constants.PHOTO -> update2(_filePhotos, id, newName, isDelete)
            Constants.MOVIE -> update2(_fileMovies, id, newName, isDelete)
        }
    }
    fun updateDirectoriesAfterEdit(id: String, newName: String, level: Int, isDelete: Boolean){
        when(level){
            1 -> update(_folderDocuments, id, newName, level, isDelete)
            2 -> update(_folderMusics, id, newName, level, isDelete)
            3 -> update(_folderPhotos, id, newName, level, isDelete)
            4 -> update(_folderMovies, id, newName, level, isDelete)
        }
    }
    fun editDirectoryOrFile(any: Any, newName: String) = viewModelScope.launch {
        try {
            when(any){
                is Directory -> {
                    val response = mediaRepository.editDirectory(any.id.toString(), newName)
                    handlingResponse2(response, "Edit directory successfully !")
                    updateDirectoriesAfterEdit(any.id.toString(), newName, any.level, false)
                }
                is File -> {}
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun updateDirectoriesAfterCreate(listMutable: MutableLiveData<List<Directory>>, level: Int, currentPage: Int) = viewModelScope.launch{
        try {
            val list: MutableList<Directory> = ArrayList()
            for(i in 0..currentPage){
                list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), i, 10))))
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
            listMutable.postValue(listMutable.value)
        }
    }
    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            handlingResponse2(response, "Create directory successfully !")
            when(directory.level){
                1 -> updateDirectoriesAfterCreate(_folderDocuments, directory.level, pageDocument)
                2 -> updateDirectoriesAfterCreate(_folderMusics, directory.level, pageMusic)
                3 -> updateDirectoriesAfterCreate(_folderPhotos, directory.level, pagePhoto)
                4 -> updateDirectoriesAfterCreate(_folderMovies, directory.level, pageMovie)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun loadMoreFolders(listMutable: MutableLiveData<List<Directory>>, isHaveMore: MutableLiveData<Boolean>,level: Int, currentPage: Int) = viewModelScope.launch{
        try {
            val list = convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), currentPage, 10)))
            if(list.isNotEmpty()&& listMutable.value?.containsAll(list) == false){
                isHaveMore.postValue(true)
            }else{
                isHaveMore.postValue(false)
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            listMutable.postValue(listMutable.value)
        }
    }

    private fun loadMoreFiles(listMutable: MutableLiveData<List<File>>, isHaveMore: MutableLiveData<Boolean>,level: Int, currentPage: Int) = viewModelScope.launch{
        try {
            val list = convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(_folderRoots.value!![level-1].id.toString(), currentPage, 10)))
            if(list.isNotEmpty()&& listMutable.value?.containsAll(list) == false){
                isHaveMore.postValue(true)
            }else{
                isHaveMore.postValue(false)
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            listMutable.postValue(listMutable.value)
        }
    }

    fun loadMore(page: Int, level: Int, isDirectoryType: Boolean){
        Log.d("page", page.toString())
        when(level){
            1 -> {
                if(isDirectoryType){
                    loadMoreFolders(_folderDocuments, _isHaveMoreDocuments, level, page)
                }else{
                    loadMoreFiles(_fileDocuments, _isHaveMoreDocumentsFile, level, page)
                }
            }
            2 -> {
                if(isDirectoryType){
                    loadMoreFolders(_folderMusics, _isHaveMoreMusics, level, page)
                }else{
                    loadMoreFiles(_fileMusics, _isHaveMoreMusicsFile, level, page)
                }
            }
            3 -> {
                if(isDirectoryType){
                    loadMoreFolders(_folderPhotos, _isHaveMorePhotos, level, page)
                }else{
                    loadMoreFiles(_filePhotos, _isHaveMorePhotosFile, level, page)
                }
            }
            4 -> {
                if(isDirectoryType){
                    loadMoreFolders(_folderMovies, _isHaveMoreMovies, level, page)
                }else{
                    loadMoreFiles(_fileMovies, _isHaveMoreMoviesFile, level, page)
                }
            }
        }
    }

    fun getFolderRoots() = viewModelScope.launch {
        try{
            val response = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            val list = convertToListDirectory(handlingResponse(response))
            _folderRoots.postValue(list)
            if(list.isNotEmpty()){
                launch { _folderMusics.postValue(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(list[1].id.toString(), 0, 10)))) }
                launch { _folderMovies.postValue(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(list[3].id.toString(), 0, 10)))) }
                launch { _folderPhotos.postValue(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(list[2].id.toString(), 0, 10)))) }
                launch { _folderDocuments.postValue(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(list[0].id.toString(), 0, 10)))) }
                launch { _fileMusics.postValue(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(list[1].id.toString(), 0, 10)))) }
                launch { _fileMovies.postValue(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(list[3].id.toString(), 0, 10)))) }
                launch { _filePhotos.postValue(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(list[2].id.toString(), 0, 10)))) }
                launch { _fileDocuments.postValue(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(list[0].id.toString(), 0, 10)))) }
                _toast.postValue("")
            }
        }catch (e:Exception){
            _toast.postValue(e.message.toString())
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
    private fun handlingResponse(response: Response<ResponseBody>): ResponseBody? {
        return if(response.isSuccessful) {
            _toast.postValue("")
            response.body()
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            _toast.postValue(jObjError.getString("message"))
            null
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
}