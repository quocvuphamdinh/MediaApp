package com.example.mediaapp.features.myshare

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
import com.example.mediaapp.util.ResponseUtil
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class MyShareViewModel(private val mediaRepository: MediaRepository): ViewModel() {
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

    private var _directoryAndFileLongClick: MutableLiveData<Any> = MutableLiveData()
    val directoryAndFileLongClick: LiveData<Any>
        get() = _directoryAndFileLongClick

    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    var option = 1
    fun setDirectoryLongClick(any: Any, option: Int) {
        _directoryAndFileLongClick.postValue(any)
        this.option = option
    }
    fun resetToast() = _toast.postValue("")

    fun deleteDirectoryOrFileByOwner(any: Any, user: User) = viewModelScope.launch {
        try{
            when(any){
                is Directory ->{
                    val response = mediaRepository.deleteDirectoryShareByOwner(any.id.toString(), user.email)
                    ResponseUtil.handlingResponse2(response, "Delete the directory shared with ${user.firstName+user.lastName} successfully", _toast, _success)
                    if(any.receivers.isNotEmpty()){
                        val list = any.receivers.toMutableList()
                        list.remove(user)
                        any.receivers = list
                    }
                    when(any.level){
                        1 -> _folderDocuments.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(_folderRoots.value!![0].id.toString()), _toast)))
                        2 -> _folderMusics.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(_folderRoots.value!![1].id.toString()), _toast)))
                        3 -> _folderPhotos.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(_folderRoots.value!![2].id.toString()), _toast)))
                        4 -> _folderMovies.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(_folderRoots.value!![3].id.toString()), _toast)))
                    }
                }
                is File ->{
                    val response = mediaRepository.deleteFileShareByOwner(any.id.toString(), user.email)
                    ResponseUtil.handlingResponse2(response, "Delete the file shared with ${user.firstName+user.lastName} successfully", _toast, _success)
                    if(any.receivers.isNotEmpty()){
                        val list = any.receivers.toMutableList()
                        list.remove(user)
                        any.receivers = list
                    }
                    when(any.type){
                        Constants.DOCUMENT -> _fileDocuments.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(_folderRoots.value!![0].id.toString()), _toast)))
                        Constants.MUSIC -> _fileMusics.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(_folderRoots.value!![1].id.toString()), _toast)))
                        Constants.PHOTO -> _filePhotos.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(_folderRoots.value!![2].id.toString()), _toast)))
                        Constants.MOVIE -> _fileMovies.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(_folderRoots.value!![3].id.toString()), _toast)))
                    }
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    private fun refresh(listDirectoryMutable: MutableLiveData<List<Directory>>, listFileMutable: MutableLiveData<List<File>>, listDirectoryNew: List<Directory>, listFileNew: List<File>){
        listDirectoryMutable.postValue(listDirectoryNew)
        listFileMutable.postValue(listFileNew)
    }
    private fun refresh2(listDirectoryMutable: MutableLiveData<List<Directory>>, listFileMutable: MutableLiveData<List<File>>){
        listDirectoryMutable.postValue(ArrayList())
        listFileMutable.postValue(ArrayList())
    }
    fun refreshFoldersAndFiles(level: Int) = viewModelScope.launch {
        try {
            when(level){
                1 ->refresh2(_folderDocuments, _fileDocuments)
                2 -> refresh2(_folderMusics, _fileMusics)
                3 -> refresh2(_folderPhotos, _filePhotos)
                4 -> refresh2(_folderMovies, _fileMovies)
            }
            val response = mediaRepository.getListFolderInMyShare(_folderRoots.value!![level-1].id.toString())
            val response2 = mediaRepository.getListFileInMyShare(_folderRoots.value!![level-1].id.toString())
            when(level){
                1 -> {
                    refresh(_folderDocuments, _fileDocuments,
                        getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(
                            response,
                            _toast
                        )),
                        getListFileNew(ResponseUtil.handlingResponseListFile(
                            response2,
                            _toast
                        ))
                    )
                }
                2-> {
                    refresh(_folderMusics, _fileMusics,
                        getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(
                            response,
                            _toast
                        )),
                        getListFileNew(ResponseUtil.handlingResponseListFile(
                            response2,
                            _toast
                        ))
                    )
                }
                3-> {
                    refresh(_folderPhotos, _filePhotos,
                        getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(
                            response,
                            _toast
                        )),
                        getListFileNew(ResponseUtil.handlingResponseListFile(
                            response2,
                            _toast
                        ))
                    )
                }
                4 -> {
                    refresh(_folderMovies, _fileMovies,
                        getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(
                            response,
                            _toast
                        )),
                        getListFileNew(ResponseUtil.handlingResponseListFile(
                            response2,
                            _toast
                        ))
                    )
                }
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }

    fun getFolderRoots() = viewModelScope.launch {
        try {
            val response = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            val list = ResponseUtil.convertToListDirectory(ResponseUtil.handlingResponse(response, _toast))
            _folderRoots.postValue(list)
            if(list.isNotEmpty()){
                launch { _folderMusics.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(list[1].id.toString()), _toast))) }
                launch { _folderMovies.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(list[3].id.toString()), _toast)))}
                launch { _folderPhotos.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(list[2].id.toString()), _toast)))}
                launch { _folderDocuments.postValue(getListDirectoryNew(ResponseUtil.handlingResponseListDirectory(mediaRepository.getListFolderInMyShare(list[0].id.toString()), _toast))) }
                launch { _fileMusics.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(list[1].id.toString()), _toast)))}
                launch { _fileMovies.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(list[3].id.toString()), _toast)))}
                launch { _filePhotos.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(list[2].id.toString()), _toast)))}
                launch { _fileDocuments.postValue(getListFileNew(ResponseUtil.handlingResponseListFile(mediaRepository.getListFileInMyShare(list[0].id.toString()), _toast)))}
                _toast.postValue("")
            }
        }catch (e: Exception){
            _toast.postValue(e.message.toString())
        }
    }
    private fun getListDirectoryNew(list:List<Directory>): List<Directory>{
        val directoryNew = ArrayList<Directory>()
        list.groupBy { it.id }.entries.map { (_, group)-> directoryNew.add(getDirectoryHasReceivers(group))}
        return directoryNew
    }
    private fun getDirectoryHasReceivers (group: List<Directory>): Directory{
        val directoryNew = group[0]
        val listReceiver = ArrayList<User>()
        for (directory in group){
            listReceiver.add(User(directory.receiver!!, "", "", directory.email!!, "", ""))
        }
        directoryNew.receivers = listReceiver
        return directoryNew
    }
    private fun getListFileNew(list:List<File>): List<File>{
        val fileNew = ArrayList<File>()
        list.groupBy { it.id }.entries.map { (_, group)-> fileNew.add(getFileHasReceivers(group))}
        return fileNew
    }
    private fun getFileHasReceivers(group: List<File>): File{
        val fileNew = group[0]
        val listReceiver = ArrayList<User>()
        for (file in group){
            listReceiver.add(User(file.receiver!!, "", "", file.email!!, "", ""))
        }
        fileNew.receivers = listReceiver
        return fileNew
    }
}