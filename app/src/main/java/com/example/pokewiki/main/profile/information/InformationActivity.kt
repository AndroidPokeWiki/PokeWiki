package com.example.pokewiki.main.profile.information

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import com.example.pokewiki.main.profile.resetPwd.ResetPwdActivity
import com.example.pokewiki.utils.*
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat
import java.io.File
import java.io.IOException

/**
 * created by DWF on 2022/5/28.
 */
class InformationActivity : AppCompatActivity() {
    private val viewModel by viewModels<InformationViewModel>()

    private lateinit var mIconIv: ImageView
    private lateinit var mUsernameEt: EditText
    private lateinit var mEditBtn: ImageButton
    private lateinit var mResetPwdBtn: CardView
    private lateinit var mBackBtn: ImageButton
    private lateinit var loading: LoadingDialogUtils
    private lateinit var dialog: BottomDialogUtils

    private lateinit var mCamaraArl: ActivityResultLauncher<Uri>
    private lateinit var mAlbumArl: ActivityResultLauncher<String>

    private lateinit var photoImage: File
    private lateinit var imageUri: Uri

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_activity)

        StatusBarCompat.setStatusBarColor(
            this,
            resources.getColor(R.color.poke_ball_red, theme)
        )

        initRegister()
        initView()
        initViewModel()
        initViewAction()
    }

    private fun initRegister() {
        sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)

        mCamaraArl = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            if (photoImage.length() > 0)
                viewModel.dispatch(InformationViewAction.ChangeIcon(photoImage, sp))
        }

        mAlbumArl = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result: Uri? ->
            if (result != null) {
                val imagePath = FileManagerUtils.getFilePath(this, result)
                if (imagePath != null)
                    viewModel.dispatch(InformationViewAction.ChangeIcon(File(imagePath), sp))
            }
        }
    }


    private fun initView() {
        loading = LoadingDialogUtils(this)
        dialog = BottomDialogUtils(this) {
            when (it.id) {
                R.id.dialog_photo_btn -> {
                    dialog.dismiss()
                    openCamara()
                }
                R.id.dialog_album_btn -> {
                    dialog.dismiss()
                    mAlbumArl.launch("image/*")
                }
                R.id.dialog_cancel_btn -> dialog.dismiss()
            }
        }

        mIconIv = findViewById(R.id.profile_edit_img)
        mIconIv.setOnClickListener {
            if (PermissionUtils.isGrantExternalRW(this, PROFILE_EDIT_GET_PERMISSION_FLAG))
                dialog.showAtLocation(findViewById(R.id.profile_edit_bg), Gravity.BOTTOM, 0, 0)
        }
        mUsernameEt = findViewById(R.id.profile_edit_user_name)
        mEditBtn = findViewById(R.id.profile_edit_btn)
        mResetPwdBtn = findViewById(R.id.profile_main_collection)
        mResetPwdBtn.setOnClickListener {
            intent = Intent(this, ResetPwdActivity::class.java)
            startActivity(intent)
        }
        mBackBtn = findViewById(R.id.profile_edit_back_btn)
        mBackBtn.setOnClickListener { finish() }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViewModel() {
        viewModel.dispatch(InformationViewAction.InitData)

        viewModel.viewState.let { state ->
            state.observeState(this, InformationViewState::icon) {
                Log.e("TAG", "initViewModel: $it")
                if (it.isNullOrBlank()) {
                    mIconIv.setImageDrawable(resources.getDrawable(R.drawable.default_icon, theme))
                } else
                    Glide.with(this).load(it).into(mIconIv)
            }
            state.observeState(this, InformationViewState::name) {
                mUsernameEt.setText(it)
            }
            state.observeState(this, InformationViewState::isChanged) {
                if (it)
                    setResult(STATE_CHANGE)
            }
            state.observeState(this, InformationViewState::state) {
                when (it) {
                    InformationViewState.TOKEN_OUT_OF_DATE -> {
                        setResult(NEED_RELOGIN)
                        finish()
                    }
                    InformationViewState.FAIL -> {
                        // 修改失败返回
                        mUsernameEt.setText(AppContext.userData.username)
                        if (AppContext.userData.profile_photo.isNullOrBlank()) {
                            mIconIv.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.default_icon,
                                    theme
                                )
                            )
                        } else
                            Glide.with(this).load(it).into(mIconIv)
                    }
                }
            }
            state.observeState(this, InformationViewState::changeState) {
                mUsernameEt.alpha = if (it) 1f else 0.5f
                if (!it) {
                    mEditBtn.setImageDrawable(resources.getDrawable(R.drawable.edit_icon, theme))
                    mEditBtn.setOnClickListener {
                        mUsernameEt.isFocusable = true
                        mUsernameEt.isFocusableInTouchMode = true
                        mUsernameEt.requestFocus()
                        viewModel.dispatch(InformationViewAction.SwitchState)

                        // 打开软键盘
                        mUsernameEt.postDelayed({
                            val inputManager: InputMethodManager =
                                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputManager.showSoftInput(mUsernameEt, 0)
                        }, 200)
                    }
                } else {
                    mEditBtn.setImageDrawable(resources.getDrawable(R.drawable.edit_check, theme))
                    mEditBtn.setOnClickListener {
                        mUsernameEt.isFocusable = false
                        mUsernameEt.isFocusableInTouchMode = false
                        viewModel.dispatch(InformationViewAction.SwitchState)

                        // 关闭软键盘
                        val manager: InputMethodManager =
                            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.hideSoftInputFromWindow(mUsernameEt.windowToken, 0)

                        viewModel.dispatch(InformationViewAction.UpdateUsername(mUsernameEt.editableText.toString()))
                        // 连接后台
                        viewModel.dispatch(
                            InformationViewAction.ClickToChangeUsername(
                                getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initViewAction() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is InformationViewEvent.ShowToast -> ToastUtils.getInstance(this)
                    ?.showLongToast(it.msg)
                is InformationViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在修改...")
                is InformationViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }

    //打开相机
    private fun openCamara() {
        //创建file对象，用于储存拍照后的图片，getExternalCacheDir() : 将照片存放在手机的关联缓存目录下
        photoImage = File(externalCacheDir, "user_icon.jpg")
        try {
            if (photoImage.exists()) {
                photoImage.delete()
            }
            photoImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        imageUri =
                //如果系统版本大于7.0，调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象
            FileProvider.getUriForFile(
                this,
                "edu.example.pokewiki.fileprovider",
                photoImage
            )
        //调用launcher启动相机
        mCamaraArl.launch(imageUri)
    }

    // 获取权限回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PROFILE_EDIT_GET_PERMISSION_FLAG) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialog.showAtLocation(findViewById(R.id.profile_edit_bg), Gravity.BOTTOM, 0, 0)
            } else {
                Toast.makeText(
                    this, "拒绝了相关权限，应用无法正常运行，请尝试重新授权",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}