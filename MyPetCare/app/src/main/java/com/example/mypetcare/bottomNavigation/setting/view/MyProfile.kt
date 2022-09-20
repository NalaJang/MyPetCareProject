package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.mypetcare.Constants
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.File

class MyProfile : DialogFragment()
    , View.OnClickListener {

    private var TAG = "MyProfile"
    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null

    private var profileImageUri: Uri? = null

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

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 정보 가져오기
        getUserInfo()

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                println("RESULT_OK: ${result.data}")
                profileImageUri = result.data?.data
                println("profileImageUri: ${profileImageUri}")

//                val bitmap = result?.data?.extras?.get("data") as Bitmap
                profileImageUri?.let { uri ->
                    var file = File(getPathFromUri(uri))
                }
                binding.profileProfileImage.setImageURI(profileImageUri)
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

    // 정보 가져오기
    private fun getUserInfo() {

        db?.collection("userInfo")
            ?.get()
            ?.addOnCompleteListener { task ->
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
                    println("${TAG} No such document")
            }
            ?.addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }

    }

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }

    private fun getPathFromUri(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        println("getPathFromUri, buildName: ${buildName}")
        var columnIndex = 0
        val project = arrayOf(MediaStore.Images.Media.DATA)

        return ""
    }
    // 정보 업데이트
    private fun updateInfo() {
        val map = mutableMapOf<String, Any>()
        map["userPetName"] = binding.profileMyPetName.text.toString()
        map["userPetAge"] = binding.profileMyPetAge.text.toString()
        map["userPetSpecies"] = binding.profileMyPetSpecies.text.toString()
        map["userPetWeight"] = binding.profileMyPetWeight.text.toString()
        map["userPetCharacter"] = binding.profileMyPetCharacter.text.toString()

        db  ?.collection(Constants.USER_INFO)
            ?.document(uid!!)
            ?.update(map)
            ?.addOnCompleteListener { task ->
                if( task.isSuccessful ) {
                    Toast.makeText(context, "정보 수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getInstance(): MyProfile {
        return MyProfile()
    }
}