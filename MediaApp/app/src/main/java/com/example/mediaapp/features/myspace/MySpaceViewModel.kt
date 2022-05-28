package com.example.mediaapp.features.myspace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
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

    var pageDocument = 0
    var pageMusic = 0
    var pagePhoto = 0
    var pageMovie = 0

    var pageDocumentFile = 0
    var pageMusicFile = 0
    var pagePhotoFile = 0
    var pageMovieFile = 0

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
    fun uploadFile(path: String) = viewModelScope.launch {
        try {
            val level = when(path.substring(path.lastIndexOf("."))){
                Constants.MUSIC_EXTENSION -> 2
                Constants.PHOTO_EXTENSION -> 3
                Constants.MOVIE_EXTENSION -> 4
                Constants.DOCUMENT_EXTENSION -> 1
                else -> 0
            }
            if(level!=0){
                val directoryId = _folderRoots.value!![level-1].id
                val response = mediaRepository.uploadFile(directoryId.toString(), path, level)
                if(response.isSuccessful){
                    _toast.postValue("Upload file successfully !")
                    _success.postValue(true)
                    when(level){
                        1 -> updateFilesAfterUpload(_fileDocuments, level, pageDocumentFile)
                        2 -> updateFilesAfterUpload(_fileMusics, level, pageMusicFile)
                        3 -> updateFilesAfterUpload(_filePhotos, level, pagePhotoFile)
                        4 -> updateFilesAfterUpload(_fileMovies, level, pageMovieFile)
                    }
                }else{
                    val jObjError = JSONObject(response.errorBody()?.string()!!)
                    _toast.postValue(jObjError.getString("message"))
                    _success.postValue(false)
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun updateFilesAfterUpload(listMutable: MutableLiveData<List<File>>, level: Int, currentPage: Int) = viewModelScope.launch{
        val list = ArrayList<File>()
        for(i in 0..currentPage){
            list.addAll(convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(_folderRoots.value!![level-1].id.toString(), i, 10))))
        }
        list.removeAll(listMutable.value!!)
        listMutable.postValue(listMutable.value?.plus(list))
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
    fun updateDirectoriesAfterEdit(id: String, newName: String, level: Int, isDelete: Boolean){
        when(level){
            1 -> update(_folderDocuments, id, newName, level, isDelete)
            2 -> update(_folderMusics, id, newName, level, isDelete)
            3 -> update(_folderPhotos, id, newName, level, isDelete)
            4 -> update(_folderMovies, id, newName, level, isDelete)
        }
    }
    private fun updateDirectoriesAfterCreate(listMutable: MutableLiveData<List<Directory>>, level: Int, currentPage: Int) = viewModelScope.launch{
        val list = ArrayList<Directory>()
        for(i in 0..currentPage){
            list.addAll(convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), i, 10))))
        }
        list.removeAll(listMutable.value!!)
        listMutable.postValue(listMutable.value?.plus(list))
    }
    fun createDirectory(directory: Directory) = viewModelScope.launch {
        try {
            val response = mediaRepository.createDirectory(directory)
            if(response.isSuccessful){
                _toast.postValue("Create directory successfully !")
                _success.postValue(true)
                when(directory.level){
                    1 -> updateDirectoriesAfterCreate(_folderDocuments, directory.level, pageDocument)
                    2 -> updateDirectoriesAfterCreate(_folderMusics, directory.level, pageMusic)
                    3 -> updateDirectoriesAfterCreate(_folderPhotos, directory.level, pagePhoto)
                    4 -> updateDirectoriesAfterCreate(_folderMovies, directory.level, pageMovie)
                }
            }else{
                val jObjError = JSONObject(response.errorBody()?.string()!!)
                _toast.postValue(jObjError.getString("message"))
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun loadMoreFolders(listMutable: MutableLiveData<List<Directory>>, isHaveMore: MutableLiveData<Boolean>,level: Int, currentPage: Int) = viewModelScope.launch{
        val list = convertToListDirectory(handlingResponse(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), currentPage, 10)))
        if(list.isNotEmpty()&& listMutable.value?.containsAll(list) == false){
            isHaveMore.postValue(true)
        }else{
            isHaveMore.postValue(false)
        }
        listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
    }

    private fun loadMoreFiles(listMutable: MutableLiveData<List<File>>, isHaveMore: MutableLiveData<Boolean>,level: Int, currentPage: Int) = viewModelScope.launch{
        val list = convertToListFile(handlingResponse(mediaRepository.getListFileByDirectory(_folderRoots.value!![level-1].id.toString(), currentPage, 10)))
        if(list.isNotEmpty()&& listMutable.value?.containsAll(list) == false){
            isHaveMore.postValue(true)
        }else{
            isHaveMore.postValue(false)
        }
        listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
    }

    fun loadMore(page: Int, level: Int, isDirectoryType: Boolean){
        try {
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
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
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
}