package com.hari.learningfirebase.realtime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hari.learningfirebase.BuildConfig
import com.hari.learningfirebase.databinding.ActivityLeaderBoardBinding
import kotlin.random.Random

class LeaderBoardActivity : AppCompatActivity(), OnClickLeaderBoard {

    private lateinit var binding: ActivityLeaderBoardBinding
    private val realtime = FirebaseDatabase.getInstance(BuildConfig.REALTIME_BASE).reference
    private val dataLeaderBoard = mutableListOf<LeaderBoardModel>()
    private lateinit var adapter: LeaderBoardAdapter

    companion object {
        fun startActivity(context: Context) = Intent(context, LeaderBoardActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        getLeaderBoard()
    }

    private fun initViews() {
        with(binding) {
            adapter = LeaderBoardAdapter(arrayListOf(), this@LeaderBoardActivity)
            rvLeaderBoard.apply {
                layoutManager = LinearLayoutManager(this@LeaderBoardActivity)
                adapter = this@LeaderBoardActivity.adapter
            }
            binding.btnAddLeader.setOnClickListener { addDataLeaderBoard() }
        }
    }

    private fun getLeaderBoard() {
        with(binding) {
            loading.visibility = View.VISIBLE
            //ini pakek yang selalu ambil setiap ada perubahan selama lifecycle activity tidak destroy()
            realtime.child("leaderboards").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataLeaderBoard.clear()
                    for (dataSnapshot in snapshot.children) {
                        dataSnapshot.getValue(LeaderBoardModel::class.java)?.let { dataLeaderBoard.add(it) }
                    }
                    if (dataLeaderBoard.isEmpty()) Toast.makeText(
                        this@LeaderBoardActivity,
                        "Tidak ada data",
                        Toast.LENGTH_SHORT
                    ).show()
                    else renderLeaderBoard()
                    loading.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun renderLeaderBoard() {
        dataLeaderBoard.sortByDescending { it.point }
        adapter.setData(dataLeaderBoard)
        adapter.notifyDataSetChanged()
    }

    private fun addDataLeaderBoard() {
        with(binding) {
            loading.visibility = View.VISIBLE
            val randomId = realtime.push().key ?: ""
            val random: Int = Random.nextInt(0, 1000)
            val dataNew = LeaderBoardModel(randomId, "RT ${dataLeaderBoard.size}", random.toString())
            realtime.child("leaderboards").child(randomId).setValue(dataNew)
                .addOnSuccessListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this@LeaderBoardActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this@LeaderBoardActivity, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun click(data: LeaderBoardModel) {
        with(binding) {
            val random: Int = Random.nextInt(0, 1000)
            realtime.child("leaderboards").child(data.uuid ?: "").child("point").setValue(random.toString())
                .addOnSuccessListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this@LeaderBoardActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this@LeaderBoardActivity, "Gagal mengubah data", Toast.LENGTH_SHORT).show()

                }
            //Kalo mau delete
            //realtime.child("leaderboards").child(data.uuid?: "").removeValue()
        }
    }
}