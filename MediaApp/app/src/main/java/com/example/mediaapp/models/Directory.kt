package com.example.mediaapp.models

import java.util.*

data class Directory(val name:String, val level: Int, val parentId: UUID){
    var id: UUID? = null
}