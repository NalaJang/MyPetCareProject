package com.example.mypetcare.dto

data class ChatData(
    var uid: String? = null,
//    val chatUsers: HashMap<String, Boolean> = HashMap(),
    var userEmail: String? = "",
    var userName: String? = "",
    var message: String? = "",
    var time: String? = ""
)
