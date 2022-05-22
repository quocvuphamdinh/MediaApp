package com.example.mediaapp.models

import java.util.*

data class File (
    val id: UUID,
    val name: String,
    val displayName: String,
    val size: Long,
    val directoryId: UUID,
    val type : String,
)