package com.example.mypetcare.bottomNavigation.home.schedule.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.schedule.adapter.ScheduleListAdapter
import com.example.mypetcare.databinding.DialogCalendarBinding
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@SuppressLint("ResourceType")
class CalendarDialog constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, CalendarView.OnDateChangeListener {

    private var mBinding: DialogCalendarBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var scheduleAdapter: ScheduleListAdapter? = null

    init {
        setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.calendarDialogSelectedDate.text = "오늘"

        // listAdapter 설정
        initAdapter()

        // 달력에서 선택한 날짜
        binding.calendarDialogCalendarView.setOnDateChangeListener(this)
        binding.calendarClose.setOnClickListener(this)
        binding.calendarDialogApplyButton.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.calendar_close -> dismiss()

            // 신청하기
            R.id.calendarDialog_applyButton -> {
                val applyDate: String = binding.calendarDialogSelectedDate.text.toString()
                val applyDialog = ApplyDialog(context, applyDate)

                // 선택한 유형
                applyDialog.setOnCheck(object : OnCheckedBox {
                    override fun setCheckedCategory(category: String) {
//                        binding.calendarSelectedCategory.text = category
                    }
                })

                applyDialog.setOnApplyTime(object : OnApplyTimeListener {
                    override fun setOnStartTime(selectedHour: Int, selectedMinute: Int) {
//                        if( selectedMinute == 0 )
//                            binding.calendarStartTime.text = "${selectedHour}시부터"
//                        else
//                            binding.calendarStartTime.text = "${selectedHour}시 ${selectedMinute}분부터"

                    }

                    override fun setOnEndTime(selectedHour: Int, selectedMinute: Int) {
//                        if( selectedMinute == 0 )
//                            binding.calendarEndTime.text = "${selectedHour}시까지"
//                        else
//                            binding.calendarEndTime.text = "${selectedHour}시 ${selectedMinute}분까지"
                    }
                })

                applyDialog.show()
            }
        }
    }

    // 달력에서 선택한 날짜
    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        val today: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val selectedDay = "${year}년 ${month+1}월 ${dayOfMonth}일"

        if( dayOfMonth == today )
            binding.calendarDialogSelectedDate.text = "오늘"
        else
            binding.calendarDialogSelectedDate.text = selectedDay

        val selectedYear = year.toString()
        val selectedMonth = (month + 1).toString()
        val selectedDate = dayOfMonth.toString()

        println("달력 날짜 클릭")
        // 일정 가져오기
        scheduleAdapter?.getScheduleList(selectedYear, selectedMonth, selectedDate)
    }

    // listAdapter 설정
    private fun initAdapter() {
        scheduleAdapter = ScheduleListAdapter()
        binding.calendarListView.adapter = scheduleAdapter
    }

    // 일정 가져오기
    private fun getScheduleList(year: String, month: String, day: String) {
        db.collection(Constants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year)
            .document(month)
            .collection(day)
//            .document("schedule")
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {

                        val name = i.data["endTime"]
                        println("name >> ${name}")
//                        println("month >> ${month}")
//                        if( i.id == month ) {
//                            println("i.id 들어옴")
//                        } else
//                            println("안 들어옴")

                        println("i.id >> ${i.id}")
                    }
                } else {
                    println("fail")
                }
            }
    }

}