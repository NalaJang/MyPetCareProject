package com.example.mypetcare.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mypetcare.R
import com.example.mypetcare.activity.BottomNavigation
import com.example.mypetcare.activity.LoginActivity
import com.example.mypetcare.databinding.FragmentSettingBinding
import com.example.mypetcare.dialog.MyProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment(), View.OnClickListener {

    private var mBinding : FragmentSettingBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.settingMyProfile.setOnClickListener(this)
        binding.settingLogout.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // 프로필 편집
            R.id.setting_myProfile -> {
                val myProfileDialog = MyProfile(requireContext())
                myProfileDialog.show()
            }

            // 로그아웃
            R.id.setting_logout -> {
                Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                Firebase.auth.signOut()

                val activity: BottomNavigation = context as BottomNavigation
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                activity.finish()
            }
        }
    }

}