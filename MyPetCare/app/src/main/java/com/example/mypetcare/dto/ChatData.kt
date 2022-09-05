package com.example.mypetcare.dto

data class ChatData(
    var uid: String? = null,
    var roomUid: String? = null,
    val member: HashMap<String, Boolean> = HashMap(),
    var userEmail: String? = null,
    var userName: String? = null,
    var message: String? = null,
    var time: String? = null
)
