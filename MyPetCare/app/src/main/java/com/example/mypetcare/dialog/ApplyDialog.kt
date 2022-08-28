package com.example.mypetcare.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.core.content.ContextCompat.getSystemService
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogApplyBinding
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("ResourceType")
class ApplyDialog constructor(context: Context, applyDate: String): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private val TAG: String = "ApplyDialog"
    private var mBinding: DialogApplyBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var checkBoxListener : OnCheckedBox? = null
    private var applyTimeListener: OnApplyTimeListener? = null

    private val startDate: String = applyDate
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDate = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        binding.applyStartDate.text = startDate
        binding.applyEndDate.text = startDate
        binding.applyStartTime.text = "${hour}시부터"
        binding.applyEndTime.text = "${hour+1}시까지"
        applyTimeListener?.setOnStartTime(hour, 0)
        applyTimeListener?.setOnEndTime(hour+1, 0)



        binding.applyClose.setOnClickListener(this)
        binding.applyComplete.setOnClickListener(this)
        binding.applyWalk.setOnCheckedChangeListener(this)
        binding.applyBath.setOnCheckedChangeListener(this)
        binding.applyVisit.setOnCheckedChangeListener(this)
        binding.applyStartDate.setOnClickListener(this)
        binding.applyStartTime.setOnClickListener(this)
        binding.applyEndDate.setOnClickListener(this)
        binding.applyEndTime.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.apply_close -> dismiss()

            // 시작 날짜
            R.id.apply_startDate -> getApplyDate(R.id.apply_startDate)

            // 시작 시간
            R.id.apply_startTime -> geApplyTime(R.id.apply_startTime)

            // 종료 날짜
            R.id.apply_endDate -> getApplyDate(R.id.apply_endDate)

            // 종료 시간
            R.id.apply_endTime -> geApplyTime(R.id.apply_endTime)

            // 완료
            R.id.apply_complete -> applyForSchedule()

        }
    }

    // 신청 유형
    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        if( isChecked ) {
            when(button?.id) {
                R.id.apply_walk -> checkBoxListener?.setCheckedCategory("산책")
                R.id.apply_bath -> checkBoxListener?.setCheckedCategory("목욕")
                R.id.apply_visit -> checkBoxListener?.setCheckedCategory("방문")
            }
       }
    }


    // 신청 날짜
    private fun getApplyDate(id: Int) {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val date = "${year}년 ${month+1}월 ${dayOfMonth}일"

            if( id == R.id.apply_startDate )
                binding.applyStartDate.text = date
            else if( id == R.id.apply_endDate)
                binding.applyEndDate.text = date

            selectedYear = year
            selectedMonth = month
            selectedDate = dayOfMonth
        }


        val datePickerDialog: DatePickerDialog
        if( selectedYear == 0 ) {
            datePickerDialog = DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        } else
            datePickerDialog = DatePickerDialog(context, dateSetListener, selectedYear, selectedMonth, selectedDate)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    // 신청 시간
    private fun geApplyTime(id: Int) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{
            _: TimePicker?, hour: Int, minute: Int ->

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            val time = "${hour}시 ${minute}분부터"

            if( id == R.id.apply_startTime ) {
                binding.applyStartTime.text = time
                applyTimeListener?.setOnStartTime(hour, minute)

            } else if( id == R.id.apply_endTime ) {
                binding.applyEndTime.text = time
                applyTimeListener?.setOnEndTime(hour, minute)
            }
        }

        TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), 0, false).show()
    }


    fun setOnCheck(checkListener: OnCheckedBox) {
        checkBoxListener = checkListener
    }

    fun setOnApplyTime(listener: OnApplyTimeListener) {
        applyTimeListener = listener
    }

    fun applyForSchedule() {
        var selectedCategory = ""
        if( binding.applyWalk.isChecked )
            selectedCategory += binding.applyWalk.text.toString() + ","
        if( binding.applyBath.isChecked )
            selectedCategory += binding.applyBath.text.toString() + ","
        if( binding.applyVisit.isChecked )
            selectedCategory += binding.applyVisit.text.toString() + ","

        if( selectedCategory.length > 0 )
            selectedCategory = selectedCategory.substring(0, selectedCategory.length -1)

        println("selectedCategory >> ${selectedCategory}")

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val registrationTime = dateFormat.format(now)

        val selectedLocation = ""
        val startDate = binding.applyStartDate.text.toString()
        val endDate = binding.applyEndDate.text.toString()
        val startTime = binding.applyStartTime.text.toString()
        val endTime = binding.applyEndTime.text.toString()
        val memo = binding.applyMemo.text.toString()

        val userSchedule = hashMapOf(
            "uid" to auth?.currentUser?.uid,
            "selectedCategory" to selectedCategory,
            "selectedLocation" to selectedLocation,
            "startDate" to startDate,
            "endDate" to endDate,
            "startTime" to startTime,
            "endTime" to endTime,
            "memo" to memo,
            "registrationTime" to registrationTime
        )

        db  ?.collection("userSchedule")
            ?.document(auth!!.currentUser!!.uid)
            ?.collection(year.toString())
            ?.document(month.toString())
            ?.set(userSchedule)
            ?.addOnSuccessListener {
                println("성공")
                dismiss()
            }
            ?.addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }

    }

    // 화면 바깥 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if( focusView != null && ev != null ) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    //    fun setOnCheck(checkListener: (String) -> Unit) {
//        listener = object : OnCheckedBox {
//            override fun setCheckedCategory(category: String) {
//                checkListener(category)
//            }
//
//        }
//    }
}

