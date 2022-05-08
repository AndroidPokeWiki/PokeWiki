package com.example.pokewiki.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.R

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.login_activity)
        super.onCreate(savedInstanceState)
    }
}