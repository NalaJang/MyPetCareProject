package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.CalendarDialog
import com.example.mypetcare.R
import com.example.mypetcare.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener {

    private var mBinding : FragmentHomeBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeShowCalendarDialog.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.home_showCalendarDialog -> {
                val calendarDialog = CalendarDialog(requireContext())
                calendarDialog.showCalendar()
            }
        }
    }

}