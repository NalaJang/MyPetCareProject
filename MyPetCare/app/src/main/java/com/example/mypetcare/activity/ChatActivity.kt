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
import com.example.mypetcare.dto.ChatData
import com.example.mypetcare.dto.ChatMessageData
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
    private var databaseReference = database.getReference("chatRoom")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var userName: String
    private lateinit var chatAdapter: ChatAdapter
    private var chatRoomUid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = PreferenceManager.getString(this, "userName")

        if( intent.hasExtra("roomUid") ) {
            chatRoomUid = intent.getStringExtra("roomUid")
        }


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


    private val test1_uid = "eX5wB65N4SNlEFbKbhnfgSVcuYA2"
    private val test3_uid = "3uHDMtqIEMW01Qi8u7a3dAgvEXp2"

    private fun sendMessage() {

        val message = binding.chatMessage.text.toString()

        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
        val currentTime = dateFormat.format(now)

        val chatMessage = ChatMessageData(uid.toString(), userName, message, currentTime)

        // 메시지 보내기
        databaseReference.child(chatRoomUid!!).child("comments").push().setValue(chatMessage)
        binding.chatMessage.setText("")

        // 메시지를 보내면 스크롤을 맨 아래로 내림
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