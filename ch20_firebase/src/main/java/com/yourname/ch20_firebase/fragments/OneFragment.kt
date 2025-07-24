package com.yourname.ch20_firebase.fragments // 본인의 패키지명에 맞게 변경해주세요

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.yourname.ch20_firebase.databinding.FragmentOneBinding
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject
import com.yourname.ch20_firebase.R
import android.util.Log
import android.app.NotificationChannel // 추가
import android.app.NotificationManager // 추가
import android.content.Context // 추가
import android.content.pm.PackageManager // 추가
import android.os.Build // Build 클래스 import (API 33 권한 요청 및 채널 생성용)
import androidx.core.app.NotificationCompat // 추가

// 모드 변경값을 플라스크 서버로 전송
class OneFragment : Fragment() {
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()
    private val serverUrl = "http://192.168.0.129:5000/mode" // 실제 서버 URL로 변경 필요

    // 사용자가 스피너에서 선택한 모드 값을 저장할 변수 (서버로 보낼 값)
    // 초기값은 "보안 모드"에 해당하는 "secure"로 설정 (UI 이미지 기준)
    private var selectedModeValue: String = "secure"

    // --- 알림 관련 상수 및 변수 시작 ---
    private val CHANNEL_ID = "mode_notification_channel"
    private val CHANNEL_NAME = "모드 변경 알림"
    private val NOTIFICATION_ID = 1001
    private val REQUEST_CODE_POST_NOTIFICATIONS = 101 // 알림 권한 요청 코드

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // **수정:** ViewBinding을 사용하여 레이아웃을 인플레이트합니다.
        // 기존: val view = inflater.inflate(R.layout.fragment_one, container, false)
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        return binding.root // 바인딩된 루트 뷰를 반환
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 알림 관련 초기화 호출 시작 ---
        createNotificationChannel() // 알림 채널 생성 (Android 8.0 이상)
        requestNotificationPermission() // 알림 권한 요청 (Android 13 이상)
        // --- 알림 관련 초기화 호출 끝 ---

        // **삭제:** 기존 findViewByID를 통한 UI 요소 초기화 및 XML ID와 연결 부분
        // modeStatusTextView = view.findViewById(R.id.modeStatusTextView)
        // ... (이 모든 findViewByID 라인들을 삭제)

        // 2. Spinner 설정
        // strings.xml에 <string-array name="mode_options"> 정의는 이미 확인되었습니다.
        val modeOptions = resources.getStringArray(R.array.mode_options)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // **수정:** ViewBinding을 사용하여 스피너에 접근합니다.
        binding.modeSelectSpinner.adapter = adapter

        // 3. Spinner 초기 선택값 설정 (디자인에 맞춰 보안 모드 선택)
        // **수정:** ViewBinding을 사용하여 스피너에 접근합니다.
        binding.modeSelectSpinner.setSelection(1) // 0: "일반 모드", 1: "보안 모드 (얼굴 인식)"

        // 4. Spinner 아이템 선택 리스너 설정
        // **수정:** ViewBinding을 사용하여 스피너에 접근합니다.
        binding.modeSelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedModeValue = when (position) {
                    0 -> "guest"
                    1 -> "secure"
                    else -> "secure"
                }
                // 스피너 선택 시에도 UI를 즉시 업데이트하도록 추가 (기존에는 '설정 적용' 시에만 업데이트)
                updateCurrentModeUI(selectedModeValue)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { /* 구현할 내용 없음 */ }
        }

        // 5. '설정 적용' 버튼 클릭 리스너 설정
        // **수정:** ViewBinding을 사용하여 버튼에 접근합니다.
        binding.applySettingsButton.setOnClickListener {
            sendModeToServer(selectedModeValue) // 기존 서버 통신 로직
            sendModeChangeNotification(selectedModeValue) // <-- 알림 발송 로직 추가
        }

        // 6. 프래그먼트 로드 시 초기 UI 상태 설정 (스피너의 초기 선택값에 맞춰 UI 업데이트)
        // **수정:** selectedModeValue를 사용하여 초기 UI를 설정
        updateCurrentModeUI(selectedModeValue)
    }

// OneFragment 클래스 내부에 추가 (onCreateView 닫는 괄호 } 바로 아래에 넣으면 됩니다.)

    /**
     * 현재 동작 모드에 따라 UI (텍스트, 아이콘, 색상, 배경, 설명)를 업데이트하는 함수
     * @param mode 현재 설정된 모드 ("guest" 또는 "secure")
     */
    private fun updateCurrentModeUI(mode: String) {
        when (mode) {
            "guest" -> {
                binding.modeStatusTextView.text = "일반 모드"
                binding.modeStatusTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_status))
                binding.modeStatusIcon.setImageResource(R.drawable.ic_check_circle)
                binding.modeStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_status))
                binding.currentModeStatusLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_blue_bg)
                binding.modeDescriptionTextView.text = "스위치나 NFC 태그로 문이 즉시 열립니다."
            }
            "secure" -> {
                binding.modeStatusTextView.text = "보안 모드 (얼굴 인식)"
                binding.modeStatusTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_status))
                binding.modeStatusIcon.setImageResource(R.drawable.ic_check_circle)
                binding.modeStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_status))
                binding.currentModeStatusLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_green_bg)
                binding.modeDescriptionTextView.text = "스위치를 누르면 얼굴 인식을 통해 문이 열립니다."
            }
        }
    }

    /**
     * 선택된 모드 값을 Flask 서버로 전송하는 함수 (기존 코드와 동일)
     * @param mode 서버로 전송할 모드 값 ("guest" 또는 "secure")
     */
    private fun sendModeToServer(mode: String) {
        Log.d("OneFragment", "Selected mode to send: $mode")
        Log.d("OneFragment", "Server URL: $serverUrl")

        val jsonObject = JSONObject().apply {
            put("mode", mode)
        }
        val jsonBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Log.d("OneFragment", "Request JSON Body: ${jsonObject.toString()}")

        val request = Request.Builder()
            .url(serverUrl)
            .post(jsonBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OneFragment", "Network request failed: ${e.message}", e)
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "서버 연결 실패: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    activity?.runOnUiThread {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), "모드 전송 성공", Toast.LENGTH_SHORT).show()
                            Log.d("OneFragment", "Server response success: ${responseBody}")
                            println("서버 응답: $responseBody")
                            // 서버 응답 성공 시 UI 업데이트는 이미 onItemSelected 또는 onCreate에서 처리되므로 여기서는 제거 가능
                            // updateCurrentModeUI(mode) // 필요시 다시 추가
                        } else {
                            Toast.makeText(requireContext(), "모드 전송 실패: ${it.code} - ${responseBody}", Toast.LENGTH_LONG).show()
                            println("서버 오류 응답: ${it.code} - ${responseBody}")
                            Log.e("OneFragment", "Server response failed: ${it.code} - ${responseBody}")
                        }
                    }
                }
            }
        })
    }

    // --- 알림 관련 함수들 추가 시작 ---

    // 알림 채널 생성 함수 (Android 8.0 이상 필수)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "도어락 모드 변경 알림"
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 알림 권한 요청 함수 (Android 13 이상 필수)
    // 이 메서드는 Fragment 클래스 내부에 있어야 합니다.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 승인됨
                Log.d("OneFragment", "Notification permission granted.")
            } else {
                // 권한 거부됨
                Log.w("OneFragment", "Notification permission denied.")
                Toast.makeText(requireContext(), "알림 권한이 거부되어 알림을 표시할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) 이상
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Fragment에서 권한 요청: requestPermissions()
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }

    // 모드 변경 알림 보내는 함수
    private fun sendModeChangeNotification(mode: String) {
        // Android 13 (API 33) 이상에서 권한이 없는 경우 알림을 보내지 않음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("OneFragment", "Cannot send notification: POST_NOTIFICATIONS permission not granted.")
            Toast.makeText(requireContext(), "알림 권한이 없어 알림을 보낼 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationTitle = "도어락 모드 변경"
        val notificationText = "${getDisplayModeName(mode)}이(가) 적용되었습니다."

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check_circle) // drawable 폴더에 ic_check_circle.xml 아이콘이 이미 있습니다.
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // 사용자가 탭하면 알림이 사라지도록 설정

        notificationManager.notify(NOTIFICATION_ID, builder.build())
        Log.d("OneFragment", "Notification sent for mode: $mode")
    }

    // "guest"나 "secure" 값을 "일반 모드", "보안 모드"로 변환하는 헬퍼 함수
    private fun getDisplayModeName(modeValue: String): String {
        return when (modeValue) {
            "guest" -> "일반 모드"
            "secure" -> "보안 모드 (얼굴 인식)"
            else -> modeValue // 예상치 못한 값일 경우 그대로 반환
        }
    }

    // --- 알림 관련 함수들 추가 끝 ---

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 뷰가 파괴될 때 바인딩 객체를 해제하여 메모리 누수 방지
    }

} // OneFragment 클래스 끝
