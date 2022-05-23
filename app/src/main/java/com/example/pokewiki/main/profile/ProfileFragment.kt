package com.example.pokewiki.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.pokewiki.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ruffian.library.widget.RImageView

class ProfileFragment : Fragment() {

    private lateinit var mIcon : RImageView
    private lateinit var mNickname : TextView
    private lateinit var mProverb: TextView
    private lateinit var mCollectionBtn : CardView
    private lateinit var mEditBtn : CardView
    private lateinit var mAdvice: CardView
    private lateinit var mAutoCacheSwitch: SwitchMaterial
    private lateinit var mLogoutBtn : Button

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

    private fun initView(view: View){
        mIcon = view.findViewById(R.id.profile_main_icon)
        mNickname = view.findViewById(R.id.profile_main_nickname)
        mProverb = view.findViewById(R.id.profile_main_proverb)
        mCollectionBtn = view.findViewById(R.id.profile_main_collection)
        mEditBtn = view.findViewById(R.id.profile_main_edit)
        mAdvice = view.findViewById(R.id.profile_main_advice)
        mAutoCacheSwitch = view.findViewById(R.id.profile_main_autoCache_switch)
        mLogoutBtn = view.findViewById(R.id.profile_main_logout)

    }
}