package com.example.mypetcare.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogMyProfileBinding

@SuppressLint("ResourceType")
class MyProfile constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener {

    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!


    init {
        setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DialogMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.proFileClose.setOnClickListener(this)
        binding.profileComplete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.proFile_close -> dismiss()

            // 완료
            R.id.profile_complete -> dismiss()
        }
    }
}