package com.example.mediaapp.util

import androidx.lifecycle.MutableLiveData
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

object ResponseUtil {
    fun handlingResponseReturnWithValue(response: Response<ResponseBody>, successToast: String, toast: MutableLiveData<String>, nameValue: String,
    isHaveNoMessageError: Boolean = false, messageError:String = ""): String{
        return if(response.isSuccessful){
            toast.postValue(successToast)
            val result = response.body()
            val jsonObj = JSONObject(result!!.charStream().readText())
            jsonObj.getString(nameValue)
        }else{
            if(isHaveNoMessageError){
                toast.postValue(messageError)
            }else{
                val jObjError = JSONObject(response.errorBody()?.string()!!)
                toast.postValue(jObjError.getString("message"))
            }
            "ERROR"
        }
    }
    fun handlingResponseListDirectory(response: Response<List<Directory>>, toast: MutableLiveData<String>): List<Directory> {
        return if(response.isSuccessful) {
            toast.postValue("")
            response.body() ?:ArrayList()
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            toast.postValue(jObjError.getString("message"))
            ArrayList()
        }
    }
    fun handlingResponseListFile(response: Response<List<File>>, toast: MutableLiveData<String>): List<File> {
        return if(response.isSuccessful) {
            toast.postValue("")
            response.body() ?:ArrayList()
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            toast.postValue(jObjError.getString("message"))
            ArrayList()
        }
    }
    fun handlingResponse2(response: Response<ResponseBody>, successToast: String, toast: MutableLiveData<String>, success: MutableLiveData<Boolean>){
        if(response.isSuccessful){
            toast.postValue(successToast)
            success.postValue(true)
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            toast.postValue(jObjError.getString("message"))
            success.postValue(false)
        }
    }
    fun handlingResponse(response: Response<ResponseBody>, toast: MutableLiveData<String>): ResponseBody? {
        return if(response.isSuccessful) {
            toast.postValue("")
            response.body()
        }else{
            val jObjError = JSONObject(response.errorBody()?.string()!!)
            toast.postValue(jObjError.getString("message"))
            null
        }
    }
    fun convertToListDirectory(response: ResponseBody?): List<Directory> {
        response?.let {
            val json = JSONObject(response.charStream().readText())
            return Gson().fromJson(json.getString("items"), Array<Directory>::class.java).toList()
        }
        return ArrayList()
    }
    fun convertToListFile(response: ResponseBody?): List<File> {
        response?.let {
            val json = JSONObject(response.charStream().readText())
            return Gson().fromJson(json.getString("items"), Array<File>::class.java).toList()
        }
        return ArrayList()
    }
}