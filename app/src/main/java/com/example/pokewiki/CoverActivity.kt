package com.example.pokewiki

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.login.LoginActivity
import com.ruffian.library.widget.RTextView
import qiu.niorgai.StatusBarCompat

class CoverActivity : AppCompatActivity() {
    private lateinit var mSkipBtn: RTextView
    private lateinit var mCounter: CountDownTimer
    private var ms = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.cover_activity)
        super.onCreate(savedInstanceState)

        StatusBarCompat.setStatusBarColor(this, Color.BLACK)
        window.navigationBarColor = Color.BLACK

        mSkipBtn = findViewById(R.id.cover_skip)
        mSkipBtn.setOnClickListener {
            mCounter.cancel()
            myFinish()
        }

        initAction()
    }

    private fun initAction() {
        mCounter = object : CountDownTimer(3000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                ms -= 1
                mSkipBtn.text = "跳过 $ms s"
            }

            override fun onFinish() {
                myFinish()
            }
        }
        mCounter.start()
    }

    private fun myFinish() {

        val type = intent.getStringExtra("type")
        if (type == null)
            startActivity(Intent(this, LoginActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}