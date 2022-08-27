package com.example.mypetcare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mypetcare.R
import com.example.mypetcare.databinding.ActivityBottomNavigationBinding
import com.example.mypetcare.fragment.ChatFragment
import com.example.mypetcare.fragment.HomeFragment
import com.example.mypetcare.fragment.SettingFragment
import java.lang.IllegalArgumentException

class BottomNavigation : AppCompatActivity() {

    private var mBinding: ActivityBottomNavigationBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 처음에 보여줄 fragment
        supportFragmentManager.beginTransaction().replace(R.id.bottom_container, HomeFragment()).commit()

        // bottomNavigationView 세팅
       initNavigationView()

    }

    private fun initNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            replaceFragment(
                when(it.itemId) {
                    R.id.menu_home -> HomeFragment()
                    R.id.menu_chat -> ChatFragment()
                    R.id.menu_setting -> SettingFragment()
                    else -> throw IllegalArgumentException("not found menu item id.")
                }
            )

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.bottom_container, fragment).commit()
    }
}