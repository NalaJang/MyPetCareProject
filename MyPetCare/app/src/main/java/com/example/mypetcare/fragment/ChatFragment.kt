package com.example.mypetcare.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.R
import com.example.mypetcare.databinding.FragmentChatBinding
import com.example.mypetcare.databinding.FragmentProfileBinding

class ChatFragment : Fragment() {

    private var mBinding : FragmentChatBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

}