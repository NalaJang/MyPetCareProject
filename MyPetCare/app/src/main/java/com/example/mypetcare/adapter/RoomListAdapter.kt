package com.example.mypetcare.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.R
import com.example.mypetcare.dto.ChatModel
import com.example.mypetcare.dto.ChatUserDTO
import com.example.mypetcare.activity.ChatActivity
import com.example.mypetcare.dto.ChatData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class RoomListAdapter: RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {

    private var databaseReference = FirebaseDatabase.getInstance().getReference("chatRoom")
    private val dataList: ArrayList<ChatData> = arrayListOf()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var chatData: ChatData? = null

    init {
        getMessageList()


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData: ChatData = dataList.get(position)
        println("onBindViewHolder,  ${chatData.message}")
        holder.name.text = chatData.message
        holder.time.text = chatData.time


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getChatData(position: Int): ChatData {
        return dataList.get(position)
    }

    fun addChatData(chat: ChatData) {
        dataList.add(chat)
        notifyItemInserted(dataList.size -1)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.roomListAdapter_text)
        val time = itemView.findViewById<TextView>(R.id.roomListAdapter_time)
    }

    private fun getMessageList() {
        databaseReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()

                    for( data in snapshot.children ) {
                        val item = data.getValue<ChatData>()
                        dataList.add(item!!)
                        println("getMessageList >> ${dataList}")
                        println("getMessageList, uid >> ${item.uid}")
                        println("getMessageList >> ${dataList}")

                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}