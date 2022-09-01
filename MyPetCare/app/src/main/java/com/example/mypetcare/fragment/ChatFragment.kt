package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.R
import com.example.mypetcare.adapter.ChatAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.FragmentChatBinding
import com.example.mypetcare.dto.ChatDTO
import com.example.mypetcare.dto.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalTime.now
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment(), View.OnClickListener {

    private var mBinding : FragmentChatBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var database = FirebaseDatabase.getInstance()
    private var databaseReference = database.getReference("chat")
    private lateinit var registration: ListenerRegistration
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var userName: String
    private lateinit var chatAdapter: ChatAdapter
    private val chatList: ArrayList<ChatDTO> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userName = PreferenceManager.getString(context, "userName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentChatBinding.inflate(inflater, container, false)
//        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 리사이클러뷰 설정
        binding.chatList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        chatAdapter = ChatAdapter(requireContext(), userName, chatList)
        binding.chatList.adapter = chatAdapter

        // 입력창이 공백일 경우 버튼 비활성화
        binding.chatMessage.addTextChangedListener { text ->
            binding.chatSend.isEnabled = text.toString() != ""
        }



        binding.chatSend.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.chat_send -> {
                sendMessage2()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatList.add(ChatDTO(uid, "${userName}님", "입장", ""))
        val enterTime = Date(System.currentTimeMillis())

//        registration = db.collection("chat")
//            .orderBy("time", Query.Direction.DESCENDING)
//            .limit(1)
//            .addSnapshotListener { snapshots, error ->
//                if( error != null)
//                    return@addSnapshotListener
//
//                // 원치 않는 문서 무시
//                if( snapshots!!.metadata.isFromCache)
//                    return@addSnapshotListener
//
//                // 문서 수신
//                for( doc in snapshots.documentChanges ) {
//                    val timestamp = doc.document["time"] as com.google.firebase.Timestamp
//
//                    // 문서가 추가될 경우 리사이클러뷰에 추가
//                    if( doc.type == DocumentChange.Type.ADDED
//                        && timestamp.toDate() > enterTime ) {
//                        val userName = doc.document["userName"].toString()
//                        val message = doc.document["message"].toString()
//
//                        // 타임스탬프를 한국 시간, 문자열로 변환
//                        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
//                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
//                        val time = dateFormat.format(timestamp.toDate())
//
//                        val item = ChatDTO(uid, userName, message, time)
//                        chatList.add(item)
//                    }
//                    chatAdapter.notifyDataSetChanged()
//                }
//            }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                println("snapshot value >> ${value}")

                val now : Long = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
                val time = dateFormat.format(now)

                val item = ChatDTO(uid, userName, value.toString(), time)
                chatList.add(item)
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value")
            }
        })
    }


    private fun sendMessage() {
        println("userName >> ${userName}")

        val sendData = hashMapOf(
            "uid" to uid,
            "userName" to userName,
            "message" to binding.chatMessage.text.toString(),
            "time" to com.google.firebase.Timestamp.now()
        )

        // Firestore 에 전송
        db.collection("chat")
            .add(sendData)
            .addOnSuccessListener {
                binding.chatMessage.text.clear()
                println("성공")
            }
            .addOnFailureListener {
                println("실패")

            }
    }

    private fun sendMessage2() {
        println("sendMessage2")

//        databaseReference.child(uid!!).setValue(binding.chatMessage.text.toString())
//        binding.chatMessage.setText("")

        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
        val time = dateFormat.format(now)

        val chatModel = ChatModel()
        chatModel.users.put(uid.toString(), true)
        chatModel.users.put("otherUid", true)

        val message = binding.chatMessage.text.toString()
        val chatList = ChatDTO(uid, userName, message, time)
        databaseReference.child(uid.toString()).child("comments").push().setValue(chatList)
        binding.chatMessage.setText("")
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        registration.remove()
    }

}