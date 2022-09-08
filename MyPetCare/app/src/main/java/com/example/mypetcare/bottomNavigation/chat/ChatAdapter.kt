package com.example.mypetcare.bottomNavigation.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ChatMessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class ChatAdapter(private val roomUid: String): BaseAdapter() {

    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chatRoom")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private val messageList = ArrayList<ChatMessageData>()


    init {
        databaseReference.orderByChild("member/$uid").equalTo(true)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    getMessageList()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
    }



    override fun getCount(): Int {
        return messageList.size
    }

    override fun getItem(position: Int): Any {
        return messageList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = messageList[position]
        val view: View?
        // 내가 보낸 메시지일 경우
        if( item.uid == uid )
            view = LayoutInflater.from(parent?.context).inflate(R.layout.my_message_box, parent, false)
        else
            view = LayoutInflater.from(parent?.context).inflate(R.layout.your_message_box, parent, false)

        val text_name = view.findViewById<TextView>(R.id.chatting_name)
        val text_message = view.findViewById<TextView>(R.id.chatting_message)

        text_name.text = item.name
        text_message.text = item.message

        return view
    }

    private fun getMessageList() {
        // ValueEventListener 는 하나의 값이 바뀌어도 전체를 새로 주고
        // ChildEventListener 는 새로 추가된 값만 읽어온다.
        databaseReference.child(roomUid).child("comments")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val item = snapshot.getValue<ChatMessageData>()
                    messageList.add(item!!)
                    notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}