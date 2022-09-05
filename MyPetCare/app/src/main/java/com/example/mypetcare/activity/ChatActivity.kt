package com.example.mypetcare.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.R
import com.example.mypetcare.adapter.ChatAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.ActivityChatBinding
import com.example.mypetcare.dto.ChatModel
import com.example.mypetcare.dto.ChatUserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding : ActivityChatBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chat")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var userName: String
    private lateinit var chatAdapter: ChatAdapter
    private var chatRoomUid: String? = null
    private val chatList: ArrayList<ChatUserDTO> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = PreferenceManager.getString(this, "userName")

        // 리사이클러뷰 설정
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = ChatAdapter(chatRoomUid.toString())
        binding.chatRecyclerView.adapter = chatAdapter

        // 입력창이 공백일 경우 버튼 비활성화
        binding.chatMessage.addTextChangedListener { text ->
            binding.chatSend.isEnabled = text.toString() != ""
        }



        binding.chatSend.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view?.id) {

            // 메시지 전송
            R.id.chat_send -> {
                sendMessage()
            }
        }
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        chatList.add(ChatUserDTO(uid, "${userName}님", "time"))
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val value = snapshot.value
//                println("snapshot value >> ${value}")
//
//                val now : Long = System.currentTimeMillis()
//                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
//                val time = dateFormat.format(now)
//
//                val item = ChatUserDTO(uid, userName, time)
//                chatList.add(item)
//                chatAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                println("Failed to read value")
//            }
//        })
//    }


    private fun sendMessage() {

        val chatModel = ChatModel()
        chatModel.users.put(uid.toString(), true)
        chatModel.users.put("otherUserUid", true)

        val message = binding.chatMessage.text.toString()

        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
        val currentTime = dateFormat.format(now)

        val comment = ChatModel.Comment(uid.toString(), userName, message, currentTime)

        // 채팅방 존재 확인
        if( chatRoomUid == null ) {
            databaseReference.child(uid.toString())
                                .push()
                                .setValue(chatModel)
                                .addOnSuccessListener {
                                // 채팅방 생성
                                createChatRoom()
                            }
        }

        // 메시지 보내기
        databaseReference.child(uid.toString()).child("comments").push().setValue(comment)
        binding.chatMessage.setText("")

        // 메시지를 보내면 화면을 맨 아래로 내림
        binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    // 채팅방 생성
    /*
     * addListenerForSingleValueEvent: 로컬 디스크 캐시에서 데이터를 즉시 가져온다.
     * 한 번 로드된 후 자주 변경되지 않거나 수신 대기할 필요가 없는 데이터에 유용.
     */
    private fun createChatRoom() {
        databaseReference.child(uid.toString()).orderByChild("users/$uid").equalTo(true)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for( item in snapshot.children ) {
                                    println("createChatRoom, item >> ${item}")

                                    val chatModel = item.getValue<ChatModel>()
                                    if( chatModel?.users!!.containsKey("otherUserUid") ) {
                                        chatRoomUid = item.key

                                        binding.chatRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                                        chatAdapter = ChatAdapter(chatRoomUid.toString())
                                        binding.chatRecyclerView.adapter = chatAdapter
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
    }

}