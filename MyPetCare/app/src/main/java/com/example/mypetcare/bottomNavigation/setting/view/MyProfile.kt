package com.example.mypetcare.bottomNavigation.setting.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class MyProfile : DialogFragment()
    , View.OnClickListener {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogMyProfileBinding.inflate(inflater, container, false)

        // 사용자 정보 가져오기
        getUserInfo()
        // 사용자 프로필 이미지 가져오기
        getProfileImage()

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                profileImageUri = result.data?.data

                binding.profileProfileImage.setImageURI(profileImageUri)
                // firebaseStorage 에 이미지 업로드
                uploadProfileImage(profileImageUri!!)
            }
        }

        binding.proFileClose.setOnClickListener(this)
        binding.profileProfileImage.setOnClickListener(this)
        binding.profileComplete.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.proFile_close -> dismiss()

            // 프로필 사진 변경
            R.id.profile_profileImage -> openGallery()

            // 수정
            R.id.profile_complete -> updateInfo()
        }
    }

    // 사용자 정보 가져오기
    private fun getUserInfo() {
        db  .collection("userInfo")
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == uid.toString() ) {

                            val myName = i.data["userName"]
                            val myPhoneNum = i.data["userPhoneNum"]
                            val myPetName = i.data["userPetName"]
                            val myPetAGE = i.data["userPetAge"]
                            val myPetWeight = i.data["userPetWeight"]
                            val myPetSpecies = i.data["userPetSpecies"]
                            val myPetCharacter = i.data["userPetCharacter"]

                            binding.profileMyName.text = myName.toString()
                            binding.profileMyPhoneNum.text = myPhoneNum.toString()
                            binding.profileMyPetName.setText(myPetName.toString())
                            binding.profileMyPetAge.setText(myPetAGE.toString())
                            binding.profileMyPetWeight.setText(myPetWeight.toString())
                            binding.profileMyPetSpecies.setText(myPetSpecies.toString())
                            binding.profileMyPetCharacter.setText(myPetCharacter.toString())

                            break
                        }
                    }
                } else
                    println("No such document")
            }
            .addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }
    }

    // 사용자 프로필 이미지 가져오기
    private fun getProfileImage() {
        val file = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/${filePath}")

        // file 에서 디렉토리 확인
        // 만약 없다면 디렉토리를 생성
        if( !file!!.isDirectory ) {
            file.mkdir()
        }
        downloadProfileImage()
    }

    // 프로필 이미지 가져오기
    private fun downloadProfileImage() {
        storageRef.child("${filePath}/").child(fileName).downloadUrl
            .addOnSuccessListener { uri ->
                println("사진 다운로드 성공 uri: $uri")
                // context X -> activity
                Glide.with(activity).load(uri).into(binding.profileProfileImage)
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
        map["userPetName"] = binding.profileMyPetName.text.toString()
        map["userPetAge"] = binding.profileMyPetAge.text.toString()
        map["userPetSpecies"] = binding.profileMyPetSpecies.text.toString()
        map["userPetWeight"] = binding.profileMyPetWeight.text.toString()
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