package com.example.pokewiki

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.login.LoginActivity

class CoverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.cover_activity)
        super.onCreate(savedInstanceState)

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