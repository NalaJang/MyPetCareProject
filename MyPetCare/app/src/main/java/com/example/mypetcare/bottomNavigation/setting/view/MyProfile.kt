package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.mypetcare.R
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@SuppressLint("ResourceType")
class MyProfile constructor(context: Context): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener {

    private var TAG = "MyProfile"
    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null


    init {
        setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 정보 가져오기
        getUserInfo()

        binding.proFileClose.setOnClickListener(this)
        binding.profileComplete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.proFile_close -> dismiss()

            // 완료
            R.id.profile_complete -> updateInfo()
        }
    }

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

    private fun updateInfo() {
        val map = mutableMapOf<String, Any>()
        map["userPetName"] = binding.profileMyPetName.text.toString()
        map["userPetAge"] = binding.profileMyPetAge.text.toString()
        map["userPetSpecies"] = binding.profileMyPetSpecies.text.toString()
        map["userPetWeight"] = binding.profileMyPetWeight.text.toString()
        map["userPetCharacter"] = binding.profileMyPetCharacter.text.toString()

        db?.collection("userInfo")
            ?.document(uid!!)
            ?.update(map)
            ?.addOnCompleteListener { task ->
                if( task.isSuccessful ) {
                    Toast.makeText(context, "정보 수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
    }

}