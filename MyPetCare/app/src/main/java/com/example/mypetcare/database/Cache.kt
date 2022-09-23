package com.example.mypetcare.database

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Cache(activity: Activity, fileName: String) {
    private val mActivity = activity
    private val mFileName = fileName

    // 내부 저장소에 파일 저장
    fun saveImageToCache(bitmap: Bitmap) {
        val storage = mActivity.cacheDir
        val file = File(storage, mFileName)

        try {
            file.createNewFile()

            // 파일을 사용할 수 있도록 스트림 준비
            val outputStream = FileOutputStream(file)

            // compress 함수를 사용해 스트림에 비트맵을 저장
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            println("saveImageToCache 성공: ${file.path}")
        } catch (e: FileNotFoundException) {
            println("FileNotFoundException: ${e.message}")
        } catch (e: IOException) {
            println("IOException: ${e.message}")
        }
    }

    fun getImageFromCache(imageView: ImageView) {
        val file = File(mActivity.cacheDir.toString())
//        val fileList = file.listFiles()
        println("fileList: ${file.name}")
        val fileName = file.name

        val path = "${mActivity.cacheDir}/${fileName}"
        println("path: ${path}")
        val bitmap = BitmapFactory.decodeFile(path)
        imageView.setImageBitmap(bitmap)
    }
}