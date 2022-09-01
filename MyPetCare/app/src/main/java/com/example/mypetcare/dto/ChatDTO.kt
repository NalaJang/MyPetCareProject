package com.example.mypetcare.dto

data class ChatDTO(
    var uid: String? = null,
    var userName: String? = null,
    var message: String? = null,
    var time:String? = null
)

class ChatModel(
    val users: HashMap<String, Boolean> = HashMap(),
    val comments: HashMap<String, ChatDTO> = HashMap()
)
