package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.R
import com.example.mypetcare.adapter.RoomListAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.FragmentRoomListBinding
import com.example.mypetcare.dto.ChatData
import com.example.mypetcare.dto.ChatModel
import com.example.mypetcare.dto.ChatUserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RoomListFragment : Fragment(){

    private var mBinding : FragmentRoomListBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var databaseReference = FirebaseDatabase.getInstance().getReference("chatRoom")
    private lateinit var roomListAdapter: RoomListAdapter
    private lateinit var userName: String
    private val dataList: ArrayList<ChatData> = arrayListOf()


    // test1 의 UID : eX5wB65N4SNlEFbKbhnfgSVcuYA2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRoomListBinding.inflate(inflater, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        userName = PreferenceManager.getString(context, "userName")

        initRecyclerView()

        initChatListDB()

        binding.roomListButton.setOnClickListener {
            val chatData = ChatData()
            chatData.uid = uid.toString()
            chatData.message = "메시지"
            chatData.time = "time"

            databaseReference.push().setValue(chatData)
        }

        return binding.root
    }


    private fun initRecyclerView() {
        binding.roomListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        roomListAdapter = RoomListAdapter()
        binding.roomListRecyclerView.adapter = roomListAdapter
    }

    private fun initChatListDB() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                println("initChatListDB")

                for( data in snapshot.children ) {
                    val result: ChatData? = data.getValue(ChatData::class.java)
                    val key = data.value
                    result!!.message = key.toString()
                    dataList.add(result)
                }
                roomListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        // view 가 새로 그려졌을 때
        roomListAdapter.notifyDataSetChanged()
    }
}