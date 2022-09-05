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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class RoomListAdapter2: RecyclerView.Adapter<RoomListAdapter2.ViewHolder>() {

    private var databaseReference = FirebaseDatabase.getInstance().reference
    private val chatModel = ArrayList<ChatModel>()
    private val userList: ArrayList<String> = arrayListOf()
    private var uid: String? = null

    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        println("RoomListAdapter")

        databaseReference.child("chat").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()

                    for( data in snapshot.children ) {
                        chatModel.add(data.getValue<ChatModel>()!!)
                        println("RoomListAdapter" + data)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var otherUserUid: String? = null

        for( user in chatModel[position].users.keys ) {
            if( !user.equals(uid) ) {
                otherUserUid = user
                userList.add(otherUserUid)
            }
        }

        databaseReference.child("users").child("$otherUserUid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val otherUser = snapshot.getValue<ChatUserDTO>()
                    holder.setText.text = otherUser?.userName
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, ChatActivity::class.java)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return chatModel.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val setText = itemView.findViewById<TextView>(R.id.roomListAdapter_text)
    }
}