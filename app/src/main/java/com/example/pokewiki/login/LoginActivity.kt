package com.example.pokewiki.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.example.pokewiki.R
import com.example.pokewiki.main.MainActivity
import com.example.pokewiki.register.RegisterActivity
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.SHARED_NAME
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var mEmailEt: EditText
    private lateinit var mPasswordEt: EditText
    private lateinit var mLoginBtn: CardView
    private lateinit var mRegisterBtn: TextView
    private lateinit var mErrorText: TextView

    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.login_activity)
        super.onCreate(savedInstanceState)

        StatusBarCompat.translucentStatusBar(this)
        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        loading = LoadingDialogUtils(this)

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
        mRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        mErrorText = findViewById(R.id.login_error_text)
    }

    private fun initViewModel() {
        // 检查是否留存用户信息，自动登录
        viewModel.dispatch(
            LoginViewAction.CheckLoginInfo(getSharedPreferences(SHARED_NAME, MODE_PRIVATE))
        )

        viewModel.viewState.let { states ->
            states.observeState(this, LoginViewState::canLogin) {
                if (it)
                    mLoginBtn.setOnClickListener {
                        viewModel.dispatch(
                            LoginViewAction.ClickLogin(
                                getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
                            )
                        )
                    }
                else
                    mLoginBtn.setOnClickListener {
                        viewModel.dispatch(LoginViewAction.ChangeErrorState(true))
                    }
            }
            states.observeState(this, LoginViewState::error, LoginViewState::errorText)
            { e, text ->
                if (e) {
                    mErrorText.visibility = View.VISIBLE

                    //错误提示抖动动画
                    AnimatorSet().apply {

                        val xDuration = 100L
                        val xOffset = 10f

                        playSequentially(
                            ObjectAnimator.ofFloat(mErrorText, "translationX", -xOffset)
                                .setDuration((xDuration / 2)),
                            ObjectAnimator.ofFloat(mErrorText, "translationX", -xOffset, xOffset)
                                .apply {
                                    duration = xDuration
                                    repeatMode = ValueAnimator.REVERSE
                                    repeatCount = 2
                                },
                            ObjectAnimator.ofFloat(mErrorText, "translationX", 0f)
                                .setDuration((xDuration / 2))
                        )
                    }.start()
                } else
                    mErrorText.visibility = View.GONE
                mErrorText.text = text
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is LoginViewEvent.ShowToast -> ToastUtils.getInstance(this)?.showLongToast(it.msg)
                is LoginViewEvent.TransIntent -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在登录...")
                is LoginViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }
}
