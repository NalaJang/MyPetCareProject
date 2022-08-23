package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.R
import com.example.mypetcare.databinding.FragmentProfileBinding
import com.example.mypetcare.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var mBinding : FragmentSettingBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

}