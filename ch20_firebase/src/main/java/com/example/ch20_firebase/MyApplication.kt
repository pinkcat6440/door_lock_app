package com.example.ch20_firebase

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        //lateinit var authService: FirebaseAuth
        var email: String? = null

        fun checkAuth(): Boolean {
            //val currentUser = auth.currentUser
            val currentUser = auth.currentUser
            return currentUser != null && currentUser.isEmailVerified
            //return currentUser?.let {
               // email = currentUser.email
                //currentUser.isEmailVerified
                //if (currentUser.isEmailVerified) {
                    //true // currentUser가 null이 아니면 로그인된 것으로 간주
                //} else {
                    //false
                //}
            //} ?: let {
            //} ?: false
                //false
            //}
        }
    }
    override fun onCreate() {
        super.onCreate()
        //auth = Firebase.auth
        //db = FirebaseFirestore.getInstance()
        //storage = Firebase.storage
        auth  = Firebase.auth // 여기서 FirebaseAuth 인스턴스 초기화
    }
}