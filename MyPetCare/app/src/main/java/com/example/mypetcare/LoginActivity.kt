package com.example.mypetcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.mypetcare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    private var mBinding : ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginId.addTextChangedListener(this)
        binding.loginPassword.addTextChangedListener(this)
        binding.loginLogin.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view?.id) {

            // 로그인
            R.id.login_login -> {

                // 입력된 정보가 없이 로그인을 눌렀을 경우
                if( binding.loginId.length() < 1 && binding.loginPassword.length() < 1 ) {
                    binding.loginWarningId.visibility = View.VISIBLE
                    binding.loginWarningPassword.visibility = View.VISIBLE

                }
                // 정보를 모두 입력했을 경우
                else if( binding.loginWarningId.isGone && binding.loginWarningPassword.isGone ) {
                    val intent = Intent(this, BottomNavigation::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if( binding.loginId.hasFocus() ) {
            if( binding.loginId.length() < 3 )
                binding.loginWarningId.visibility = View.VISIBLE
            else
                binding.loginWarningId.visibility = View.GONE
        }

        if( binding.loginPassword.hasFocus() ) {
            if( binding.loginPassword.length() < 3 )
                binding.loginWarningPassword.visibility = View.VISIBLE
            else
                binding.loginWarningPassword.visibility = View.GONE
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}