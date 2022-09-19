package com.example.mypetcare.bottomNavigation.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.bottomNavigation.home.schedule.view.CalendarDialog
import com.example.mypetcare.R
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalStateException

class HomeFragment : Fragment() {

    private var mBinding : FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 사용자 정보 가져오기
        getUserInfo()

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

}