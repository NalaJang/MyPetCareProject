package com.example.mypetcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.MessageBoxViewType
import com.example.mypetcare.R
import com.example.mypetcare.dto.ChatMessageData
import com.example.mypetcare.dto.ChatModel
import com.example.mypetcare.dto.ChatUserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatAdapter(val roomUid: String): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chatRoom")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private val messageList = ArrayList<ChatMessageData>()
    private var chatUser: ChatMessageData? = null

    // member 에서
    init {
        databaseReference.orderByChild("member/$uid").equalTo(true)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    chatUser = snapshot.getValue<ChatMessageData>()
                                    println("ChatAdapter, userName >> ${chatUser?.uid}")

                                    getMessageList()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("onCreateViewHolder, viewType >> ${viewType}")

        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_box, parent, false)

        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if( currentUser == itemList[position].userName ) {
//            holder.chattingMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
//        }

        holder.yourName.text = messageList[position].name
        holder.yourMessage.text = messageList[position].message
        holder.yourTime.text = messageList[position].time
    }

    override fun getItemCount(): Int {
        return messageList.size
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
        databaseReference.child(roomUid).child("comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for( data in snapshot.children ) {
                        val item = data.getValue<ChatMessageData>()
                        messageList.add(item!!)
                        println("roomUid >> ${roomUid}")
                        println("data.key >> ${data.key}")
                        println("item.uid >> ${item.uid}")
                        println("getMessageList >> ${messageList}")

                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}