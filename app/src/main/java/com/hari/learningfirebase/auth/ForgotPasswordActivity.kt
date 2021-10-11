package com.hari.learningfirebase.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hari.learningfirebase.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) = Intent(context, ForgotPasswordActivity::class.java)
    }

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSendEmail.setOnClickListener {
            sendResetPassword()
        }
    }

    private fun sendResetPassword() {
        with(binding) {
            loading.visibility = View.VISIBLE
            val email = edtEmail.text.toString()
            if (validation(email)) {
                Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener(this@ForgotPasswordActivity) {
                    if (it.isSuccessful) {
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Berhasil kirim ke email, silakan cek bosQ",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Gagal Ngirim Email (Tidak Terdaftar)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun validation(email: String): Boolean = when {
        TextUtils.isEmpty(email) -> {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Toast.makeText(this, "Salah memasukan format email anda", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}