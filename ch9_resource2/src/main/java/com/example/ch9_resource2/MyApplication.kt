package com.example.ch9_resource2 // AuthActivity와 동일한 패키지여야 합니다.

import android.app.Application

// Application 클래스를 상속받습니다.
class MyApplication : Application() {

    // static(정적) 멤버처럼 동작하도록 companion object를 사용합니다.
    // 이렇게 하면 MyApplication.email, MyApplication.checkAuth() 와 같이 직접 접근할 수 있습니다.
    companion object {
        // 사용자 이메일을 저장할 변수입니다. 초기값은 null로 설정합니다.
        // 로그인 상태가 되면 실제 이메일 주소를 여기에 저장할 수 있습니다.
        var email: String? = null

        // 사용자의 인증 상태를 확인하는 함수입니다.
        // 현재 코드에서는 email 변수가 null이 아니면 로그인된 것으로 간주합니다.
        // 실제 앱에서는 Firebase Authentication 등과 연동하여 복잡한 인증 로직을 구현하게 됩니다.
        fun checkAuth(): Boolean {
            // 이메일이 null이 아니면 true (로그인 상태), null이면 false (로그아웃 상태)
            return email != null
        }
    }

    // 애플리케이션이 생성될 때 가장 먼저 호출되는 메소드입니다.
    // 앱 전반에 걸쳐 필요한 초기화 작업 (예: Firebase 초기화, 외부 라이브러리 초기화)을 여기에 수행합니다.
    override fun onCreate() {
        super.onCreate()
        // 필요한 초기화 코드를 여기에 추가
        // 예: FirebaseApp.initializeApp(this)
    }
}