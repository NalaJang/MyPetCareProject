package com.example.mypetcare.dto

data class ChatUserDTO(
    var uid: String? = null,
    var userEmail: String? = null,
    var userName: String? = null
)

class ChatModel(
    val users: HashMap<String, Boolean> = HashMap(),
    val comments: HashMap<String, Comment> = HashMap()
) {
    class Comment(
        val uid: String? = null,
        val name: String? = null,
        val message: String? = null,
        val time: String? = null
    )
}
