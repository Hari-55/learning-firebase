package com.hari.learningfirebase.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hari.learningfirebase.databinding.ActivityLoginHpBinding

class LoginHpActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) = Intent(context, LoginHpActivity::class.java)
    }

    private lateinit var binding: ActivityLoginHpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginHpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        auth = Firebase.auth
        //Hp lagi rusak. Sementara belum buat, buat dokumentasi auth sebagai berikut
        //https://firebase.google.com/docs/auth/android/phone-auth?authuser=0
        //https://github.com/firebase/snippets-android/blob/8184cba2c40842a180f91dcfb4a216e721cc6ae6/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/PhoneAuthActivity.kt
    }
}