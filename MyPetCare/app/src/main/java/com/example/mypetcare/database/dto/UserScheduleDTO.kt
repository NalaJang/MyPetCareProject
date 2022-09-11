package com.example.mypetcare.database.dto

data class UserScheduleDTO(
    var uid: String? = null,
    var selectedCategory: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var memo: String = "",
    var manager: String? = null,
    var registrationTime: String = "",

)
