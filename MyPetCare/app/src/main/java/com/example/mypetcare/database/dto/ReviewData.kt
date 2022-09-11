package com.example.mypetcare.database.dto

data class ReviewData (
    var uid: String? = null,
    val userName: String? = null,
    val managerName: String? = null,
    var writingTime: String? = null,
    var content: String? = null
)