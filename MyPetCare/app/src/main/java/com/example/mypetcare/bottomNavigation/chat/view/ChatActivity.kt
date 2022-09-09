package com.example.mypetcare.bottomNavigation.chat.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.chat.adapter.ChatAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.ActivityChatBinding
import com.example.mypetcare.database.dto.ChatMessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding : ActivityChatBinding? = null
    private val binding get() = mBinding!!
    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chatRoom")
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var userName: String
    private lateinit var chatAdapter: ChatAdapter
    private var roomUid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = PreferenceManager.getString(this, "userName")

        if( intent.hasExtra("roomUid") ) {
            roomUid = intent.getStringExtra("roomUid")
        }

        // adapter 설정
        initAdapter()


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

    // adapter 설정
    private fun initAdapter() {
        chatAdapter = ChatAdapter(roomUid.toString())
        binding.chatListView.adapter = chatAdapter
    }

    // 메시지 전송
    private fun sendMessage() {

        val message = binding.chatMessage.text.toString()

        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
        val currentTime = dateFormat.format(now)

        val chatMessage = ChatMessageData(uid.toString(), userName, message, currentTime)

        // 메시지 보내기
        databaseReference.child(roomUid!!).child(Constants.CHAT_COMMENTS).push().setValue(chatMessage)
        binding.chatMessage.setText("")

        // 메시지를 보내면 스크롤을 맨 아래로 내림
//        binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount)
        binding.chatListView.setSelection(messageList.size -1)
    }

    private val messageList = ArrayList<ChatMessageData>()

}