package com.example.pokewiki.main.profile.resetPwd

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.example.pokewiki.R
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.SHARED_NAME
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState

/**
 * created by DWF on 2022/5/28.
 */
class ResetPwdActivity : AppCompatActivity() {
    private val viewModel by viewModels<ResetPwdViewModel>()

    private lateinit var mBackBtn: ImageButton
    private lateinit var mOldPassword: EditText
    private lateinit var mNewPassword: EditText
    private lateinit var mConfirmBtn: CardView
    private lateinit var loading: LoadingDialogUtils

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_change_password_acticity)

        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
        loading = LoadingDialogUtils(this)

        mBackBtn = findViewById(R.id.profile_change_password_back_btn)
        mBackBtn.setOnClickListener { finish() }

        mOldPassword = findViewById(R.id.profile_change_password_old_password_text)
        mOldPassword.addTextChangedListener {
            val oldPassword = mOldPassword.text.toString()
            viewModel.dispatch(ResetPwdViewAction.UpdateOldPassword(oldPassword))
        }

        mNewPassword = findViewById(R.id.profile_change_password_new_password_text)
        mNewPassword.addTextChangedListener {
            val newPassword = mNewPassword.text.toString()
            viewModel.dispatch(ResetPwdViewAction.UpdateNewPassword(newPassword))
        }

        mConfirmBtn = findViewById(R.id.profile_change_password_confirm_btn)

    }

    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(this, ResetPwdViewState::canReset) {
                mConfirmBtn.alpha = if (it) 1F else 0.5F
                mConfirmBtn.isClickable = it

                if (it)
                    mConfirmBtn.setOnClickListener {
                        viewModel.dispatch(ResetPwdViewAction.ClickResetPWD(sp))
                    }
            }
            states.observeState(this, ResetPwdViewState::changeState) {
                if (it)
                    finish()
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is ResetPwdViewEvent.ShowToast -> ToastUtils.getInstance(this)
                    ?.showLongToast(it.msg)
                is ResetPwdViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在修改...")
                is ResetPwdViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }

}
