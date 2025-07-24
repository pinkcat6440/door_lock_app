package com.yourname.ch20_firebase

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        var email: String? = null

        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return currentUser != null && currentUser.isEmailVerified
        }
    }
    override fun onCreate() {
        super.onCreate()
        auth  = Firebase.auth // 여기서 FirebaseAuth 인스턴스 초기화
    }
}