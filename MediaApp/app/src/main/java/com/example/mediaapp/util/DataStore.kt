package com.example.mediaapp.util

import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import java.util.*

object DataStore {
    fun getListDirectory() : List<Directory>{
        return listOf(
            Directory("File 1", 1, UUID.randomUUID()),
            Directory("File 2", 1, UUID.randomUUID()),
            Directory("File 3", 1, UUID.randomUUID()),
            Directory("File 4", 1, UUID.randomUUID()),
            Directory("File 5", 1, UUID.randomUUID()),
            Directory("File 6", 1, UUID.randomUUID()),
            Directory("File 7", 1, UUID.randomUUID()),
        )
    }
    fun getListImage() : List<File>{
        return listOf(
//            File(UUID.randomUUID(), "Image 1", "3", "https://c4.wallpaperflare.com/wallpaper/511/504/893/makoto-shinkai-kimi-no-na-wa-wallpaper-preview.jpg"),
//            File(UUID.randomUUID(), "Image 2", "3", "https://i.pinimg.com/originals/70/48/c8/7048c84fbf6f44c5e8eec9b8a9146556.jpg"),
//            File(UUID.randomUUID(), "Image 3", "3", "https://www.itl.cat/pngfile/big/143-1431700_the-garden-of-words-hd-wallpapers-garden-of.jpg"),
//            File(UUID.randomUUID(), "Image 4", "3", "https://cutewallpaper.org/26/attack-on-titan-city-wallpaper/1828216449.jpg"),
        )
    }
}