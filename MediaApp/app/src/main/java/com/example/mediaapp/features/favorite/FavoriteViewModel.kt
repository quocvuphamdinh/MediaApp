package com.example.mediaapp.features.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.repository.MediaRepository
import com.example.mediaapp.util.Constants
import com.example.mediaapp.util.ResponseUtil
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FavoriteViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
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

    private var _fileImage: MutableLiveData<File> = MutableLiveData()
    val fileImage: LiveData<File>
        get() = _fileImage

    private var _isLoadFile: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadFile: LiveData<Boolean>
        get() = _isLoadFile

    var isOpenFile = false

    var pageDocument = 0
    var pageMusic = 0
    var pagePhoto = 0
    var pageMovie = 0

    var pageDocumentFile = 0
    var pageMusicFile = 0
    var pagePhotoFile = 0
    var pageMovieFile = 0

    init {
        getFolderRoots()
    }

    var option = 1
    fun setDirectoryLongClick(any: Any, option: Int) {
        _directoryAndFileLongClick.postValue(any)
        this.option = option
    }

    fun resetToast() = _toast.postValue("")

    fun getFile(fileId: String) = viewModelScope.launch {
        try {
            isOpenFile = true
            _isLoadFile.postValue(true)
            val response = mediaRepository.getFile(fileId)
            if (response.isSuccessful) {
                _fileImage.postValue(response.body())
                _success.postValue(true)
            } else {
                _toast.postValue("Get file failed !")
                _success.postValue(false)
            }
            _isLoadFile.postValue(false)
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _success.postValue(false)
            _isLoadFile.postValue(false)
        }
    }

    private fun update2(list: MutableLiveData<List<File>>, id: String) {
        val oldFile = list.value!!.find { it.id == UUID.fromString(id) }
        oldFile?.let {
            val mutableList = list.value!!.toMutableList()
            mutableList.remove(it)
            list.postValue(mutableList.toList())
        }
    }

    private fun update(list: MutableLiveData<List<Directory>>, id: String) {
        val oldDirectory = list.value!!.find { it.id == UUID.fromString(id) }
        oldDirectory?.let {
            val mutableList = list.value!!.toMutableList()
            mutableList.remove(it)
            list.postValue(mutableList.toList())
        }
    }

    private fun updateDirectoriesAfterDelete(id: String, level: Int) {
        when (level) {
            1 -> update(_folderDocuments, id)
            2 -> update(_folderMusics, id)
            3 -> update(_folderPhotos, id)
            4 -> update(_folderMovies, id)
        }
    }

    private fun updateFilesAfterDelete(id: String, type: String) {
        when (type) {
            Constants.DOCUMENT -> update2(_fileDocuments, id)
            Constants.MUSIC -> update2(_fileMusics, id)
            Constants.PHOTO -> update2(_filePhotos, id)
            Constants.MOVIE -> update2(_fileMovies, id)
        }
    }

    fun deleteDirectoryOrFileFromFavorite(any: Any) = viewModelScope.launch {
        try {
            when (any) {
                is Directory -> {
                    val response = mediaRepository.deleteDirectoryFromFavorite(any.id.toString())
                    ResponseUtil.handlingResponse2(
                        response,
                        "Remove directory ${any.name} from favorite successfully !",
                        _toast,
                        _success
                    )
                    updateDirectoriesAfterDelete(any.id.toString(), any.level)
                }
                is File -> {
                    val response = mediaRepository.deleteFileFromFavorite(any.id.toString())
                    ResponseUtil.handlingResponse2(
                        response,
                        "Remove file ${any.name} from favorite successfully !",
                        _toast,
                        _success
                    )
                    updateFilesAfterDelete(any.id.toString(), any.type)
                }
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    fun addDirectoryOrFileToShare(any: Any, email: String, name: String) = viewModelScope.launch {
        try {
            when (any) {
                is Directory -> {
                    val response = mediaRepository.addDirectoryToShare(any.id.toString(), email)
                    ResponseUtil.handlingResponse2(
                        response,
                        "Share directory to $name successfully !",
                        _toast,
                        _success
                    )
                }
                is File -> {
                    val response = mediaRepository.addFileToShare(any.id.toString(), email)
                    ResponseUtil.handlingResponse2(
                        response,
                        "Share file to $name successfully !",
                        _toast,
                        _success
                    )
                }
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    private fun refresh(
        listDirectoryMutable: MutableLiveData<List<Directory>>,
        listFileMutable: MutableLiveData<List<File>>,
        listDirectoryNew: List<Directory>,
        listFileNew: List<File>
    ) {
        listDirectoryMutable.postValue(listDirectoryNew)
        listFileMutable.postValue(listFileNew)
    }

    private fun refresh2(
        listDirectoryMutable: MutableLiveData<List<Directory>>,
        listFileMutable: MutableLiveData<List<File>>
    ) {
        listDirectoryMutable.postValue(ArrayList())
        listFileMutable.postValue(ArrayList())
    }

    fun refreshFoldersAndFiles(level: Int) = viewModelScope.launch {
        try {
            when (level) {
                1 -> refresh2(_folderDocuments, _fileDocuments)
                2 -> refresh2(_folderMusics, _fileMusics)
                3 -> refresh2(_folderPhotos, _filePhotos)
                4 -> refresh2(_folderMovies, _fileMovies)
            }
            val responseRoot = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            val list = ResponseUtil.convertToListDirectory(
                ResponseUtil.handlingResponse(
                    responseRoot,
                    _toast
                )
            )
            val response = mediaRepository.getListFolderInFavorite(
                list[level - 1].id.toString(),
                0,
                10
            )
            val response2 = mediaRepository.getListFileInFavorite(
                list[level - 1].id.toString(),
                0,
                10
            )
            when (level) {
                1 -> {
                    refresh(
                        _folderDocuments, _fileDocuments,
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                response,
                                _toast
                            )
                        ),
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                response2,
                                _toast
                            )
                        )
                    )
                    pageDocument = 0
                    pageDocumentFile = 0
                }
                2 -> {
                    refresh(
                        _folderMusics, _fileMusics,
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                response,
                                _toast
                            )
                        ),
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                response2,
                                _toast
                            )
                        )
                    )
                    pageMusic = 0
                    pageMusicFile = 0
                }
                3 -> {
                    refresh(
                        _folderPhotos, _filePhotos,
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                response,
                                _toast
                            )
                        ),
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                response2,
                                _toast
                            )
                        )
                    )
                    pagePhoto = 0
                    pagePhotoFile = 0
                }
                4 -> {
                    refresh(
                        _folderMovies, _fileMovies,
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                response,
                                _toast
                            )
                        ),
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                response2,
                                _toast
                            )
                        )
                    )
                    pageMovie = 0
                    pageMovieFile = 0
                }
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
        }
    }

    private fun loadMoreFolders(
        listMutable: MutableLiveData<List<Directory>>,
        isHaveMore: MutableLiveData<Boolean>,
        level: Int,
        currentPage: Int
    ) = viewModelScope.launch {
        try {
            val list = ResponseUtil.convertToListDirectory(
                ResponseUtil.handlingResponse(
                    mediaRepository.getListFolderInFavorite(
                        _folderRoots.value!![level - 1].id.toString(),
                        currentPage,
                        10
                    ), _toast
                )
            )
            if (list.isNotEmpty() && listMutable.value?.containsAll(list) == false) {
                isHaveMore.postValue(true)
            } else {
                isHaveMore.postValue(false)
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            listMutable.postValue(listMutable.value)
        }
    }

    private fun loadMoreFiles(
        listMutable: MutableLiveData<List<File>>,
        isHaveMore: MutableLiveData<Boolean>,
        level: Int,
        currentPage: Int
    ) = viewModelScope.launch {
        try {
            val list = ResponseUtil.convertToListFile(
                ResponseUtil.handlingResponse(
                    mediaRepository.getListFileInFavorite(
                        _folderRoots.value!![level - 1].id.toString(),
                        currentPage,
                        10
                    ), _toast
                )
            )
            if (list.isNotEmpty() && listMutable.value?.containsAll(list) == false) {
                isHaveMore.postValue(true)
            } else {
                isHaveMore.postValue(false)
            }
            listMutable.postValue(listMutable.value?.plus(list)?.distinctBy { it.id })
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            listMutable.postValue(listMutable.value)
        }
    }

    fun loadMore(page: Int, level: Int, isDirectoryType: Boolean) {
        Log.d("page", page.toString())
        when (level) {
            1 -> {
                if (isDirectoryType) {
                    loadMoreFolders(_folderDocuments, _isHaveMoreDocuments, level, page)
                } else {
                    loadMoreFiles(_fileDocuments, _isHaveMoreDocumentsFile, level, page)
                }
            }
            2 -> {
                if (isDirectoryType) {
                    loadMoreFolders(_folderMusics, _isHaveMoreMusics, level, page)
                } else {
                    loadMoreFiles(_fileMusics, _isHaveMoreMusicsFile, level, page)
                }
            }
            3 -> {
                if (isDirectoryType) {
                    loadMoreFolders(_folderPhotos, _isHaveMorePhotos, level, page)
                } else {
                    loadMoreFiles(_filePhotos, _isHaveMorePhotosFile, level, page)
                }
            }
            4 -> {
                if (isDirectoryType) {
                    loadMoreFolders(_folderMovies, _isHaveMoreMovies, level, page)
                } else {
                    loadMoreFiles(_fileMovies, _isHaveMoreMoviesFile, level, page)
                }
            }
        }
    }

    private fun getFolderRoots() = viewModelScope.launch {
        try {
            val response = mediaRepository.getFolderByParentId(Constants.ROOT_FOLDER_ID, 0, 10)
            val list =
                ResponseUtil.convertToListDirectory(ResponseUtil.handlingResponse(response, _toast))
            _folderRoots.postValue(list)
            if (list.isNotEmpty()) {
                launch {
                    _folderMusics.postValue(
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFolderInFavorite(
                                    list[1].id.toString(),
                                    0,
                                    10
                                ),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _folderMovies.postValue(
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFolderInFavorite(
                                    list[3].id.toString(),
                                    0,
                                    10
                                ),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _folderPhotos.postValue(
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFolderInFavorite(
                                    list[2].id.toString(),
                                    0,
                                    10
                                ),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _folderDocuments.postValue(
                        ResponseUtil.convertToListDirectory(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFolderInFavorite(
                                    list[0].id.toString(),
                                    0,
                                    10
                                ),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _fileMusics.postValue(
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFileInFavorite(list[1].id.toString(), 0, 10),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _fileMovies.postValue(
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFileInFavorite(list[3].id.toString(), 0, 10),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _filePhotos.postValue(
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFileInFavorite(list[2].id.toString(), 0, 10),
                                _toast
                            )
                        )
                    )
                }
                launch {
                    _fileDocuments.postValue(
                        ResponseUtil.convertToListFile(
                            ResponseUtil.handlingResponse(
                                mediaRepository.getListFileInFavorite(list[0].id.toString(), 0, 10),
                                _toast
                            )
                        )
                    )
                }
                _toast.postValue("")
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
        }
    }
}