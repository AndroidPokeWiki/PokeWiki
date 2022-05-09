package com.example.pokewiki.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.example.pokewiki.R
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var mEmailEt: EditText
    private lateinit var mPasswordEt: EditText
    private lateinit var mLoginBtn: CardView
    private lateinit var mRegisterBtn: Button
    private lateinit var mErrorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.login_activity)
        super.onCreate(savedInstanceState)

        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        mEmailEt = findViewById(R.id.login_email_input)
        mEmailEt.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdateUsername(it.toString()))
            viewModel.dispatch(LoginViewAction.ChangeErrorState(false))
        }
        mPasswordEt = findViewById(R.id.login_password_input)
        mPasswordEt.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdatePassword(it.toString()))
            viewModel.dispatch(LoginViewAction.ChangeErrorState(false))
        }
        mLoginBtn = findViewById(R.id.login_btn)
        mRegisterBtn = findViewById(R.id.login_register_btn)
        mErrorText = findViewById(R.id.login_error_text)
    }

    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(this, LoginViewState::canLogin) {
                if (it)
                    mLoginBtn.setOnClickListener { viewModel.dispatch(LoginViewAction.ClickLogin) }
                else
                    mLoginBtn.setOnClickListener {
                        viewModel.dispatch(LoginViewAction.ChangeErrorState(true))
                    }
            }
            states.observeState(this, LoginViewState::error, LoginViewState::errorText)
            { e, text ->
                if (e)
                    mErrorText.visibility = View.VISIBLE
                else
                    mErrorText.visibility = View.GONE
                mErrorText.text = text
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is LoginViewEvent.ShowToast -> ToastUtils.getInstance(this)?.showLongToast(it.msg)
            }
        }
    }
}