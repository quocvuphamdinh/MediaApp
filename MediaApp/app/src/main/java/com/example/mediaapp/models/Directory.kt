package com.example.mediaapp.models

import java.util.*
import kotlin.collections.ArrayList

data class Directory(val name:String, val level: Int, val parentId: UUID): Abstract(){
    override var id: UUID? = null
    override var createdDate: String?=null
    override var modifiedDate: String?=null
    override var createdBy: UUID? = null
    override var modifiedBy: UUID? = null
    val accountId: UUID?=null
    val deleted: Int? =null
    val email: String? = null
    val receiver: String? = null
    var receivers: List<User> = ArrayList()
}