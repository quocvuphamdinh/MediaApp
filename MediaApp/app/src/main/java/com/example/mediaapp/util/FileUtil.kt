package com.example.mediaapp.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


object FileUtil {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toBitmap(byteString: String): Bitmap {
        val bytes = Base64.getDecoder().decode(byteString)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toFilePDFOrMP3OrMP4(
        context: Context,
        byteString: String,
        nameFile: String,
        mimeType: String,
        type: String
    ): File {
        val byteArray = Base64.getDecoder().decode(byteString)
        val suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
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

    fun downloadFile(context: Context, nameFile: String, mimeType: String, fileSize:Long, file: File): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nameFile)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.SIZE, fileSize)
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            toUri(context, file)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(file.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
    }

    fun toUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }
}