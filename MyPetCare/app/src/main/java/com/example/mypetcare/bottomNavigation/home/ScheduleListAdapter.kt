package com.example.mypetcare.bottomNavigation.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListAdapter(val context: Context, val itemList: ArrayList<UserScheduleDTO>): BaseAdapter() {

    private var db: FirebaseFirestore? = null
    private var uid: String? = null

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_schedule_list, null)

        val startTime = view.findViewById<TextView>(R.id.scheduleList_startTime)
        val endTime = view.findViewById<TextView>(R.id.scheduleList_endTime)
        val selectedCategory = view.findViewById<TextView>(R.id.scheduleList_selectedCategory)

        val schedule = itemList[position]
        startTime.text = schedule.startTime
        endTime.text = schedule.endTime
        selectedCategory.text = schedule.selectedCategory

        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        getScheduleList()

        return view
    }

    private fun getScheduleList() {
    println("getScheduleList")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        db?.collection("userSchedule")
            ?.orderBy("schedule")
            ?.addSnapshotListener { snapshot, error ->
                itemList.clear()

                if( snapshot == null)
                    return@addSnapshotListener

                for( data in snapshot!!.documents ) {
                    var item = data.toObject(UserScheduleDTO::class.java)
                    itemList.add(item!!)

                }
                notifyDataSetChanged()
            }
    }

    private fun getScheduleList2() {
        db?.collection("userSchedule")
    }

}