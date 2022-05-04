package com.example.mediaapp.util

import com.example.mediaapp.models.Directory
import java.util.*

object DataStore {
    fun getListDirectory() : List<Directory>{
        return listOf(
            Directory(UUID.randomUUID(), "File 1"),
            Directory(UUID.randomUUID(), "File 2"),
            Directory(UUID.randomUUID(), "File 3"),
            Directory(UUID.randomUUID(), "File 4"),
            Directory(UUID.randomUUID(), "File 5"),
            Directory(UUID.randomUUID(), "File 6"),
            Directory(UUID.randomUUID(), "File 7"),
        )
    }
}