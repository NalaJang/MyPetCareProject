package com.example.mypetcare.bottomNavigation.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypetcare.bottomNavigation.home.schedule.view.CalendarDialog
import com.example.mypetcare.database.Cache
import com.example.mypetcare.database.firebase.ProfileImage
import com.example.mypetcare.database.firebase.GetUserInfo
import com.example.mypetcare.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class HomeFragment : Fragment() {

    private var mBinding : FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val getUserInfo = GetUserInfo()
        val profileImage = ProfileImage(requireActivity(), uid!!)

        // 사용자 정보 가져오기
        getUserInfo.getUserInfo(binding.homeMyName,
                                binding.homeMyPhoneNum,
                                binding.homeMyPetName,
                                binding.homeMyPetAge,
                                binding.homeMyPetWeight,
                                binding.homeMyPetSpecies,
                                binding.homeMyPetCharacter,
                                requireContext()
                            )

        val cache = Cache(requireActivity(), "${uid}.png")
        // 사용자 프로필 이미지 가져오기
        // 캐시에서 bitmap 이미지 가져오기
        val getImage: Boolean = cache.getImageFromCache(binding.homeProfileImage)

        if( !getImage ) {
            profileImage.setBasicProfileImage(requireContext())
            profileImage.getProfileImage(binding.homeProfileImage)
        }

        // 일정 확인
        binding.homeShowCalendarDialog.setOnClickListener{
            val calendarDialog = CalendarDialog(requireActivity())
            calendarDialog.show()
        }

        return binding.root
    }

}