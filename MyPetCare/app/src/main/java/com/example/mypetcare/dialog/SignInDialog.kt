package com.example.mypetcare.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogSignInBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("ResourceType")
class SignInDialog constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, TextWatcher {

    private var mBinding: DialogSignInBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null

    var myEmail: String? = null
    var myPassword: String? = null
    private var PASSWORD_MIN_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signInClose.setOnClickListener(this)
        binding.signInSignIn.setOnClickListener(this)
        binding.signInMyEmail.addTextChangedListener(this)
        binding.signInMyPassword.addTextChangedListener(this)
        binding.signInMyPhoneNum.addTextChangedListener(this)
        binding.signInMyPetAge.addTextChangedListener(this)
        binding.signInMyPetSpecies.addTextChangedListener(this)
        binding.signInMyPetCharacter.addTextChangedListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // 닫기
            R.id.signIn_close -> dismiss()

            // 가입
            R.id.signIn_signIn -> {

                if( wrongAccess() ) {
                    createAccount(myEmail!!, myPassword!!)
                } else {
                    toastMessage("정보를 모두 입력해 주세요.")
                }
            }
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // 이메일
        if( binding.signInMyEmail.hasFocus() ) {
            if( binding.signInMyEmail.length() > 5 )
                binding.signInWarningMyEmail.visibility = View.INVISIBLE
            else
                binding.signInWarningMyEmail.visibility = View.VISIBLE
        }

        // 비밀번호
        // 비밀번호는 최소 6자리 이상
        if( binding.signInMyPassword.hasFocus() ) {
            if( binding.signInMyPassword.length() >= PASSWORD_MIN_LENGTH )
                binding.signInWarningMyPassword.visibility = View.INVISIBLE
            else
                binding.signInWarningMyPassword.visibility = View.VISIBLE
        }

        // 휴대폰번호
        if( binding.signInMyPhoneNum.hasFocus() ) {
            if( binding.signInMyPhoneNum.length() >= 11 )
                binding.signInWarningMyPhoneNum.visibility = View.INVISIBLE
            else
                binding.signInWarningMyPhoneNum.visibility = View.VISIBLE
        }

        // myPet 나이
        if( binding.signInMyPetAge.hasFocus() ) {
            if( binding.signInMyPetAge.length() < 1 )
                binding.signInWarningMyPetAge.visibility = View.VISIBLE
            else
                binding.signInWarningMyPetAge.visibility = View.INVISIBLE
        }

        // myPet 종
        if( binding.signInMyPetSpecies.hasFocus() ) {
            if( binding.signInMyPetSpecies.length() < 1 )
                binding.signInWarningMyPetSpecies.visibility = View.VISIBLE
            else
                binding.signInWarningMyPetSpecies.visibility = View.INVISIBLE
        }

        // myPet 성격
        if( binding.signInMyPetCharacter.hasFocus() ) {
            if( binding.signInMyPetCharacter.length() >= 1 )
                binding.signInWarningMyPetCharacter.visibility = View.INVISIBLE
            else
                binding.signInWarningMyPetCharacter.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }


    private fun wrongAccess(): Boolean {
        myEmail = binding.signInMyEmail.text.toString()
        myPassword = binding.signInMyPassword.text.toString()
        val myPhoneNum = binding.signInMyPhoneNum.text.toString()
        val myPetAge = binding.signInMyPetAge.text.toString()
        val myPetSpecies = binding.signInMyPetSpecies.text.toString()
        val myPetCharacter = binding.signInMyPetCharacter.text.toString()


        if( myEmail!!.isEmpty() ) // = myEmail.length < 1
            binding.signInWarningMyEmail.visibility = View.VISIBLE

        if( myPassword!!.isEmpty() )
            binding.signInWarningMyPassword.visibility = View.VISIBLE

        if( myPhoneNum.isEmpty() )
            binding.signInWarningMyPhoneNum.visibility = View.VISIBLE

        if( myPetAge.isEmpty() )
            binding.signInWarningMyPetAge.visibility = View.VISIBLE

        if( myPetSpecies.isEmpty() )
            binding.signInWarningMyPetSpecies.visibility = View.VISIBLE

        if( myPetCharacter.isEmpty() )
            binding.signInWarningMyPetCharacter.visibility = View.VISIBLE

        if( binding.signInWarningMyEmail.isInvisible && binding.signInWarningMyPassword.isInvisible
            && binding.signInWarningMyPhoneNum.isInvisible && binding.signInWarningMyPetAge.isInvisible
            && binding.signInWarningMyPetSpecies.isInvisible && binding.signInWarningMyPetCharacter.isInvisible ) {

            return true
        }
        return false
    }

    private fun createAccount(email: String, password: String) {

        if( email.isNotEmpty() && password.isNotEmpty() ) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if( it.isSuccessful ) {
                        toastMessage("가입되었습니다.")
                        dismiss()
                    } else {
                        toastMessage("다시 시도해 주세요.")
                    }
                }
        }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}