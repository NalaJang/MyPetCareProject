package com.example.mypetcare.bottomNavigation.home.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListAdapter: BaseAdapter() {

    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var scheduleList: ArrayList<UserScheduleDTO> = arrayListOf()

    init {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        // 일정 목록
        getScheduleList(year.toString(), month.toString(), day.toString())
//        getTodaySchedule()
    }

    override fun getCount(): Int {
        return scheduleList.size
    }

    override fun getItem(position: Int): Any {
        return scheduleList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_schedule_list, parent, false)

        val startTime = view.findViewById<TextView>(R.id.scheduleList_startTime)
        val endTime = view.findViewById<TextView>(R.id.scheduleList_endTime)
        val selectedCategory = view.findViewById<TextView>(R.id.scheduleList_selectedCategory)

        val schedule = scheduleList[position]
        startTime.text = schedule.startTime
        endTime.text = schedule.endTime
        selectedCategory.text = schedule.selectedCategory

        return view
    }

    private fun getTodaySchedule() {
        println("getTodaySchedule")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        db.collection(Constants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year.toString())
            .document(month.toString())
            .collection(day.toString())
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    scheduleList.clear()

                    for( i in task.result!! ) {
                        println("i.id >> ${i.id}")

                        val category = i.data["selectedCategory"].toString()
                        val location = i.data["selectedLocation"].toString()
                        val startTime = i.data["startTime"].toString()
                        val endTime = i.data["endTime"].toString()
                        val memo = i.data["memo"].toString()
                        val registrationTime = i.data["registrationTime"].toString()

                        scheduleList.add(UserScheduleDTO(
                                            uid,
                                            category,
                                            location,
                                            startTime,
                                            endTime,
                                            memo,
                                            registrationTime
                                        ))
                    }
                }

                notifyDataSetChanged()
            }
    }

    // 일정 목록
    fun getScheduleList(year: String, month: String, day: String) {

        db.collection(Constants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year)
            .document(month)
            .collection(day)
            .addSnapshotListener { snapshot, error ->

                scheduleList.clear()

                if( error != null )
                    return@addSnapshotListener

                if( snapshot != null ) {

                    for( document in snapshot ) {
                        println("registrationTime >> ${document.getString("registrationTime")}")
                        val category = document.getString("selectedCategory").toString()
                        val location = document.getString("selectedLocation").toString()
                        val startTime = document.getString("startTime").toString()
                        val endTime = document.getString("endTime").toString()
                        val memo = document.getString("memo").toString()
                        val registrationTime = document.getString("registrationTime").toString()

                        scheduleList.add(UserScheduleDTO(
                                                        uid,
                                                        category,
                                                        location,
                                                        startTime,
                                                        endTime,
                                                        memo,
                                                        registrationTime
                        ))

                    }
                    // list clear 후 새로 고침을 해준다.
                    notifyDataSetChanged()

                } else println("data null")
            }
    }

}