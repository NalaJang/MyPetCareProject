package com.example.mypetcare.bottomNavigation.chat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.chat.adapter.RoomListAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.FragmentRoomListBinding
import com.example.mypetcare.database.dto.ChatData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RoomListFragment : Fragment(), View.OnClickListener{

    private var mBinding : FragmentRoomListBinding? = null
    private val binding get() = mBinding!!
    private lateinit var roomListAdapter: RoomListAdapter
    private var roomUid: String? = null

    private val test1_uid = "eX5wB65N4SNlEFbKbhnfgSVcuYA2"
    private val test3_uid = "3uHDMtqIEMW01Qi8u7a3dAgvEXp2"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRoomListBinding.inflate(inflater, container, false)

        initAdapter()

        binding.roomListButton.setOnClickListener(this)


        return binding.root
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.roomList_button -> createRoom()
        }
    }

    private fun initAdapter() {
        binding.roomListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        roomListAdapter = RoomListAdapter()
        binding.roomListRecyclerView.adapter = roomListAdapter
    }

    private fun createRoom() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("chatRoom")
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userName = PreferenceManager.getString(context, "userName")

        val chatData = ChatData()
        chatData.uid = uid.toString()
        chatData.member.put(uid.toString(), true)
        chatData.member.put(test3_uid, true)
        chatData.userName = userName
        chatData.message = "마지막 메시지"
        chatData.time = "마지막 시간"

        // 선택한 사용자와의 채팅방이 없다면 roomUid 를 새로 생성
        if( roomUid == null ) {
            // 데이터를 선택적으로 검색하기 전에 먼저 정렬 함수(orderByChild)로 정렬 방법을 지정한다.
            databaseReference.orderByChild("member/$uid").equalTo(true)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for( item in snapshot.children ) {

                            val result = item.getValue(ChatData::class.java)
                            if( result?.member!!.containsKey(test3_uid) ) {
                                roomUid = item.key
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            chatData.roomUid = roomUid
            databaseReference.push().setValue(chatData)
        }

    }

    override fun onResume() {
        super.onResume()
        // view 가 새로 그려졌을 때
        roomListAdapter.notifyDataSetChanged()
    }

}