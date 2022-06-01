package com.example.pokewiki.main.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import com.example.pokewiki.login.LoginActivity
import com.example.pokewiki.main.profile.advice.AdviceActivity
import com.example.pokewiki.main.profile.collection.CollectionActivity
import com.example.pokewiki.main.profile.information.InformationActivity
import com.example.pokewiki.utils.*
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ruffian.library.widget.RImageView
import qiu.niorgai.StatusBarCompat
import java.io.FileNotFoundException

class ProfileFragment : Fragment() {

    private lateinit var mIcon: RImageView
    private lateinit var mNickname: TextView
    private lateinit var mProverb: TextView
    private lateinit var mCollectionBtn: CardView
    private lateinit var mEditBtn: CardView
    private lateinit var mAdviceBtn: CardView
    private lateinit var mAutoCacheSwitch: SwitchMaterial
    private lateinit var mLogoutBtn: Button

    private lateinit var sp: SharedPreferences
    private lateinit var mInfoEditArl: ActivityResultLauncher<Intent>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 注册返回
        mInfoEditArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == STATE_CHANGE)
                    updateData()
                if (result.resultCode == NEED_RELOGIN) {
                    ToastUtils.getInstance(requireContext())?.showLongToast("登录信息过时，请重新登录")
                    mLogoutBtn.performClick()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView(view: View) {
        sp = requireActivity().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

        mIcon = view.findViewById(R.id.profile_main_icon)
        mNickname = view.findViewById(R.id.profile_main_nickname)
        mProverb = view.findViewById(R.id.profile_main_proverb)
        mCollectionBtn = view.findViewById(R.id.profile_main_collection)
        mCollectionBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CollectionActivity::class.java))
        }
        mEditBtn = view.findViewById(R.id.profile_main_edit)
        mEditBtn.setOnClickListener {
            mInfoEditArl.launch(Intent(requireContext(), InformationActivity::class.java))
        }
        mAdviceBtn = view.findViewById(R.id.profile_main_advice)
        mAdviceBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AdviceActivity::class.java))
        }
        mAutoCacheSwitch = view.findViewById(R.id.profile_main_autoCache_switch)
        mLogoutBtn = view.findViewById(R.id.profile_main_logout)
        mLogoutBtn.setOnClickListener {
            sp.edit().putString(USER_DATA, null).apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        val autoCache = AppContext.autoSave

        if (!AppContext.userData.profile_photo.isNullOrBlank())
            Glide.with(this).load(AppContext.userData.profile_photo).into(mIcon)
        else
            mIcon.setImageDrawable(
                resources.getDrawable(R.drawable.default_icon, requireActivity().theme)
            )

        mNickname.text = AppContext.userData.username
        mAutoCacheSwitch.isChecked = autoCache

        mAutoCacheSwitch.setOnClickListener {
            if (!mAutoCacheSwitch.isChecked) {
                sp.edit().putBoolean(AUTO_SAVE, false).apply()
                AppContext.autoSave = false
            } else {
                mAutoCacheSwitch.isChecked = false
                //获取权限
                if (PermissionUtils.isGrantExternalRW(
                        requireActivity(),
                        PROFILE_GET_PERMISSION_FLAG
                    )
                ) {
                    mAutoCacheSwitch.isChecked = true
                    sp.edit().putBoolean(AUTO_SAVE, true).apply()
                    AppContext.autoSave = true
                }
            }
        }
    }

    fun getPermission() {
        mAutoCacheSwitch.isChecked = true
        sp.edit().putBoolean(AUTO_SAVE, true).apply()
        AppContext.autoSave = true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateData() {
        if (!AppContext.userData.profile_photo.isNullOrBlank())
            Glide.with(this).load(AppContext.userData.profile_photo).into(mIcon)
        else
            mIcon.setImageDrawable(
                resources.getDrawable(R.drawable.default_icon, requireActivity().theme)
            )

        mNickname.text = AppContext.userData.username
    }
}