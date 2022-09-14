package com.example.mypetcare.bottomNavigation.home.managerInfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewData
import com.example.mypetcare.database.dto.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class ReviewListAdapter(managerUid: String, reviewUid: String): RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference(Constants.REVIEWS)
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val reviewList = ArrayList<ReviewModel.Comment>()
    private val reviewUidList = ArrayList<String>()
    private val mManagerUid = managerUid
    private val mReviewUid = reviewUid

    init {
        println("mManagerUid >> ${mManagerUid}")
        println("mReviewUid >> ${mReviewUid}")

        databaseReference.orderByChild("users/$userUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    println("들어옴")
                    for (item in snapshot.children) {

                        reviewList.clear()

                        val key = item.key.toString()
                        println("key >> $key")

                        // 리뷰 리스트 가져오기
                        getReviewList(key)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manager_review_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, position: Int) {

        val item = reviewList[position]
        holder.setItem(item)

        // 로그인한 사람과 작성자 uid 비교
        if( userUid == item.uid ) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener{
                // 리뷰 삭제
                deleteReview(reviewUidList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val userName: TextView = view.findViewById(R.id.managerReview_userName)
        private val content: TextView = view.findViewById(R.id.managerReview_content)
        private val writingTime: TextView = view.findViewById(R.id.managerReview_writingTime)
        val deleteButton: Button = view.findViewById(R.id.managerReview_delete)

        fun setItem(item: ReviewModel.Comment) {
            userName.text = item.userName
            content.text = item.content
            writingTime.text = item.writingTime
        }
    }

    // 리뷰 리스트 가져오기
    private fun getReviewList(key: String) {
        databaseReference.child(key).child(Constants.REVIEW_COMMENT)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( data in snapshot.children ) {

                        val item = data.getValue<ReviewModel.Comment>()
                        val writerUid = item?.uid.toString()
                        val userName = item?.userName.toString()
                        val managerName = item?.managerName.toString()
                        val writingTime = item?.writingTime.toString()
                        val content = item?.content.toString()
                        val reviewUid = data.key.toString()

                        reviewList.add(ReviewModel.Comment(
                            writerUid, userName, managerName, writingTime, content
                        ))
                        reviewUidList.add(reviewUid)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
//            .addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//
//                    val item = snapshot.getValue<ReviewModel.Comment>()
//                    reviewList.add(item!!)
//                    val reviewUid = item.uid.toString()
//                    println("getReviewList, reviewUid >> ${reviewUid}")
//
////                    for( data in snapshot.children ) {
////
////                        val item = data.getValue<ReviewModel.Comment>()
////                        val writerUid = item?.uid.toString()
////                        val userName = item?.userName.toString()
////                        val managerName = item?.managerName.toString()
////                        val writingTime = item?.writingTime.toString()
////                        val content = item?.content.toString()
////                        val reviewUid = data.key.toString()
////
////                        reviewList.add(ReviewModel.Comment(
////                            writerUid, userName, managerName, writingTime, content))
////
//                        reviewUidList.add(reviewUid)
////                    }
//                    notifyDataSetChanged()
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
    }


    // 리뷰 삭제
    private fun deleteReview(selectedReview: String) {
        databaseReference.orderByChild("users/$userUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {
                        val reviewModel = item.getValue<ReviewModel>()

                        if( reviewModel?.users!!.containsKey(mManagerUid) ) {
                            println("deleteReview, item.key >> ${item.key}")
                            println("deleteReview, reviewUidList >> $reviewUidList")
                            val commentKey = item.key.toString()

                            // 리뷰 value null 설정
                            setReviewValueNull(commentKey, selectedReview)
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setReviewValueNull(commentKey: String, selectedReview: String) {
        databaseReference.child(commentKey).child(Constants.REVIEW_COMMENT).child(selectedReview).setValue(null)
    }
}