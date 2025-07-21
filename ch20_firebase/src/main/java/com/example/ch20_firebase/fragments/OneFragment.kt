package com.example.ch20_firebase.fragments // 본인의 패키지명에 맞게 변경해주세요

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ch20_firebase.databinding.FragmentOneBinding

class OneFragment : Fragment() {
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!

    // selectedModeValue는 이제 UI 표시 외에 직접적인 상태 저장 용도만 됨 (저장 버튼이 없으므로)
    // private var selectedModeValue: Int = -1 // 필요하다면 유지

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        val view = binding.root

        // 초기 모드 상태 텍스트 설정
        binding.modeStatusTextView.text = "현재 모드: "

        // 게스트 모드 버튼 클릭 리스너
        binding.guestModeButton.setOnClickListener {
            val modeValue = 0 // 게스트 모드는 0으로 가정
            binding.modeStatusTextView.text = "현재 모드: 게스트 모드" // 선택한 버튼 텍스트 바로 반영
            Toast.makeText(context, "게스트 모드가 선택되었고, 설정이 전송됩니다.", Toast.LENGTH_SHORT).show()
            // 게스트 모드 선택 시 라즈베리파이로 바로 값 전송
            sendModeToRaspberryPi(modeValue)
        }

        // 보안 모드 버튼 클릭 리스너
        binding.securityModeButton.setOnClickListener {
            val modeValue = 1 // 보안 모드는 1로 가정
            binding.modeStatusTextView.text = "현재 모드: 보안 모드" // 선택한 버튼 텍스트 바로 반영
            Toast.makeText(context, "보안 모드가 선택되었고, 설정이 전송됩니다.", Toast.LENGTH_SHORT).show()
            // 보안 모드 선택 시 라즈베리파이로 바로 값 전송
            sendModeToRaspberryPi(modeValue)
        }

        // "설정 저장" 버튼이 없어졌으므로, 해당 버튼에 대한 로직은 제거됩니다.

        return view
    }

    // 모드 값을 라즈베리파이로 전송하는 함수
    private fun sendModeToRaspberryPi(mode: Int) {
        // 여기에 라즈베리파이로 데이터를 전송하는 실제 로직을 구현합니다.
        // 예: HTTP 요청 (Retrofit, OkHttp 등 사용), 블루투스 통신 등
        println("모드($mode)를 라즈베리파이로 즉시 전송 요청...")
        // 실제 통신이 성공했을 때 사용자에게 알림을 줄 수 있습니다.
        // Toast.makeText(context, "모드 설정 전송 완료! (값: $mode)", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}