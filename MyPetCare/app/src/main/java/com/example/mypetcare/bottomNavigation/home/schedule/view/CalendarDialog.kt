package com.example.mypetcare.bottomNavigation.home.schedule.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CalendarView
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.schedule.adapter.ScheduleListAdapter
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.example.mypetcare.databinding.DialogCalendarBinding
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ResourceType")
class CalendarDialog constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, CalendarView.OnDateChangeListener {

    private var mBinding: DialogCalendarBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private var scheduleAdapter: ScheduleListAdapter? = null
    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDate: String? = null

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


        binding.calendarDialogCalendarView.setOnDateChangeListener(this)
        binding.calendarClose.setOnClickListener(this)
        binding.calendarDialogApplyButton.setOnClickListener(this)
        binding.calendarListView.onItemClickListener = itemClickListener

    }

    // listAdapter 설정
    private fun initAdapter() {
        scheduleAdapter = ScheduleListAdapter()
        binding.calendarListView.adapter = scheduleAdapter
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

        selectedYear = year.toString()
        selectedMonth = (month + 1).toString()
        selectedDate = dayOfMonth.toString()

        println("달력 날짜 클릭")
        // 일정 가져오기
        scheduleAdapter?.getScheduleList(selectedYear!!, selectedMonth!!, selectedDate!!)
    }

    // 일정 항목 클릭
    private val itemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        if( selectedYear == null )
            getScheduleList(year.toString(), month.toString(), date.toString(), position)
        else
            getScheduleList(selectedYear!!, selectedMonth!!, selectedDate!!, position)
    }

    // 일정 가져오기
    private fun getScheduleList(year: String, month: String, date: String, position: Int) {

        db.collection(Constants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year)
            .document(month)
            .collection(date)
            .get()
            .addOnSuccessListener { result ->
                val data = result.toObjects<UserScheduleDTO>()
                val category = data[position].selectedCategory
                val startTime = data[position].startTime
                val endTime = data[position].endTime
                val memo = data[position].memo

                val setData = ArrayList<UserScheduleDTO>()
                setData.add(UserScheduleDTO(
                                            uid,
                                            category,
                                            startTime,
                                            endTime,
                                            memo,
                                    "",
                                ""
                                        ))

                val scheduleCheckDialog = ScheduleCheckDialog(context, setData, year, month, date)
                scheduleCheckDialog.show()
            }
    }

}