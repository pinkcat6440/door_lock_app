package com.example.ch20_firebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ch20_firebase.databinding.ActivityIntroBinding
import android.util.Log

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding // 바인딩 선언
    private val TAG = "AuthActivity" // 로그 태그 정의

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "IntroActivity onCreate 실행")
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater) // 바인딩 초기화
        setContentView(binding.root)

        binding.goToLoginButton.setOnClickListener {
            Log.d(TAG, "로그인하기 버튼 클릭")
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

    }
}