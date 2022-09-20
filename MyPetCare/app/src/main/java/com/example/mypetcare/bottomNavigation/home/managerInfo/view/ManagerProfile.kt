package com.example.mypetcare.bottomNavigation.home.managerInfo.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.chat.view.ChatActivity
import com.example.mypetcare.bottomNavigation.home.managerInfo.adapter.ReviewListAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.database.dto.ChatModel
import com.example.mypetcare.database.dto.ReviewModel
import com.example.mypetcare.databinding.DialogManagerProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ResourceType")
class ManagerProfile constructor(context: Context, managerUid: String):
    Dialog(context, R.drawable.dialog_full_screen), View.OnClickListener {

    private var mBinding: DialogManagerProfileBinding? = null
    private val binding get() = mBinding!!
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val databaseReference_review = FirebaseDatabase.getInstance().getReference(Constants.REVIEWS)
    private val databaseReference_chat = FirebaseDatabase.getInstance().getReference(Constants.CHAT_ROOM)
    private var reviewUid: String? = null
    private var chatRoomUid: String? = null
    private val mManagerUid = managerUid
    private var managerName: String? = null
    private val intent = Intent(context, ChatActivity::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogManagerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 매니저 프로필 가져오기
        getManagerProfile()

        // adapter 설정
        initAdapter()

        // roomUid 가져오기
        getChatRoomUid()

        // reviewUid 가져오기
        getReviewUid()


        binding.managerClose.setOnClickListener(this)
        binding.managerStartChat.setOnClickListener(this)
        binding.managerWriteReviewButton.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            // 닫기
            R.id.manager_close -> dismiss()

            // 채팅 시작
            R.id.manager_startChat -> startChat()

            // 후기 작성
            R.id.manager_writeReviewButton -> writeReview()
        }
    }

    // 매니저 프로필 가져오기
    private fun getManagerProfile() {
        db.collection(Constants.MANAGER_INFO)
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == mManagerUid ) {

                            managerName = i.data["managerName"].toString()
                            val possibleWork = i.data["possibleWork"].toString()
                            val introduce = i.data["introduce"].toString()

                            binding.managerName.text = managerName
                            binding.managerPossibleWork.text = possibleWork
                            binding.managerIntroduce.text = introduce
                        }
                    }
                }
            }
    }

    // adapter 설정
    private fun initAdapter() {
        // recyclerview 에는 layoutManager 설정 필요
        val layoutManager = LinearLayoutManager(context)
        val reviewAdapter = ReviewListAdapter(mManagerUid)
        binding.managerReviewList.layoutManager = layoutManager
        binding.managerReviewList.adapter = reviewAdapter
    }

    private fun startChat() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userName = PreferenceManager.getString(context, "userName")

        val comment = ChatModel.Comment()
        comment.uid = uid.toString()
        comment.userName = userName
        comment.managerName = managerName
        comment.message = ""
        comment.time = ""

        // 선택한 사용자와의 채팅방이 없다면 chatRoomUid 를 새로 생성
        if( chatRoomUid == null ) {
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(), true) // 사용자 uid
            chatModel.users.put(mManagerUid, true)    // 매니저 uid

            // setValue 를 통해 roomUid 생성
            databaseReference_chat.push().setValue(chatModel).addOnSuccessListener {

                getChatRoomUid()

                Handler().postDelayed({
                    databaseReference_chat.child(chatRoomUid.toString())
                                            .child(Constants.CHAT_COMMENTS)
                                            .push()
                                            .setValue(comment)

                    intent.putExtra("roomUid", chatRoomUid)
                    intent.putExtra("managerName", managerName)
                    context.startActivity(intent)

                }, 1000L)
            }

        } else {
            intent.putExtra("roomUid", chatRoomUid)
            intent.putExtra("managerName", managerName)
            context.startActivity(intent)
        }
    }

    // roomUid 가져오기
    private fun getChatRoomUid() {
        databaseReference_chat.orderByChild("users/${uid}").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {

                        val chatModel = item.getValue<ChatModel>()
                        if( chatModel?.users!!.containsKey(mManagerUid) ) {
                            chatRoomUid = item.key
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    // 리뷰 작성
    private fun writeReview() {

        val userName = PreferenceManager.getString(context, "userName")
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
        val writingTime = dateFormat.format(Date(now))
        val content = binding.managerReviewContent.text.toString()

        val review = ReviewModel.Comment(uid, userName, managerName, writingTime, content)

        // reviewUid 가 없을 경우 새로 생성
        if( reviewUid == null ) {

            val reviewModel = ReviewModel()
            reviewModel.users.put(mManagerUid, true)
            reviewModel.users.put(uid.toString(), true)

            databaseReference_review.push().setValue(reviewModel).addOnSuccessListener {

                // setValue() 후 새로 생성된 reviewUid 를 가져온다.
                getReviewUid()

                // reviewUid 생성이 되면 1초 딜레이 후 review data 를 넣어준다.
                // 딜레이 -> reviewUid = null 을 방지하기 위함.
                Handler().postDelayed({
                    databaseReference_review.child(reviewUid.toString()).child(Constants.REVIEW_COMMENT).push().setValue(review)
                    initAdapter()

                }, 1000L)
            }
        }
        else
            databaseReference_review.child(reviewUid.toString()).child(Constants.REVIEW_COMMENT).push().setValue(review)

        binding.managerReviewContent.setText("")
    }

    // reviewUid 가져오기
    private fun getReviewUid() {
        // 데이터를 선택적으로 검색하기 전에 먼저 정렬 함수(orderByChild)로 정렬 방법을 지정한다.
        databaseReference_review.orderByChild("users/${uid}").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {

                        val reviewModel = item.getValue<ReviewModel>()
                        if( reviewModel?.users!!.containsKey(mManagerUid) ) {
                            reviewUid = item.key
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}