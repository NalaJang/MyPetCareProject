package com.example.mypetcare.dto

data class UserScheduleDTO(
    var uid: String? = null,
    var selectedCategory: String = "",
    var selectedLocation: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var memo: String = "",
    var registrationTime: String = "",

)
