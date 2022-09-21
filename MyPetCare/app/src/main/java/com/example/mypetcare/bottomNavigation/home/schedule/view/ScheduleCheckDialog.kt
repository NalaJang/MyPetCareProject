package com.example.mypetcare.bottomNavigation.home.schedule.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.managerInfo.view.ManagerProfile
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
    private var managerUid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogScheduleCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 일정에 등록된 매니저 확인
        checkRegisteredManager()

        // 일정 정보
        getScheduleInfo()

        binding.scheduleCheckClose.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener {
        when(it?.id) {
            // 닫기
            R.id.scheduleCheck_close -> dismiss()

            // 매니저 프로필
            R.id.scheduleCheck_managerInfo -> {
//                val managerUid = "r6RWzihGNWUIgbpOes2SQp8ZLds1"
                val managerProfile = ManagerProfile(context, managerUid!!)
                managerProfile.show()
            }
        }
    }

    // 일정에 등록된 매니저 확인
    private fun checkRegisteredManager() {
        managerUid = itemList[0].managerUid

        if( managerUid == null )
            binding.scheduleCheckManagerInfo.visibility = View.INVISIBLE

        else {
            binding.scheduleCheckManagerInfo.visibility = View.VISIBLE
            binding.scheduleCheckManagerName.text = itemList[0].managerName

            binding.scheduleCheckManagerInfo.setOnClickListener(clickListener)
        }
    }

    // 일정 정보
    private fun getScheduleInfo() {
        // yyyy 년 mm월 dd일
        val applicationDate = context.getString(R.string.applicationDate, selectedYear, selectedMonth, selectedDate)

        binding.scheduleCheckCategory.text = itemList[0].selectedCategory
        binding.scheduleCheckDate.text = applicationDate
        binding.scheduleCheckStartTime.text = itemList[0].startTime
        binding.scheduleCheckEndTime.text = itemList[0].endTime
        binding.scheduleCheckMemo.text = itemList[0].memo
    }

}