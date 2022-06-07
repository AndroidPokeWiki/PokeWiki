package com.example.pokewiki.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.example.pokewiki.R
import com.example.pokewiki.login.LoginActivity
import com.example.pokewiki.main.MainActivity
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.SHARED_NAME
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat

/**
 * created by DWF on 2022/5/9.
 */
class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel>()

    private lateinit var mEmailEt: EditText
    private lateinit var mPasswordEt: EditText
    private lateinit var mConfirmEt: EditText
    private lateinit var mRegisterBtn: CardView
    private lateinit var mErrorText: TextView

    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.register_activity)
        super.onCreate(savedInstanceState)

        StatusBarCompat.translucentStatusBar(this)
        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        loading = LoadingDialogUtils(this)

        mEmailEt = findViewById(R.id.register_email_input)
        mEmailEt.addTextChangedListener {
            viewModel.dispatch(RegisterViewAction.UpdateUsername(it.toString()))
            viewModel.dispatch(RegisterViewAction.ChangeErrorState(false))
        }
        mPasswordEt = findViewById(R.id.register_password_input)
        mPasswordEt.addTextChangedListener {
            viewModel.dispatch(RegisterViewAction.UpdatePassword(it.toString()))
            viewModel.dispatch(RegisterViewAction.ChangeErrorState(false))
        }
        mConfirmEt = findViewById(R.id.register_password_confirm_input)
        mConfirmEt.addTextChangedListener {
            viewModel.dispatch(RegisterViewAction.UpdateConfirm(it.toString()))
            viewModel.dispatch(RegisterViewAction.ChangeErrorState(false))
        }
        mRegisterBtn = findViewById(R.id.register_btn)
        mErrorText = findViewById(R.id.register_error_text)
    }

    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(this, RegisterViewState::canRegister) {
                if (it == RegisterViewState.SUCCESS)
                    mRegisterBtn.setOnClickListener {
                        viewModel.dispatch(
                            RegisterViewAction.ClickRegister(
                                getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
                            )
                        )
                    }
                else
                    mRegisterBtn.setOnClickListener {
                        viewModel.dispatch(RegisterViewAction.ChangeErrorState(true))
                    }
            }
            states.observeState(this, RegisterViewState::error, RegisterViewState::errorText)
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
                is RegisterViewEvent.ShowToast -> ToastUtils.getInstance(this)
                    ?.showLongToast(it.msg)
                is RegisterViewEvent.TransIntent -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is RegisterViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在注册...")
                is RegisterViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}