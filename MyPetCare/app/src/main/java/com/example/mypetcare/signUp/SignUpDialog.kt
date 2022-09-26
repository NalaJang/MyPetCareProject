package com.example.mypetcare.signUp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.databinding.DialogSignUpBinding
import com.example.mypetcare.database.dto.UserInfoDTO
import com.example.mypetcare.database.firebase.ProfileImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@SuppressLint("ResourceType")
class SignUpDialog constructor(activity: Activity): Dialog(activity, R.drawable.dialog_full_screen)
    , View.OnClickListener, TextWatcher {

    private val mActivity = activity
    private var mBinding: DialogSignUpBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var myEmail: String? = null
    private var myPassword: String? = null
    private var myPhoneNum: String? = null
    private var myPetAge: String? = null
    private var myPetSpecies: String? = null
    private var myPetCharacter: String? = null
    private val PASSWORD_MIN_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

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
                myEmail = binding.signInMyEmail.text.toString()
                myPassword = binding.signInMyPassword.text.toString()

                if( wrongAccess() )
                    createAccount(myEmail!!, myPassword!!)
                else
                    toastMessage("정보를 모두 입력해 주세요.")

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
        myPhoneNum = binding.signInMyPhoneNum.text.toString()
        myPetAge = binding.signInMyPetAge.text.toString()
        myPetSpecies = binding.signInMyPetSpecies.text.toString()
        myPetCharacter = binding.signInMyPetCharacter.text.toString()


        if( myEmail!!.isEmpty() ) // = myEmail.length < 1
            binding.signInWarningMyEmail.visibility = View.VISIBLE

        if( myPassword!!.isEmpty() )
            binding.signInWarningMyPassword.visibility = View.VISIBLE

        if( myPhoneNum!!.isEmpty() )
            binding.signInWarningMyPhoneNum.visibility = View.VISIBLE

        if( myPetAge!!.isEmpty() )
            binding.signInWarningMyPetAge.visibility = View.VISIBLE

        if( myPetSpecies!!.isEmpty() )
            binding.signInWarningMyPetSpecies.visibility = View.VISIBLE

        if( myPetCharacter!!.isEmpty() )
            binding.signInWarningMyPetCharacter.visibility = View.VISIBLE

        if( binding.signInWarningMyEmail.isInvisible && binding.signInWarningMyPassword.isInvisible
            && binding.signInWarningMyPhoneNum.isInvisible && binding.signInWarningMyPetAge.isInvisible
            && binding.signInWarningMyPetSpecies.isInvisible && binding.signInWarningMyPetCharacter.isInvisible ) {

            return true
        }
        return false
    }

    // 계정 생성
    private fun createAccount(email: String, password: String) {

        if( email.isNotEmpty() && password.isNotEmpty() ) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if( it.isSuccessful ) {
                        // DB 에 저장
                        saveInfoToDB()
                        toastMessage("가입되었습니다.")
                        dismiss()

                    } else
                        toastMessage("다시 시도해 주세요.")
                }
        }
    }

    // DB 에 저장
    private fun saveInfoToDB() {
        val myName: String      = binding.signInMyName.text.toString()
        val myPetName: String   = binding.signInMyPetName.text.toString()
        val myPetWeight: String = binding.signInMyPetWeight.text.toString()

        val userInfoDTO = UserInfoDTO()
        userInfoDTO.uid             = auth?.uid
        userInfoDTO.userEmail       = auth?.currentUser?.email
        userInfoDTO.userPassword    = myPassword
        userInfoDTO.userName        = myName
        userInfoDTO.userPhoneNum    = myPhoneNum
        userInfoDTO.userPetName     = myPetName
        userInfoDTO.userPetAge      = myPetAge
        userInfoDTO.userPetSpecies  = myPetSpecies
        userInfoDTO.userPetWeight   = myPetWeight
        userInfoDTO.userPetCharacter= myPetCharacter

        db  ?.collection(UserInfoConstants.USER_INFO)
            ?.document(auth!!.currentUser!!.uid)
            ?.set(userInfoDTO)
            ?.addOnSuccessListener {
                println("성공")
            }
            ?.addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }

        // firebaseStorage 에 기본 프로필 이미지 저장 및 캐시 파일 생성
        val profileImage = ProfileImage(mActivity, auth!!.currentUser!!.uid)
        profileImage.setBasicProfileImage(context)

    }

    private fun toastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // 화면 바깥 터치 시 키보드 내리기
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val focusView = currentFocus

        if( focusView != null)
            HideKeyboard().hideKeyboard(focusView, context, event)

        return super.dispatchTouchEvent(event)
    }


}