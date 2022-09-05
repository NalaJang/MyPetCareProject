package com.example.mypetcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.MessageBoxViewType
import com.example.mypetcare.R
import com.example.mypetcare.dto.ChatModel
import com.example.mypetcare.dto.ChatUserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatAdapter(val chatRoomUid: String): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chat")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private val comments = ArrayList<ChatModel.Comment>()
    private var chatUser: ChatUserDTO? = null

    init {
        databaseReference.child("users")
                            .child("otherUserUid")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    chatUser = snapshot.getValue<ChatUserDTO>()
                                    println("ChatAdapter, userName >> ${chatUser?.userName}")
                                    getBoolean()
                                    getMessageList()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("onCreateViewHolder, viewType >> ${viewType}")
        val view: View?
        if( viewType == MessageBoxViewType.LEFT_BUBBLE ) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.my_message_box, parent, false)
        } else if( viewType == MessageBoxViewType.RIGHT_BUBBLE )
            view = LayoutInflater.from(parent.context).inflate(R.layout.message_box, parent, false)
        else
            view = LayoutInflater.from(parent.context).inflate(R.layout.message_box, parent, false)

        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if( currentUser == itemList[position].userName ) {
//            holder.chattingMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
//        }

        holder.yourName.text = comments[position].name
        holder.yourMessage.text = comments[position].message
        holder.yourTime.text = comments[position].time
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val layout = itemView.findViewById<LinearLayout>(R.id.chatting_leftLayout)
        val yourName = itemView.findViewById<TextView>(R.id.chatting_yourName)
        val yourMessage = itemView.findViewById<TextView>(R.id.chatting_yourMessage)
        val yourTime = itemView.findViewById<TextView>(R.id.chatting_yourTime)
//        val myName = itemView.findViewById<TextView>(R.id.chatting_myName)
//        val myMessage = itemView.findViewById<TextView>(R.id.chatting_myMessage)
//        val myTime = itemView.findViewById<TextView>(R.id.chatting_myTime)

    }


    private fun getMessageList() {
        databaseReference.child(uid.toString()).child("comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()

                    for( data in snapshot.children ) {
                        val item = data.getValue<ChatModel.Comment>()
                        comments.add(item!!)
                        println("getMessageList >> ${comments}")


                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getBoolean() {
        databaseReference.child(uid.toString()).orderByChild("users/otherUserUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {
                        println("getBoolean, item.key >> ${item.key}")
                        println("getBoolean, chatRoomUid >> ${chatRoomUid}")
//
//                        val chatModel = item.getValue<ChatModel>()
//                        if( chatModel?.users!!.containsKey("otherUserUid") ) {
//                            println(item.value)
//                        }

                        when {
                            chatRoomUid.equals(item.key) -> {
                                val value = snapshot.child("otherUserUid")
                                println("getBoolean, value >> ${value}")

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

}