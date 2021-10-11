package com.hari.learningfirebase.firestore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hari.learningfirebase.databinding.ActivityDashBoardScActivityBinding

//SC = Study Club
//Gak ngerti cara kerja firestore ?, baca berikut
//https://firebase.google.com/docs/firestore/data-model?authuser=0

class DashBoardScActivity : AppCompatActivity(), OnClickDashBoard {

    companion object {
        fun startActivity(context: Context) = Intent(context, DashBoardScActivity::class.java)
    }

    private lateinit var binding: ActivityDashBoardScActivityBinding
    private lateinit var adapter: StudyClubAdapter
    private val dataStudyClub = mutableListOf<ParticipantModel>()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardScActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            this.startActivity(RegisterScActivity.startActivity(this))
        }
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getDataDashBoard()
    }

    private fun initViews() {
        with(binding) {
            adapter = StudyClubAdapter(arrayListOf(), this@DashBoardScActivity)
            rvStudyClub.apply {
                layoutManager = LinearLayoutManager(this@DashBoardScActivity)
                setHasFixedSize(false)
                adapter = this@DashBoardScActivity.adapter
            }
        }
    }

    private fun getDataDashBoard() {
        with(binding) {
            loading.visibility = View.VISIBLE
            db.collection("participant").get()
                .addOnSuccessListener {
                    dataStudyClub.clear()
                    for (document in it) {
                        val data = document.toObject(ParticipantModel::class.java)
                        dataStudyClub.add(data)
                    }
                    if (dataStudyClub.isEmpty()) renderToast("Tidak ada data")
                    else renderDashBoard()
                    loading.visibility = View.GONE
                }
                .addOnFailureListener {
                    renderToast("Terjadi Keselahan gagal mendapatkan data")
                    loading.visibility = View.GONE
                }
        }
    }

    private fun renderDashBoard() {
        adapter.setData(dataStudyClub)
        adapter.notifyDataSetChanged()
    }

    override fun click(data: ParticipantModel, status: StatusUpdateEnum) {
        with(binding) {
            if (status == StatusUpdateEnum.DELETE) {
                loading.visibility = View.VISIBLE
                db.collection("participant").document(data.name ?: "").delete()
                    .addOnSuccessListener {
                        renderToast("Peserta berhasil dihapus")
                        loading.visibility = View.GONE
                        getDataDashBoard()
                    }
                    .addOnFailureListener {
                        renderToast("Gagal Menghapus Peserta")
                        loading.visibility = View.GONE
                    }
            } else {
                loading.visibility = View.VISIBLE
                db.collection("participant").document(data.name ?: "").update("statusAcc", true)
                    .addOnSuccessListener {
                        loading.visibility = View.GONE
                        getDataDashBoard()
                    }
                    .addOnFailureListener {
                        loading.visibility = View.GONE
                        renderToast("Gagal Mengupdate Status")
                    }
            }
        }
    }

    private fun renderToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}