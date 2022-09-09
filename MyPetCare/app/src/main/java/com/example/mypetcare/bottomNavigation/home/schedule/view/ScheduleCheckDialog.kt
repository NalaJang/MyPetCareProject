package com.example.mypetcare.bottomNavigation.home.schedule.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.example.mypetcare.databinding.DialogScheduleCheckBinding

@SuppressLint("ResourceType")
class ScheduleCheckDialog constructor(context: Context, getData: ArrayList<UserScheduleDTO>,
                                      year: String, month: String, date: String):
    Dialog(context, R.drawable.dialog_full_screen) {

    private var mBinding: DialogScheduleCheckBinding? = null
    private val binding get() = mBinding!!
    private val itemList = getData
    private val selectedYear = year
    private val selectedMonth = month
    private val selectedDate = date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogScheduleCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 일정 정보
        getScheduleInfo()
        binding.scheduleCheckClose.setOnClickListener(clickListener)

    }

    private val clickListener = View.OnClickListener {
        when(it?.id) {
            R.id.scheduleCheck_close -> dismiss()
        }
    }

    // 일정 정보
    private fun getScheduleInfo() {
        val applicationDate = context.getString(R.string.applicationDate, selectedYear, selectedMonth, selectedDate)

        binding.scheduleCheckCategory.text = itemList[0].selectedCategory
        binding.scheduleCheckDate.text = applicationDate
        binding.scheduleCheckStartTime.text = itemList[0].startTime
        binding.scheduleCheckEndTime.text = itemList[0].endTime
        binding.scheduleCheckLocation.text = itemList[0].selectedLocation
        binding.scheduleCheckMemo.text = itemList[0].memo

    }


}