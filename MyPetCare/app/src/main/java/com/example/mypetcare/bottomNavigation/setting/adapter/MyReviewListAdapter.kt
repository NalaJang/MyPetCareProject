package com.example.mypetcare.bottomNavigation.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyReviewListAdapter: BaseAdapter() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val reviewList = ArrayList<ReviewData>()

    init {
        getReviewList()
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

        return view
    }

    private fun getReviewList() {
        db.collection(Constants.REVIEW_COMMENT)
            .document(uid.toString())
            .collection(Constants.REVIEW_COMMENT)
            .get()
            .addOnCompleteListener { task ->

                reviewList.clear()

                if( task.isSuccessful ) {
                    for( i in task.result!! ) {

                        val userName = i.data["userName"].toString()
                        val managerName = i.data["managerName"].toString()
                        val writingTime = i.data["writingTime"].toString()
                        val content = i.data["content"].toString()
                        reviewList.add(ReviewData(uid, userName, managerName, writingTime, content))
                    }

                }
                notifyDataSetChanged()
            }
    }

//    private fun deleteReview(position_managerList: String, position_userList: String) {
//
//        // 매니저에게서 삭제
//        db.collection(Constants.REVIEW)
//            .document(manager)
//            .collection(Constants.REVIEW)
//            .document(position_managerList)
//            .delete()
//            .addOnCompleteListener {
//                if( it.isSuccessful )
//                    println("삭제 성공")
//            }
//
//        // 사용자에게서 삭제
//        db.collection(Constants.REVIEW)
//            .document(userUid.toString())
//            .collection(Constants.REVIEW)
//            .document(position_userList)
//            .delete()
//            .addOnCompleteListener {
//                if( it.isSuccessful )
//                    println("삭제 성공")
//            }
//
//        notifyDataSetChanged()
//    }
}