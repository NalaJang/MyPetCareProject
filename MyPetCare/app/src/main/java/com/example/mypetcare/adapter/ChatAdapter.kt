package com.example.mypetcare.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.R
import com.example.mypetcare.dto.ChatDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatAdapter(val context: Context, val currentUser: String, val itemList: ArrayList<ChatDTO>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chat")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var item: ChatDTO? = null

    init {
        databaseReference.child("chat")
                            .child(uid.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    item = snapshot.getValue<ChatDTO>()
                                    println("ChatAdapter, userName >> ${item?.userName}")
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.your_message_box, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if( currentUser == itemList[position].userName ) {
            holder.chattingMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        }
        holder.chattingId.text = itemList[position].userName
        holder.chattingMessage.text = itemList[position].message
        holder.chattingTime.text = itemList[position].time
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val layout = itemView.findViewById<LinearLayout>(R.id.chatting_layout)
        val chattingId = itemView.findViewById<TextView>(R.id.chatting_id)
        val chattingMessage = itemView.findViewById<TextView>(R.id.chatting_message)
        val chattingTime = itemView.findViewById<TextView>(R.id.chatting_time)

    }

    private fun getMessageList() {

    }
}