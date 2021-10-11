package com.hari.learningfirebase.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.hari.learningfirebase.databinding.ActivityRegisterEmailBinding

class RegisterEmailActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) = Intent(context, RegisterEmailActivity::class.java)
    }

    private lateinit var binding: ActivityRegisterEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        with(binding) {
            loading.visibility = View.VISIBLE
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val confirmPassword = edtRepeatPassword.text.toString()
            if (validation(email, password, confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@RegisterEmailActivity) {
                    if (it.isSuccessful) {
                        loading.visibility = View.GONE
                        Toast.makeText(this@RegisterEmailActivity, "Berhasil daftar", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    } else {
                        loading.visibility = View.GONE
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            Toast.makeText(this@RegisterEmailActivity, "Password terlalu sederhana", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this@RegisterEmailActivity, "Email tidak valid", Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                this@RegisterEmailActivity,
                                "Email sudah digunakan orang lain",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@RegisterEmailActivity,
                                "gagal melakukan registrasi mohon coba lagi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun validation(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean = when {
        TextUtils.isEmpty(email) -> {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(password) -> {
            binding.edtPassword.error = "isi dulu"
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(confirmPassword) -> {
            Toast.makeText(this, "Password validasi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        password != confirmPassword -> {
            Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Toast.makeText(this, "Salah memasukan format email anda", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}