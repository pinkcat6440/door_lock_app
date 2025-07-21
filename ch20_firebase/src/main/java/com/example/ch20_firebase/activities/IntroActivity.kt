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
        //setSupportActionBar(binding.introToolbar)
        //setContentView(R.layout.activity_intro)

        /*Log.d(TAG, "setOnClickListener 실행")
        // activity_intro.xml에 있는 "인증" 버튼의 ID를 사용하여 클릭 리스너 설정
        binding.introAuthButton.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)*/

        // 무조건 "로그인하기" 버튼이 보이도록 함
        // 자동로그인 안됨
        binding.goToLoginButton.setOnClickListener {
            Log.d(TAG, "로그인하기 버튼 클릭")
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            // IntroActivity는 뒤로가기 시 다시 돌아올 수 있도록 finish() 호출 안함
        }
        /*enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets*/
    }
}