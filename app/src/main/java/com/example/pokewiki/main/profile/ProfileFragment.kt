package com.example.pokewiki.main.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import com.example.pokewiki.utils.*
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ruffian.library.widget.RImageView
import qiu.niorgai.StatusBarCompat

class ProfileFragment : Fragment() {

    private lateinit var mIcon: RImageView
    private lateinit var mNickname: TextView
    private lateinit var mProverb: TextView
    private lateinit var mCollectionBtn: CardView
    private lateinit var mEditBtn: CardView
    private lateinit var mAdvice: CardView
    private lateinit var mAutoCacheSwitch: SwitchMaterial
    private lateinit var mLogoutBtn: Button

    private lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarCompat.translucentStatusBar(requireActivity())

        initView(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView(view: View) {
        mIcon = view.findViewById(R.id.profile_main_icon)
        mNickname = view.findViewById(R.id.profile_main_nickname)
        mProverb = view.findViewById(R.id.profile_main_proverb)
        mCollectionBtn = view.findViewById(R.id.profile_main_collection)
        mEditBtn = view.findViewById(R.id.profile_main_edit)
        mAdvice = view.findViewById(R.id.profile_main_advice)
        mAutoCacheSwitch = view.findViewById(R.id.profile_main_autoCache_switch)
        mLogoutBtn = view.findViewById(R.id.profile_main_logout)

        sp = requireActivity().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        val autoCache = AppContext.autoSave

        Log.e("TAG", "initView: ${AppContext.userData.profile_photo}")
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
}