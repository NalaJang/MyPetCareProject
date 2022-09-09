package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogMyReviewBinding

@SuppressLint("ResourceType")
class MyReview constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen) {

    private var mBinding: DialogMyReviewBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 닫기
        binding.myReviewClose.setOnClickListener { dismiss() }
    }
}