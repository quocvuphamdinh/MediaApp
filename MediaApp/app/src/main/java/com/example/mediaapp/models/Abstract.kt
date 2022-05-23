package com.example.mediaapp.models

import java.util.*

abstract class Abstract{
    abstract var id: UUID?
    abstract var createdDate: String?
    abstract var modifiedDate: String?
    abstract var createdBy: UUID?
    abstract var modifiedBy: UUID?
}