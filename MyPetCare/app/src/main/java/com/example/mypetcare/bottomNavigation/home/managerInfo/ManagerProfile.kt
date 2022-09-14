package com.example.mypetcare.bottomNavigation.home.managerInfo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.managerInfo.adapter.ReviewListAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.database.dto.ReviewModel
import com.example.mypetcare.databinding.DialogManagerProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ResourceType")
class ManagerProfile constructor(context: Context, managerUid: String): Dialog(context, R.drawable.dialog_full_screen) {

    private var mBinding: DialogManagerProfileBinding? = null
    private val binding get() = mBinding!!
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val databaseReference = FirebaseDatabase.getInstance().getReference(Constants.REVIEWS)
    private var reviewUid: String? = null
    private val mManager = managerUid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogManagerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 매니저 프로필 가져오기
        getManagerProfile()

        // adapter 설정
        initAdapter()

        // reviewUid 가져오기
        getReviewUid()

        binding.managerClose.setOnClickListener{ dismiss() }
        binding.managerComposeButton.setOnClickListener{ writeReview() }
    }

    // 매니저 프로필 가져오기
    private fun getManagerProfile() {
        db.collection(Constants.MANAGER_INFO)
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == mManager ) {

                            val name = i.data["managerName"].toString()
                            val possibleWork = i.data["possibleWork"].toString()
                            val introduce = i.data["introduce"].toString()

                            binding.managerName.text = name
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
        val reviewAdapter = ReviewListAdapter(mManager, reviewUid.toString())
        binding.managerReviewList.layoutManager = layoutManager
        binding.managerReviewList.adapter = reviewAdapter
    }

    // 리뷰 작성
    private fun writeReview() {

        val userName = PreferenceManager.getString(context, "userName")
        val managerName = binding.managerName.text.toString()
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
        val writingTime = dateFormat.format(Date(now))
        val content = binding.managerReviewContent.text.toString()

        val review = ReviewModel.Comment(uid, userName, managerName, writingTime, content)

        // reviewUid 가 없을 경우 새로 생성
        if( reviewUid == null ) {

            val reviewModel = ReviewModel()
            reviewModel.users.put(mManager, true)
            reviewModel.users.put(uid.toString(), true)

            databaseReference.push().setValue(reviewModel).addOnSuccessListener {

                // setValue() 후 새로 생성된 reviewUid 를 가져온다.
                getReviewUid()

                // reviewUid 생성이 되면 1초 딜레이 후 review data 를 넣어준다.
                // 딜레이 -> reviewUid = null 을 방지하기 위함.
                Handler().postDelayed({
                    databaseReference.child(reviewUid.toString()).child(Constants.REVIEW_COMMENT).push().setValue(review)

                }, 1000L)
            }
        }
        else
            databaseReference.child(reviewUid.toString()).child(Constants.REVIEW_COMMENT).push().setValue(review)


        binding.managerReviewContent.setText("")
    }

    // reviewUid 가져오기
    private fun getReviewUid() {
        databaseReference.orderByChild("users/${uid}").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {

                        val reviewModel = item.getValue<ReviewModel>()
                        if( reviewModel?.users!!.containsKey(mManager) ) {
                            reviewUid = item.key
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}