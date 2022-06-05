package com.example.pokewiki

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.bean.UserBean
import com.example.pokewiki.login.LoginActivity
import com.example.pokewiki.main.MainActivity
import com.example.pokewiki.utils.AppContext
import com.example.pokewiki.utils.SHARED_NAME
import com.example.pokewiki.utils.USER_DATA
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
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
        val sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
        if (type == null) {
            val data = sp.getString(USER_DATA, null)
            if (!data.isNullOrBlank()) {
                try {
                    val userInfo =
                        Gson().fromJson<UserBean>(data, object : TypeToken<UserBean>() {}.type)
                    AppContext.userData = userInfo
                    startActivity(Intent(this, MainActivity::class.java))
                } catch (e: JsonParseException) {
                    Log.e("ERROR!", "checkLoginInfo: 无法解析存储json\n json:${data}")
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            } else
                startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}