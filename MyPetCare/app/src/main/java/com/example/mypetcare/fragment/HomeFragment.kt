package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.dialog.CalendarDialog
import com.example.mypetcare.R
import com.example.mypetcare.databinding.FragmentHomeBinding
import com.example.mypetcare.dialog.MyProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), View.OnClickListener {

    private var TAG = "HomeFragment"
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

        binding.homeShowCalendarDialog.setOnClickListener(this)
        binding.homeEditProfile.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // 일정 확인
            R.id.home_showCalendarDialog -> {
                val calendarDialog = CalendarDialog(requireContext())
                calendarDialog.show()
            }

            // 프로필 편집
            R.id.home_editProfile -> {
                println("myProfileDialog 클릭")
                val myProfileDialog = MyProfile(requireContext())
                myProfileDialog.show()
            }
        }
    }

    private fun getUserInfo() {

//        val docRef = db?.collection("userInfo")?.document(uid!!)
//        docRef?.get()
//            ?.addOnSuccessListener { document ->
//                if( document != null ) {
//                    println("${TAG} DocumentSnapshot data: ${document.data}")
//                } else {
//                    println("${TAG} No such document")
//                }
//            }
//            ?.addOnFailureListener { e ->
//                println("${TAG} 실패 >> ${e.message}")
//            }

        db?.collection("userInfo")
            ?.get()
            ?.addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == uid.toString() ) {

                            // 필드 데이터
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

}