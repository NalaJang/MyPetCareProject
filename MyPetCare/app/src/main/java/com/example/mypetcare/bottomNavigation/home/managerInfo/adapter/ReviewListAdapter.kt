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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReviewListAdapter(managerUid: String): RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val reviewList = ArrayList<ReviewData>()
    private val reviewUidForManagerList = ArrayList<String>()
    private val reviewUidForUserList = ArrayList<String>()
    private val manager = managerUid

    init {
        // 리뷰 리스트 가져오기
        getReviewListFromManagerUid()
        getReviewListFromUserUid()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.manager_review_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, position: Int) {

        val item = reviewList[position]
        holder.setItem(item)

        if( userUid == item.uid ) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener{
                deleteReview(reviewUidForManagerList[position], reviewUidForUserList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.managerReview_userName)
        private val content: TextView = view.findViewById(R.id.managerReview_content)
        private val writingTime: TextView = view.findViewById(R.id.managerReview_writingTime)
        val deleteButton: Button = view.findViewById(R.id.managerReview_delete)

        fun setItem(item: ReviewData) {
            userName.text = item.userName
            content.text = item.content
            writingTime.text = item.writingTime
        }
    }

    // 리뷰 리스트 가져오기
    private fun getReviewListFromManagerUid() {
        db.collection(Constants.REVIEW)
            .document(manager)
            .collection(Constants.REVIEW)
            .addSnapshotListener { snapshot, error ->
                reviewList.clear()

                if( error != null )
                    return@addSnapshotListener

                if( snapshot != null ) {
                    for( document in snapshot ) {

                        val reviewUid = document.id
                        val writerUid = document.getString("userUid").toString()
                        val userName = document.getString("userName").toString()
                        val managerName = document.getString("managerName").toString()
                        val content = document.getString("content").toString()
                        val writingTime = document.getString("writingTime").toString()

                        reviewList.add(ReviewData(writerUid, userName, managerName, writingTime, content))
                        reviewUidForManagerList.add(reviewUid)
                    }

                    notifyDataSetChanged()

                } else println("data null")

            }
    }

    // 리뷰 리스트 가져오기
    private fun getReviewListFromUserUid() {
        db.collection(Constants.REVIEW)
            .document(userUid.toString())
            .collection(Constants.REVIEW)
            .addSnapshotListener { snapshot, error ->

                if( error != null )
                    return@addSnapshotListener

                if( snapshot != null ) {
                    for( document in snapshot ) {

                        val reviewUid = document.id

                        reviewUidForUserList.add(reviewUid)
                    }

                    notifyDataSetChanged()

                } else println("data null")

            }
    }

    // 리뷰 삭제
    // 매니저와 사용자 모두에게서 삭제
    private fun deleteReview(position_managerList: String, position_userList: String) {

        // 매니저에게서 삭제
        db.collection(Constants.REVIEW)
            .document(manager)
            .collection(Constants.REVIEW)
            .document(position_managerList)
            .delete()
            .addOnCompleteListener {
                if( it.isSuccessful )
                    println("삭제 성공")
            }

        // 사용자에게서 삭제
        db.collection(Constants.REVIEW)
            .document(userUid.toString())
            .collection(Constants.REVIEW)
            .document(position_userList)
            .delete()
            .addOnCompleteListener {
                if( it.isSuccessful )
                    println("삭제 성공")
            }

        notifyDataSetChanged()
    }
}