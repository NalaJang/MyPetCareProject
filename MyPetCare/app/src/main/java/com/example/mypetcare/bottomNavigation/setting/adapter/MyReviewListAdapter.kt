package com.example.mypetcare.bottomNavigation.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.mypetcare.database.Constants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class MyReviewListAdapter: BaseAdapter() {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val databaseReference = FirebaseDatabase.getInstance().getReference(Constants.REVIEWS)
    private val reviewList = ArrayList<ReviewModel.Comment>()
    private val commentUidList = ArrayList<String>()
    private var reviewUid: String? = null

    init {
        databaseReference.orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for( item in snapshot.children ) {
                        println("item.key >> ${item.key}")
                        reviewUid = item.key.toString()

                        // 내가 쓴 리뷰 리스트 가져오기
                        getReviewList(reviewUid!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun getCount(): Int {
        return reviewList.size
    }

    override fun getItem(position: Int): Any {
        return reviewList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_my_review_list, parent, false)

        val managerName: TextView = view.findViewById(R.id.myReview_managerName)
        val content: TextView = view.findViewById(R.id.myReview_content)
        val writingTime: TextView = view.findViewById(R.id.myReview_writingTime)
        val deleteButton: Button = view.findViewById(R.id.myReview_delete)

        val item = reviewList[position]
        managerName.text = item.managerName
        content.text = item.content
        writingTime.text = item.writingTime

        // 리뷰 삭제
        deleteButton.setOnClickListener { deleteReview(commentUidList[position]) }

        return view
    }

    // 내가 쓴 리뷰 리스트 가져오기
    private fun getReviewList(reviewUid: String) {
        databaseReference.child(reviewUid).child(Constants.REVIEW_COMMENT)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reviewList.clear()

                    for( data in snapshot.children ) {
                        val item = data.getValue<ReviewModel.Comment>()
                        println("getReviewList2 >> ${item?.managerName}")

                        val writerUid = item?.uid.toString()
                        val userName = item?.userName.toString()
                        val managerName = item?.managerName.toString()
                        val writingTime = item?.writingTime.toString()
                        val content = item?.content.toString()
                        val commentUid = data.key.toString()

                        reviewList.add(ReviewModel.Comment(
                            writerUid, userName, managerName, writingTime, content
                        ))
                        commentUidList.add(commentUid)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // 리뷰 삭제
    private fun deleteReview(selectedReview: String) {
        databaseReference.child(reviewUid!!)
                        .child(Constants.REVIEW_COMMENT)
                        .child(selectedReview)
                        .setValue(null)

        getReviewList(reviewUid!!)
    }

}