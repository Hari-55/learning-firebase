package com.hari.learningfirebase.firestore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hari.learningfirebase.databinding.ActivityRegisterScBinding

class RegisterScActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) = Intent(context, RegisterScActivity::class.java)
    }

    private lateinit var binding: ActivityRegisterScBinding
    private val db = Firebase.firestore
    private val dataClass = mutableListOf<ClassModel>()
    private val fullText = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterScBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getListClass()
        binding.btnRegister.setOnClickListener {
            registerSc()
        }
    }

    private fun getListClass() {
        with(binding) {
            loading.visibility = View.VISIBLE
            db.collection("class").get()
                .addOnSuccessListener {
                    for (document in it) {
                        val data = document.toObject(ClassModel::class.java)
                        dataClass.add(data)
                    }
                    loading.visibility = View.GONE
                    renderListClass()
                }
                .addOnFailureListener {
                    renderToast("Gagal Mendapatkan data class")
                    loading.visibility = View.GONE
                }
        }
    }

    private fun renderListClass() {
        dataClass.forEach { fullText.add("${it.name} | ${it._class} - ${it.day}") }
        val adapterClass = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullText)
        binding.edtClass.setAdapter(adapterClass)
    }

    private fun registerSc() {
        with(binding) {
            loading.visibility = View.VISIBLE
            val name = edtName.text.toString()
            val nim = edtNim.text.toString()
            val chosenClass = edtClass.text.toString()
            if (validation(name, nim, chosenClass)) {
                val position = fullText.getPositionSelected(chosenClass)
                val dataClass = this@RegisterScActivity.dataClass[position]
                val data = ParticipantModel(name, nim, dataClass._class, dataClass.name, dataClass.day, false)
                db.collection("participant").document(name).set(data)
                    .addOnSuccessListener {
                        renderToast("Berhasil Mendaftar")
                        edtName.setText("")
                        edtNim.setText("")
                        edtClass.setText("")
                        loading.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        renderToast("Gagal Mendaftar")
                        loading.visibility = View.GONE
                    }
            } else {
                loading.visibility = View.GONE
            }
        }
    }

    private fun validation(name: String, nim: String, _class: String): Boolean = when {
        TextUtils.isEmpty(name) -> {
            renderToast("Nama Belum diisi")
            false
        }
        TextUtils.isEmpty(nim) -> {
            renderToast("NIM Belum diisi")
            false
        }
        TextUtils.isEmpty(_class) -> {
            renderToast("Pilih Kelas dulu")
            false
        }
        !fullText.cekValidateClass(_class) -> {
            renderToast("Salah Memasukan Pilihan Kelas")
            false
        }
        else -> true
    }

    private fun renderToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun List<String>.getPositionSelected(name: String) = this.indexOf(name)

    private fun List<String>.cekValidateClass(name: String) = this.any { it == name }
}