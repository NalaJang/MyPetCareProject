package com.example.mypetcare.bottomNavigation.home.view

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.mypetcare.bottomNavigation.home.schedule.view.CalendarDialog
import com.example.mypetcare.R
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND
import java.lang.IllegalStateException

class HomeFragment : Fragment() {

    private var mBinding : FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val filePath = "profile_images"
    private val fileName = "${uid}.png"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // 사용자 정보 가져오기
        getUserInfo()
        // 사용자 프로필 이미지 가져오기
        getProfileImage()

        // 일정 확인
        binding.homeShowCalendarDialog.setOnClickListener{
            val calendarDialog = CalendarDialog(requireContext())
            calendarDialog.show()
        }

        return binding.root
    }

    // 사용자 정보 가져오기
    private fun getUserInfo() {
        db  ?.collection("userInfo")
            ?.get()
            ?.addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == uid.toString() ) {

                            // db 필드 데이터
                            val myName = i.data["userName"]
                            val myPhoneNum = i.data["userPhoneNum"]
                            val myPetName = i.data["userPetName"]
                            val myPetAGE = i.data["userPetAge"]
                            val myPetWeight = i.data["userPetWeight"]
                            val myPetSpecies = i.data["userPetSpecies"]
                            val myPetCharacter = i.data["userPetCharacter"]

                            binding.homeMyName.text = myName.toString()
                            binding.homeMyPhoneNum.text = myPhoneNum.toString()
                            binding.homeMyPetName.text = myPetName.toString()
                            binding.homeMyPetAge.text = getString(R.string.ageUnit, myPetAGE.toString())
                            binding.homeMyPetWeight.text = getString(R.string.weightUnit, myPetWeight.toString())
                            binding.homeMyPetSpecies.text = myPetSpecies.toString()
                            binding.homeMyPetCharacter.text = myPetCharacter.toString()

                            PreferenceManager.setString(context, "userName", myName.toString())

                            break
                        }
                    }
                } else
                    println("No such document")
            }
            ?.addOnFailureListener { e ->
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
                println("사진 다운로드 성공 uri: ${uri}")
                // context X -> activity
                Glide.with(activity).load(uri).into(binding.homeProfileImage)
            }
            .addOnFailureListener {
                if( it.message == "Object does not exist at location." )
                    binding.homeProfileImage.setImageResource(R.drawable.profile_photo)
                else
                    println("다운로드 실패 -> ${it.message}")
            }
    }

}