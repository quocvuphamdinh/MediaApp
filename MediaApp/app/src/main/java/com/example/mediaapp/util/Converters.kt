package com.example.mediaapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.util.*


object Converters {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toBitmap(byteString: String): Bitmap {
        val bytes = Base64.getDecoder().decode(byteString)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFilePDFOrMP3OrMP4(context: Context, byteString: String, nameFile: String, mimeType: String, type: String): File {
        val byteArray = Base64.getDecoder().decode(byteString)
        val suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) //.pdf
        val file = context.getExternalFilesDir(type)?.let {
            if (!it.exists()) {
                it.mkdir()
            }
            File.createTempFile(nameFile, suffix, it)
        } ?: File.createTempFile(nameFile, suffix, Environment.getExternalStorageDirectory())
        val fos = FileOutputStream(file)
        fos.write(byteArray)
        fos.close()
        return file
    }
}