package com.example.mypetcare.database.firebase

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mypetcare.R
import com.example.mypetcare.database.constant.UserInfoConstants
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class ProfileImage(activity: Activity, uid: String) {

    private val mActivity = activity
    private val storage = FirebaseStorage.getInstance()
    /*
    * 참조 생성
    * 파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하려면 참조를 만든다.
    * 참조는 클라우드의 파일을 가리키는 포인터이다.
    * 참조는 메모리에 부담을 주지 않으므로 원하는 만큼 만들 수 있으며 여러 작업에서 재사용할 수도 있다.
    */
    private val storageRef = storage.reference
    private val filePath = UserInfoConstants.PROFILE_IMAGE_PATH
    private val fileName = "${uid}.png"

    // 저장된 프로필 이미지 파일 찾기
    fun getProfileImage(imageView: ImageView) {
        val file = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/${filePath}")

        // file 에서 디렉토리 확인
        // 만약 없다면 디렉토리를 생성
        if( !file!!.isDirectory ) {
            file.mkdir()
        }
        downloadProfileImage(imageView)
    }

    // 프로필 이미지 다운로드
    private fun downloadProfileImage(imageView: ImageView) {
        storageRef  .child("${filePath}/")
                    .child(fileName).downloadUrl
                    .addOnSuccessListener { uri ->
                        println("사진 다운로드 성공 uri: $uri")
                        /*
                         * context 로 호출하면 아래와 같은 에러 발생
                         * java.lang.NoClassDefFoundError: Failed resolution of: Landroid/support/v4/app/FragmentActivity;
                         */
                        // context X -> activity
                        Glide.with(mActivity).load(uri).into(imageView)

                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        try {
                            val inputStream = mActivity.contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                            inputStream!!.close()

                            // non-null 체크
                            bitmap?.let {
                                imageView.setImageBitmap(bitmap)
                            } ?: let {
                                // bitmap 이 null 일 경우
                                imageView.setImageURI(uri)
                            }
                        } catch (e: Exception) {}

                    }
                    .addOnFailureListener {
                        imageView.setImageResource(R.drawable.basic_profile_image)
                    }
    }

    // firebaseStorage 에 기본 프로필 이미지 저장
    // 이미지 리소스 drawable -> uri 로 변환
    fun setProfileImage(context: Context) {
        val drawableId = R.drawable.basic_profile_image
        val imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                    + "://" + context.resources.getResourcePackageName(drawableId)
                                    + '/' + context.resources.getResourceTypeName(drawableId)
                                    + '/' + context.resources.getResourceEntryName(drawableId)
                                )
        println("imageUri: $imageUri")
        uploadProfileImage(imageUri)
    }

    // firebaseStorage 에 기본 프로필 이미지 저장
    fun uploadProfileImage(uri: Uri) {
        val uploadImagesRef = storageRef.child("${filePath}/").child(fileName)

        uploadImagesRef .putFile(uri)
                        .addOnSuccessListener {
                            println("GetProfileImage, 사진 업로드 성공")
                        }
                        .addOnFailureListener {
                            println("GetProfileImage, 사진 업로드 실패 -> ${it.message}")
                            //E/StorageException: The server has terminated the upload session 해결
                        }
    }

    // 기존 저장된 프로필 이미지 삭제
    fun deleteProfileImage() {
        val deleteImagesRef = storageRef.child("${filePath}/").child(fileName)

        deleteImagesRef .delete()
                        .addOnSuccessListener {
                            println("사진 삭제 성공")
                        }
                        .addOnFailureListener {
                            if( it.message == "Object does not exist at location." )
                                println("기존 저장된 이미지가 없습니다.")
                            else
                                println("사진 삭제 실패 -> ${it.message}")
                        }
    }
}