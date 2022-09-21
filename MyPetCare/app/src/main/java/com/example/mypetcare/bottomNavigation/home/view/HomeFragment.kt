package com.example.mypetcare.bottomNavigation.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.bottomNavigation.home.schedule.view.CalendarDialog
import com.example.mypetcare.database.firebase.GetUserInfo
import com.example.mypetcare.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var mBinding : FragmentHomeBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // 사용자 정보 가져오기
        GetUserInfo(requireActivity()).getUserInfo( binding.homeMyName,
                                                    binding.homeMyPhoneNum,
                                                    binding.homeMyPetName,
                                                    binding.homeMyPetAge,
                                                    binding.homeMyPetWeight,
                                                    binding.homeMyPetSpecies,
                                                    binding.homeMyPetCharacter,
                                                    requireContext()
                                                )
        // 사용자 프로필 이미지 가져오기
        GetUserInfo(requireActivity()).getProfileImage(binding.homeProfileImage)

        // 일정 확인
        binding.homeShowCalendarDialog.setOnClickListener{
            val calendarDialog = CalendarDialog(requireContext())
            calendarDialog.show()
        }

        return binding.root
    }

}