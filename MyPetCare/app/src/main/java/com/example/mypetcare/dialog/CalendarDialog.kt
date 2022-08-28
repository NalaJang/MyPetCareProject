package com.example.mypetcare.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogCalendarBinding
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

@SuppressLint("ResourceType")
class CalendarDialog constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener {

    private val TAG: String = "CalendarDialog"
    private var mBinding: DialogCalendarBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null


    init {
        setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        binding.calendarDialogSelectedDate.text = "오늘"


        // 달력에서 선택한 날짜
        val calendar = Calendar.getInstance()
        val today: Int = calendar.get(Calendar.DAY_OF_MONTH)
        binding.calendarDialogCalendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            if( dayOfMonth == today)
                binding.calendarDialogSelectedDate.text = "오늘"
            else
                binding.calendarDialogSelectedDate.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
        }

//        val applyDate: String = binding.calendarDialogSelectedDate.text.toString()
//        applyDialog = ApplyDialog(context, applyDate)
//        applyDialog!!.setOnCheck { checkListener ->
//            binding.calendarSelectedCategory.text = checkListener
//        }



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
                        binding.calendarSelectedCategory.text = category
                    }
                })

                applyDialog.setOnApplyTime(object : OnApplyTimeListener {
                    override fun setOnStartTime(selectedHour: Int, selectedMinute: Int) {
                        if( selectedMinute == 0 )
                            binding.calendarStartTime.text = "${selectedHour}시부터"
                        else
                            binding.calendarStartTime.text = "${selectedHour}시 ${selectedMinute}분부터"

                    }

                    override fun setOnEndTime(selectedHour: Int, selectedMinute: Int) {
                        if( selectedMinute == 0 )
                            binding.calendarEndTime.text = "${selectedHour}시까지"
                        else
                            binding.calendarEndTime.text = "${selectedHour}시 ${selectedMinute}분까지"
                    }
                })

                applyDialog.show()
            }
        }
    }


    fun applyForSchedule() {
        val date = binding.calendarStartTime

        val mySchedule = hashMapOf(
            "uid" to auth?.currentUser?.uid,
//            "userEmail" to myEmail,
        )
    }
}