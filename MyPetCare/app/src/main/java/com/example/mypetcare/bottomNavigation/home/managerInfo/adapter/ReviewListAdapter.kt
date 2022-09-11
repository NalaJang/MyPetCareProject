package com.example.mypetcare.bottomNavigation.home.managerInfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewData
import com.google.firebase.firestore.FirebaseFirestore

class ReviewListAdapter(managerUid: String): RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val reviewList = ArrayList<ReviewData>()
    private val manager = managerUid

    init {
        getReviewList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.manager_review_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, position: Int) {
        holder.setItem(reviewList[position])
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.managerReview_userName)
        val content: TextView = view.findViewById(R.id.managerReview_content)
        val writingTime: TextView = view.findViewById(R.id.managerReview_writingTime)

        fun setItem(item: ReviewData) {
            userName.text = item.userName
            content.text = item.content
            writingTime.text = item.writingTime
        }
    }

    private fun getReviewList() {
        db.collection(Constants.REVIEW)
            .document(manager)
            .collection(Constants.REVIEW)
            .addSnapshotListener { snapshot, error ->
                reviewList.clear()

                if( error != null )
                    return@addSnapshotListener

                if( snapshot != null ) {
                    for( document in snapshot ) {
                        println("getReviewList >> ${document.getString("userName").toString()}")

                        val userName = document.getString("userName").toString()
                        val managerName = document.getString("managerName").toString()
                        val content = document.getString("content").toString()
                        val writingTime = document.getString("writingTime").toString()

                        reviewList.add(ReviewData(manager, userName, managerName, writingTime, content))
                    }

                    notifyDataSetChanged()

                } else println("data null")

            }
    }
}