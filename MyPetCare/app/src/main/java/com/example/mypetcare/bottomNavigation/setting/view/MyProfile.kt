package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.Cache
import com.example.mypetcare.database.firebase.ProfileImage
import com.example.mypetcare.database.firebase.GetUserInfo
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyProfile : DialogFragment(), View.OnClickListener {

    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private var profileImageUri: Uri? = null
    private var profileImage: ProfileImage? = null
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogMyProfileBinding.inflate(inflater, container, false)

        val getUserInfo = GetUserInfo()
        profileImage = ProfileImage(requireActivity(), uid!!)

        // 사용자 정보 가져오기
        getUserInfo.getUserInfo(binding.profileMyName,
                                binding.profileMyPhoneNum,
                                binding.profileMyPetName,
                                binding.profileMyPetAge,
                                binding.profileMyPetWeight,
                                binding.profileMyPetSpecies,
                                binding.profileMyPetCharacter,
                                requireContext()
                            )
        // 사용자 프로필 이미지 가져오기
        profileImage!!.getProfileImage(binding.profileProfileImage)

        // 프로필 이미지 변경에 대한 결과값 받기
        getActivityResult()

        binding.proFileClose.setOnClickListener(this)
        binding.profileProfileImage.setOnClickListener(this)
        binding.profileDeleteProfileImage.setOnClickListener(this)
        binding.profileComplete.setOnClickListener(this)
        binding.profileLayout.setOnTouchListener { _, _ ->
            // 키보드 바깥 터치 시 키보드 내리기
            HideKeyboard().hideKeyboardInDialogFragment(requireContext(), requireView())
            true
        }

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.proFile_close -> dismiss()

            // 프로필 이미지 변경
            R.id.profile_profileImage -> openGallery()

            // 프로필 이미지 삭제 -> 기본 이미지로 저장
            R.id.profile_deleteProfileImage -> {
                profileImage!!.setProfileImage(requireContext())
                profileImage!!.deleteProfileImage()
                binding.profileProfileImage.setImageResource(R.drawable.basic_profile_image)
            }

            // 수정
            R.id.profile_complete -> {

                updateInfo()
                // firebaseStorage 에 이미지 업로드
                profileImage!!.uploadProfileImage(profileImageUri!!)
                // 기존 저장된 이미지 삭제
                profileImage!!.deleteProfileImage()
            }
        }
    }

    // 프로필 이미지 변경에 대한 결과값 받기
    private fun getActivityResult() {
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                profileImageUri = result.data?.data

//                binding.profileProfileImage.setImageURI(profileImageUri)

                //
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(profileImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val fileName = "${uid}.png"
                    inputStream!!.close()
                    println("getActivityResult 들어옴")
                    // non-null 체크
                    bitmap?.let {
                        binding.profileProfileImage.setImageBitmap(bitmap)
                        // 내부 저장소에 파일 저장
                        Cache(requireActivity(), fileName).saveImageToCache(bitmap)
                    } ?: let {
                        // bitmap 이 null 일 경우
                        binding.profileProfileImage.setImageURI(profileImageUri)
                    }
                } catch (e: Exception) {
                    println("getActivityResult : ${e.message}")
                }
            }
        }
    }

    // 휴대폰 갤러리 접근
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }

    // 정보 업데이트
    private fun updateInfo() {
        val map = mutableMapOf<String, Any>()
        map[UserInfoConstants.PET_NAME]      = binding.profileMyPetName.text.toString()
        map[UserInfoConstants.PET_AGE]       = binding.profileMyPetAge.text.toString()
        map[UserInfoConstants.PET_SPECIES]   = binding.profileMyPetSpecies.text.toString()
        map[UserInfoConstants.PET_WEIGHT]    = binding.profileMyPetWeight.text.toString()
        map[UserInfoConstants.PET_CHARACTER] = binding.profileMyPetCharacter.text.toString()

        db  .collection(UserInfoConstants.USER_INFO)
            .document(uid!!)
            .update(map)
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {
                    Toast.makeText(context, "정보 수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getInstance(): MyProfile {
        return MyProfile()
    }

}