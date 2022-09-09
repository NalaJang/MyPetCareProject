package com.example.mypetcare.bottomNavigation.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewData

class ReviewListAdapter: BaseAdapter() {

    private val itemList = ArrayList<ReviewData>()

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_my_review_list, parent, false)

        return view
    }
}