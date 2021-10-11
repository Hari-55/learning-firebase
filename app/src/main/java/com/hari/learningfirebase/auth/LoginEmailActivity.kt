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
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.hari.learningfirebase.R
import com.hari.learningfirebase.databinding.ActivityLoginEmailBinding

class LoginEmailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun startActivity(context: Context) = Intent(context, LoginEmailActivity::class.java)
    }

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        cekLogin()
    }

    private fun initViews() {
        with(binding) {
            tvRegisterLogin.setOnClickListener(this@LoginEmailActivity)
            tvForgotPassword.setOnClickListener(this@LoginEmailActivity)
            tvLoginNumberPhone.setOnClickListener(this@LoginEmailActivity)
            btnLogin.setOnClickListener(this@LoginEmailActivity)
            btnLogout.setOnClickListener(this@LoginEmailActivity)
            auth = FirebaseAuth.getInstance()
        }
    }

    private fun cekLogin() {
        with(binding) {
            if (auth.currentUser != null) {
                btnLogout.visibility = View.VISIBLE
                tvStatusLogin.text = "Sudah Login :)"
            } else {
                btnLogout.visibility = View.GONE
                tvStatusLogin.text = "Belum Login :("
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvRegisterLogin -> this.startActivity(RegisterEmailActivity.startActivity(this))
            R.id.tvForgotPassword -> this.startActivity(ForgotPasswordActivity.startActivity(this))
            R.id.tvLoginNumberPhone -> this.startActivity(LoginHpActivity.startActivity(this))
            R.id.btnLogin -> login()
            R.id.btnLogout -> logout()
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this@LoginEmailActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        cekLogin()
    }

    private fun login() {
        with(binding) {
            loading.visibility = View.VISIBLE
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if (auth.currentUser == null) {
                if (validation(email, password)) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginEmailActivity) {
                        if (it.isSuccessful) {
                            Toast.makeText(this@LoginEmailActivity, "Berhasil Login", Toast.LENGTH_SHORT).show()
                            loading.visibility = View.GONE
                            cekLogin()
                        } else {
                            loading.visibility = View.GONE
                            try {
                                throw it.exception!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                Toast.makeText(
                                    this@LoginEmailActivity,
                                    "Password terlalu sederhana",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: FirebaseAuthInvalidUserException) {
                                Toast.makeText(this@LoginEmailActivity, "Akun tidak ditemukan", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LoginEmailActivity,
                                    "gagal melakukan login mohon coba lagi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@LoginEmailActivity, "Hey Anda udah login yak", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
            }
        }
    }

    private fun validation(
        email: String,
        password: String
    ): Boolean = when {
        TextUtils.isEmpty(email) -> {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(password) -> {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Toast.makeText(this, "Salah memasukan format email anda", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}