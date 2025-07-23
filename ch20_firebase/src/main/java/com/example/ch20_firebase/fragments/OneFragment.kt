package com.example.ch20_firebase.fragments // 본인의 패키지명에 맞게 변경해주세요

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ch20_firebase.databinding.FragmentOneBinding
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
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
import com.example.ch20_firebase.R
import android.util.Log

// 모드 변경값을 플라스크 서버로 전송
class OneFragment : Fragment() {
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()
    private val serverUrl = "http://192.168.0.171:5000/mode" // 실제 서버 URL로 변경 필요

    // fragment_one_xml.xml에 정의된 UI 요소들을 참조할 변수들
    // 초기에는 null일 수 있으므로 nullable(?)로 선언
    private var modeStatusTextView: TextView? = null // 현재 모드 상태 텍스트
    private var modeStatusIcon: ImageView? = null     // 현재 모드 상태 아이콘
    private var currentModeStatusLayout: LinearLayout? = null // 현재 모드 상태를 감싸는 배경 레이아웃
    private var modeSelectSpinner: Spinner? = null    // 모드를 선택하는 드롭다운 (스피너)
    private var applySettingsButton: Button? = null   // '설정 적용' 버튼
    private var modeDescriptionTextView: TextView? = null // 현재 모드에 대한 설명 텍스트

    // 사용자가 스피너에서 선택한 모드 값을 저장할 변수 (서버로 보낼 값)
    // 초기값은 "보안 모드"에 해당하는 "secure"로 설정 (UI 이미지 기준)
    private var selectedModeValue: String = "secure"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_one, container, false)

        // 1. UI 요소 초기화 및 XML ID와 연결
        // 이곳의 ID들은 최종 fragment_one_xml.xml의 ID들과 정확히 일치해야 합니다.
        modeStatusTextView = view.findViewById(R.id.modeStatusTextView)
        modeStatusIcon = view.findViewById(R.id.modeStatusIcon)
        currentModeStatusLayout = view.findViewById(R.id.currentModeStatusLayout)
        modeSelectSpinner = view.findViewById(R.id.modeSelectSpinner)
        applySettingsButton = view.findViewById(R.id.applySettingsButton)
        modeDescriptionTextView = view.findViewById(R.id.modeDescriptionTextView)

        // 2. Spinner 설정
        // strings.xml에 <string-array name="mode_options"> 정의 필요
        val modeOptions = resources.getStringArray(R.array.mode_options)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modeSelectSpinner?.adapter = adapter

        // 3. Spinner 초기 선택값 설정 (디자인에 맞춰 보안 모드 선택)
        modeSelectSpinner?.setSelection(1) // 0: "일반 모드", 1: "보안 모드 (얼굴 인식)"

        // 4. Spinner 아이템 선택 리스너 설정
        modeSelectSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 5. '설정 적용' 버튼 클릭 리스너 설정
        applySettingsButton?.setOnClickListener {
            sendModeToServer(selectedModeValue)
        }

        // 6. 프래그먼트 로드 시 초기 UI 상태 설정 (디자인에 맞춰 보안 모드로 초기화)
        updateCurrentModeUI("secure")

        return view // 인플레이트된 뷰 반환
    }
// OneFragment 클래스 내부에 추가 (onCreateView 닫는 괄호 } 바로 아래에 넣으면 됩니다.)

    /**
     * 현재 동작 모드에 따라 UI (텍스트, 아이콘, 색상, 배경, 설명)를 업데이트하는 함수
     * @param mode 현재 설정된 모드 ("guest" 또는 "secure")
     */
    private fun updateCurrentModeUI(mode: String) {
        modeStatusTextView?.let { textView ->
            modeStatusIcon?.let { iconView ->
                currentModeStatusLayout?.let { layout ->
                    modeDescriptionTextView?.let { descTextView ->
                        when (mode) {
                            "guest" -> {
                                textView.text = "일반 모드"
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_status))
                                iconView.setImageResource(R.drawable.ic_check_circle)
                                iconView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_status))
                                layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_blue_bg)
                                descTextView.text = "스위치나 NFC 태그로 문이 즉시 열립니다."
                            }
                            "secure" -> {
                                textView.text = "보안 모드 (얼굴 인식)"
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_status))
                                iconView.setImageResource(R.drawable.ic_check_circle)
                                iconView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_status))
                                layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_green_bg)
                                descTextView.text = "스위치를 누르면 얼굴 인식을 통해 문이 열립니다."
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 선택된 모드 값을 Flask 서버로 전송하는 함수
     * @param mode 서버로 전송할 모드 값 ("guest" 또는 "secure")
     */
    private fun sendModeToServer(mode: String) {
        Log.d("OneFragment", "Selected mode to send: $mode") // 추가
        Log.d("OneFragment", "Server URL: $serverUrl") // 추가

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
                            println("서버 응답: $responseBody") // Logcat에서 확인 가능
                            updateCurrentModeUI(mode)
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

} // end
