package com.example.mypetcare.bottomNavigation.home.managerInfo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.managerInfo.adapter.ReviewListAdapter
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.DialogManagerProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ResourceType")
class ManagerProfile constructor(context: Context, managerUid: String): Dialog(context, R.drawable.dialog_full_screen) {

    private var mBinding: DialogManagerProfileBinding? = null
    private val binding get() = mBinding!!
    private val db = FirebaseFirestore.getInstance()
    private val manager = managerUid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogManagerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 매니저 프로필 가져오기
        getManagerProfile()

        // adapter 설정
        initAdapter()


        binding.managerClose.setOnClickListener{ dismiss() }
        binding.managerComposeButton.setOnClickListener{ writeAReview() }
    }

    // 매니저 프로필 가져오기
    private fun getManagerProfile() {
        db.collection(Constants.MANAGER_INFO)
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == manager ) {
                            println("getManagerProfile")

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
        val reviewAdapter = ReviewListAdapter(manager)
        binding.managerReviewList.layoutManager = layoutManager
        binding.managerReviewList.adapter = reviewAdapter
    }

    // 리뷰 작성
    // 매니저와 사용자 모두에게 등록되도록.
    private fun writeAReview() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val userName = PreferenceManager.getString(context, "userName")
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
        val writingTime = dateFormat.format(Date(now))

        val map = mutableMapOf<String, Any>()
        map["userName"] = userName
        map["userUid"] = userUid.toString()
        map["managerName"] = binding.managerName.text.toString()
        map["writingTime"] = writingTime
        map["content"] = binding.managerReviewContent.text.toString()

        // 매니저에게 등록
        db.collection(Constants.REVIEW)
            .document(manager)
            .collection(Constants.REVIEW)
            .add(map)
            .addOnSuccessListener {
                println("writeAReview 성공")
            }


        // 사용자에게 등록
        db.collection(Constants.REVIEW)
            .document(userUid.toString())
            .collection(Constants.REVIEW)
            .add(map)
            .addOnSuccessListener {
                println("writeAReview 성공")
            }

        binding.managerReviewContent.setText("")
    }
}