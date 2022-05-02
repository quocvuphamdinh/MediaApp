package com.example.mediaapp.models

import java.util.*

data class User(
    val firstName : String,
    val username : String,
    val lastName: String,
    val email : String,
    val password : String,
    val confirmPassword : String
){
    val id : UUID? = null
}