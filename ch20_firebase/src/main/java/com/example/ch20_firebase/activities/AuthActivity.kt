package com.example.ch20_firebase.activities
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ch20_firebase.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log
import com.example.ch20_firebase.MyApplication
import com.example.ch20_firebase.fragments.DashboardActivity
import com.example.ch20_firebase.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val TAG = "AuthActivity" // 로그 태그 정의
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인증 처리 전 작업
        auth = Firebase.auth
        Log.d(TAG, " onCreate 실행: $auth")


        // Google 로그인 처리 부분
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { firebaseAuthTask ->
                        if (firebaseAuthTask.isSuccessful) {
                            MyApplication.email = account.email
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "구글 로그인 실패: ${firebaseAuthTask.exception?.message}", Toast.LENGTH_LONG).show()
                            Log.e(TAG, "Firebase 인증 실패", firebaseAuthTask.exception)
                        }
                    }

            } catch (e: Exception) {
                Toast.makeText(this, "Google 로그인 실패: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Google Sign-In 오류", e)
            }
        }

        // 구글 로그인 버튼 클릭
        binding.googleLoginBtn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.ch20_firebase.R.string.default_web_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        // 회원가입
        binding.signBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString().trim()
            val password = binding.authPasswordEditView.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                        Toast.makeText(this, "회원가입 성공. 인증 메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
                        binding.authEmailEditView.setText("")
                        binding.authPasswordEditView.setText("")
                    } else {
                        Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "회원가입 실패", task.exception)
                    }
                }
        }

        // 로그인
        binding.loginBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString().trim()
            val password = binding.authPasswordEditView.text.toString().trim()

            // 1. 로그인 버튼 클릭 시점 로그 추가
            Log.d(TAG, "로그인 버튼 클릭됨. 이메일: $email")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "이메일 또는 비밀번호가 비어있습니다.")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 2. 로그인 성공 시점 로그 추가
                        Log.d(TAG, "Firebase 로그인 성공!") //
                        if (auth.currentUser?.isEmailVerified == true) {
                            MyApplication.email = email
                            Log.d(TAG, "이메일 인증 완료, DashboardActivity로 이동.") // 이메일 인증 완료 로그
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show()
                            MyApplication.auth.signOut()
                            Log.w(TAG, "이메일이 인증되지 않았습니다. 로그아웃 처리.")
                        }
                    } else {
                        Toast.makeText(this, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "로그인 실패: ${task.exception?.message}", task.exception)
                    }
                }
        }
    }
}