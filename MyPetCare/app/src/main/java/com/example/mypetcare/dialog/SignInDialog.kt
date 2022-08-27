package com.example.mypetcare.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogSignInBinding

@SuppressLint("ResourceType")
class SignInDialog constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, TextWatcher {

    private var mBinding: DialogSignInBinding? = null
    private val binding get() = mBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        binding.signInMyId.addTextChangedListener(this)
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
            R.id.signIn_signIn -> dismiss()
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // 아이디
        if( binding.signInMyId.hasFocus() ) {
            if( binding.signInMyId.length() < 5 )
                binding.signInWarningMyId.visibility = View.VISIBLE
            else
                binding.signInWarningMyId.visibility = View.INVISIBLE
        }

        // 비밀번호
        if( binding.signInMyPassword.hasFocus() ) {
            if( binding.signInMyPassword.length() < 5 )
                binding.signInWarningMyPassword.visibility = View.VISIBLE
            else
                binding.signInWarningMyPassword.visibility = View.INVISIBLE
        }

        // 휴대폰번호
        if( binding.signInMyPhoneNum.hasFocus() ) {
            if( binding.signInMyPhoneNum.length() < 12 )
                binding.signInWarningMyPhoneNum.visibility = View.VISIBLE
            else
                binding.signInWarningMyPhoneNum.visibility = View.INVISIBLE
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
            if( binding.signInMyPetCharacter.length() < 1 )
                binding.signInWarningMyPetCharacter.visibility = View.VISIBLE
            else
                binding.signInWarningMyPetCharacter.visibility = View.INVISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}