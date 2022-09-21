package com.example.mypetcare.database.firebase

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mypetcare.R
import com.example.mypetcare.database.Constants
import com.example.mypetcare.database.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class GetUserInfo(activity: Activity) {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val mActivity = activity
    private val storage = FirebaseStorage.getInstance()
    /*
    * 참조 생성
    * 파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하려면 참조를 만든다.
    * 참조는 클라우드의 파일을 가리키는 포인터이다.
    * 참조는 메모리에 부담을 주지 않으므로 원하는 만큼 만들 수 있으며 여러 작업에서 재사용할 수도 있다.
    */
    private val storageRef = storage.reference
    private val filePath = Constants.PROFILE_IMAGE_PATH
    private val fileName = "${uid}.png"

    // 사용자 정보 가져오기
    fun getUserInfo(userName: TextView, userPhoneNum: TextView, petName: TextView,
                    petAge: TextView, petWeight: TextView, petSpecies: TextView,
                    petCharacter: TextView, context: Context) {

        db  .collection(Constants.USER_INFO)
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == uid.toString() ) {

                            // db 필드 데이터
                            val myName         = i.data[Constants.USER_NAME]
                            val myPhoneNum     = i.data[Constants.USER_PHONE_NUM]
                            val myPetName      = i.data[Constants.PET_NAME]
                            val myPetAGE       = i.data[Constants.PET_AGE]
                            val myPetWeight    = i.data[Constants.PET_WEIGHT]
                            val myPetSpecies   = i.data[Constants.PET_SPECIES]
                            val myPetCharacter = i.data[Constants.PET_CHARACTER]

                            userName.text     = myName.toString()
                            userPhoneNum.text = myPhoneNum.toString()
                            petName.text      = myPetName.toString()
                            petAge.text       = context.getString(R.string.ageUnit, myPetAGE.toString())
                            petWeight.text    = context.getString(R.string.weightUnit, myPetWeight.toString())
                            petSpecies.text   = myPetSpecies.toString()
                            petCharacter.text = myPetCharacter.toString()

                            PreferenceManager.setString(
                                context,
                                Constants.USER_NAME,
                                myName.toString()
                            )

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
    fun getProfileImage(imageView: ImageView) {
        val file = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/${filePath}")

        // file 에서 디렉토리 확인
        // 만약 없다면 디렉토리를 생성
        if( !file!!.isDirectory ) {
            file.mkdir()
        }
        downloadProfileImage(imageView)
    }

    // 프로필 이미지 가져오기
    private fun downloadProfileImage(imageView: ImageView) {
        storageRef  .child("${filePath}/")
                    .child(fileName).downloadUrl
                    .addOnSuccessListener { uri ->
                        println("사진 다운로드 성공 uri: ${uri}")
                        // context X -> activity
                        Glide.with(mActivity).load(uri).into(imageView)
                    }
                    .addOnFailureListener {
                        if( it.message == "Object does not exist at location." )
                            imageView.setImageResource(R.drawable.profile_photo)
                        else
                            println("다운로드 실패 -> ${it.message}")
                    }
    }
}