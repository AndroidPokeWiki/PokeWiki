package com.example.pokewiki.main.profile.information

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.pokewiki.R
import com.example.pokewiki.main.profile.resetpwd.ResetPwdActivity
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState

/**
 * created by DWF on 2022/5/28.
 */
class InformationActivity : AppCompatActivity() {
    private val viewModel by viewModels<InformationViewModel>()

    private lateinit var icon: ImageView
    private lateinit var username: TextView
    private lateinit var edit: ImageView
    private lateinit var resetPwd: CardView
    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_activity)

        initView()
        initViewModel()
        initViewAction()
    }

    private fun initView() {
        loading = LoadingDialogUtils(this)

        icon = findViewById(R.id.profile_edit_img)
        icon.setOnClickListener {
            TODO("访问相册，修改头像")
        }
        username = findViewById(R.id.profile_edit_user_name)
        edit = findViewById(R.id.profile_edit_btn)
        edit.setOnClickListener {
            TODO("用户名框改为EditText，图标修改为确定按钮")
        }
        resetPwd = findViewById(R.id.profile_main_collection)
        resetPwd.setOnClickListener {
            intent = Intent(this, ResetPwdActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModel.viewState.let { state ->
            state.observeState(this, InformationViewState::icon) {
//                icon.setImageURI()
                TODO()
            }
            state.observeState(this, InformationViewState::name) {
                username.text = it
            }
        }
    }

    private fun initViewAction() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is InformationViewEvent.ShowToast -> ToastUtils.getInstance(this)?.showLongToast(it.msg)
                is InformationViewEvent.ShowLoadingDialog -> loading =
                        LoadingDialogUtils.show(this, "正在修改...")
                is InformationViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }


}