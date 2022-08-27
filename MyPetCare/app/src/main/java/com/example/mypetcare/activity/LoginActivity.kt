package com.example.mypetcare.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.databinding.ActivityLoginBinding
import com.example.mypetcare.dialog.SignInDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    private var mBinding : ActivityLoginBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null
    private var PASSWORD_MIN_LENGTH = 6


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // "가입하기"에 밑줄
        binding.loginSignIn.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.loginEmail.addTextChangedListener(this)
        binding.loginPassword.addTextChangedListener(this)
        binding.loginLogin.setOnClickListener(this)
        binding.loginSignIn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
//        if( currentUser != null)
//            reload();
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // 로그인
            R.id.login_login -> {

                // 입력된 정보가 없이 로그인을 눌렀을 경우
//                if( binding.loginEmail.length() < 1 && binding.loginPassword.length() < 1 ) {
//                    binding.loginEmail.visibility = View.VISIBLE
//                    binding.loginWarningPassword.visibility = View.VISIBLE
//
//                }
//                // 정보를 모두 입력했을 경우
//                else if( binding.loginWarningEmail.isGone && binding.loginWarningPassword.isGone ) {
                    val myEmail = binding.loginEmail.text.toString()
                    val myPassword = binding.loginPassword.text.toString()
                    userLogin(myEmail, myPassword)

//                    val intent = Intent(this, BottomNavigation::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }

            // 회원가입
            R.id.login_signIn -> {
                val signInDialog = SignInDialog(this)
                signInDialog.show()
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if( binding.loginEmail.hasFocus() ) {
            if( binding.loginEmail.length() >= 3 )
                binding.loginWarningEmail.visibility = View.INVISIBLE
            else
                binding.loginWarningEmail.visibility = View.VISIBLE
        }

        if( binding.loginPassword.hasFocus() ) {
            if( binding.loginPassword.length() >= PASSWORD_MIN_LENGTH )
                binding.loginWarningPassword.visibility = View.INVISIBLE
            else
                binding.loginWarningPassword.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }


    // 로그인
    private fun userLogin(email: String, password: String) {
        println("loginUser, ${email}, ${password}")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if( task.isSuccessful ) {

                    val intent = Intent(this, BottomNavigation::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    println("실패")
                }
            }
            .addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }
    }


    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}