package com.example.mediaapp.features.myspace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.async
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

    var pageDocument = 0
    var pageMusic = 0
    var pagePhoto = 0
    var pageMovie = 0

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
    private fun update(list: MutableLiveData<List<Directory>>, id: String, newName: String, level: Int){
        val oldDirectory = list.value!!.find { it.id == UUID.fromString(id) }
        oldDirectory?.let {
            val newDirectory = it.copy(name = newName, level = level, parentId = it.parentId)
            newDirectory.id = it.id
            val mutableList = list.value!!.toMutableList()
            mutableList[list.value!!.indexOf(it)] = newDirectory
            list.postValue(mutableList.toList())
        }
    }
    fun updateDirectoriesAfterEdit(id: String, newName: String, level: Int){
        when(level){
            1 -> update(_folderDocuments, id, newName, level)
            2 -> update(_folderMusics, id, newName, level)
            3 -> update(_folderPhotos, id, newName, level)
            4 -> update(_folderMovies, id, newName, level)
        }
    }
    private fun updateDirectoriesAfterCreate(listMutable: MutableLiveData<List<Directory>>, level: Int, currentPage: Int) = viewModelScope.launch{
        val list = ArrayList<Directory>()
        for(i in 0..currentPage){
            list.addAll(getFolders(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), i, 10)))
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
                val jObjError = JSONObject(response.errorBody()?.toString()!!)
                _toast.postValue(jObjError.getJSONObject("error").getString("message"))
                _success.postValue(false)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }
    private fun loadMore(listMutable: MutableLiveData<List<Directory>>, isHaveMore: MutableLiveData<Boolean>,level: Int, currentPage: Int) = viewModelScope.launch{
        val list = getFolders(mediaRepository.getFolderByParentId(_folderRoots.value!![level-1].id.toString(), currentPage, 10))
        if(list.isNotEmpty()&& listMutable.value?.containsAll(list) == false){
            isHaveMore.postValue(true)
        }else{
            isHaveMore.postValue(false)
        }
        listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
    }

    fun loadMoreFolders(page: Int, level: Int){
        try {
            Log.d("page", page.toString())
            when(level){
                1 -> loadMore(_folderDocuments, _isHaveMoreDocuments, level, page)
                2 -> loadMore(_folderMusics, _isHaveMoreMusics, level, page)
                3 -> loadMore(_folderPhotos, _isHaveMorePhotos, level, page)
                4 -> loadMore(_folderMovies, _isHaveMoreMovies, level, page)
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }

    fun getFolderRoots() = viewModelScope.launch {
        try{
            val response = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            val list = getFolders(response)
            _folderRoots.postValue(list)
            if(list.isNotEmpty()){
                launch { _folderMusics.postValue(getFolders(mediaRepository.getFolderByParentId(list[1].id.toString(), 0, 10))) }
                launch { _folderMovies.postValue(getFolders(mediaRepository.getFolderByParentId(list[3].id.toString(), 0, 10))) }
                launch { _folderPhotos.postValue(getFolders(mediaRepository.getFolderByParentId(list[2].id.toString(), 0, 10))) }
                launch { _folderDocuments.postValue(getFolders(mediaRepository.getFolderByParentId(list[0].id.toString(), 0, 10))) }
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
}