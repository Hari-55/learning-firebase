package com.hari.learningfirebase

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hari.learningfirebase.auth.LoginEmailActivity
import com.hari.learningfirebase.databinding.ActivityMainBinding
import com.hari.learningfirebase.firestore.DashBoardScActivity
import com.hari.learningfirebase.realtime.LeaderBoardActivity
import com.hari.learningfirebase.storage.IgMiniActivity

//SHA REPORT ./gradlew signingReport

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            btnAuthentication.setOnClickListener(this@MainActivity)
            btnCrashlytics.setOnClickListener(this@MainActivity)
            btnFireStore.setOnClickListener(this@MainActivity)
            btnRealtime.setOnClickListener(this@MainActivity)
            btnStorage.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAuthentication -> this.startActivity(LoginEmailActivity.startActivity(this))
            R.id.btnCrashlytics -> throw RuntimeException("Test Crash OMG")
            R.id.btnFireStore -> this.startActivity(DashBoardScActivity.startActivity(this))
            R.id.btnRealtime -> this.startActivity(LeaderBoardActivity.startActivity(this))
            R.id.btnStorage -> this.startActivity(IgMiniActivity.startActivity(this))
        }
    }
}