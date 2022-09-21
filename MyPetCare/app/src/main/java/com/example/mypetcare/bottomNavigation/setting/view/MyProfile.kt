package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.mypetcare.database.Constants
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.firebase.GetUserInfo
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyProfile : DialogFragment(), View.OnClickListener {

    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private val filePath = "profile_images"
    private val fileName = "${uid}.png"
    private val storage = FirebaseStorage.getInstance()
    /*
     * 참조 생성
     * 파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하려면 참조를 만든다.
     * 참조는 클라우드의 파일을 가리키는 포인터이다.
     * 참조는 메모리에 부담을 주지 않으므로 원하는 만큼 만들 수 있으며 여러 작업에서 재사용할 수도 있다.
     */
    private val storageRef = storage.reference
    private var profileImageUri: Uri? = null
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
    ): View? {
        mBinding = DialogMyProfileBinding.inflate(inflater, container, false)

        // 사용자 정보 가져오기
        GetUserInfo(requireActivity()).getUserInfo( binding.profileMyName,
                                                    binding.profileMyPhoneNum,
                                                    binding.profileMyPetName,
                                                    binding.profileMyPetAge,
                                                    binding.profileMyPetWeight,
                                                    binding.profileMyPetSpecies,
                                                    binding.profileMyPetCharacter,
                                                    requireContext()
                                                )
        // 사용자 프로필 이미지 가져오기
        GetUserInfo(requireActivity()).getProfileImage(binding.profileProfileImage)

        // 프로필 사진 변경에 대한 결과값 받기
        getActivityResult()

        binding.proFileClose.setOnClickListener(this)
        binding.profileProfileImage.setOnClickListener(this)
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

            // 프로필 사진 변경
            R.id.profile_profileImage -> openGallery()

            // 수정
            R.id.profile_complete -> {
                updateInfo()
                // firebaseStorage 에 이미지 업로드
                uploadProfileImage(profileImageUri!!)
            }
        }
    }

    // 프로필 사진 변경에 대한 결과값 받기
    private fun getActivityResult() {
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                profileImageUri = result.data?.data

                binding.profileProfileImage.setImageURI(profileImageUri)
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

    // firebaseStorage 에 이미지 업로드
    private fun uploadProfileImage(uri: Uri) {

        // 이미지 업로드
        val uploadImagesRef = storageRef.child("${filePath}/").child(fileName)
        uploadImagesRef.putFile(uri)
                        .addOnSuccessListener {
                            println("사진 업로드 성공")
                        }
                        .addOnFailureListener {
                            println("사진 업로드 실패 -> ${it.message}")
                            //E/StorageException: The server has terminated the upload session 해결
                        }

        // 기존 저장된 이미지 삭제
        val deleteImagesRef = storageRef.child("${filePath}/").child(fileName)
        deleteImagesRef.delete()
                        .addOnSuccessListener {
                            println("사진 삭제 성공")
                        }
                        .addOnFailureListener {
                            if( it.message == "Object does not exist at location." ) {
                                println("기존 저장된 이미지가 없습니다.")
                            } else
                                println("사진 삭제 실패 -> ${it.message}")
                        }
    }

    // 정보 업데이트
    private fun updateInfo() {
        val map = mutableMapOf<String, Any>()
        map["userPetName"]      = binding.profileMyPetName.text.toString()
        map["userPetAge"]       = binding.profileMyPetAge.text.toString()
        map["userPetSpecies"]   = binding.profileMyPetSpecies.text.toString()
        map["userPetWeight"]    = binding.profileMyPetWeight.text.toString()
        map["userPetCharacter"] = binding.profileMyPetCharacter.text.toString()

        db  .collection(Constants.USER_INFO)
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