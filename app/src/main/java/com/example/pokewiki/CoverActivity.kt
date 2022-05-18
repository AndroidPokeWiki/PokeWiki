package com.example.pokewiki

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.login.LoginActivity
import qiu.niorgai.StatusBarCompat

class CoverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.cover_activity)
        super.onCreate(savedInstanceState)

        StatusBarCompat.translucentStatusBar(this)
        initAction()
    }

    private fun initAction(){
        Thread{
            Thread.sleep( 3000)
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }.start()
    }
}