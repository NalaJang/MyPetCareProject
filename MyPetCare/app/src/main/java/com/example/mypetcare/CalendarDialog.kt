package com.example.mypetcare

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class CalendarDialog(context: Context) {


    private val dialog = Dialog(context)

    fun showCalendar() {
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.show()
    }
}