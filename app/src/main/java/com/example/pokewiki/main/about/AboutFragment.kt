package com.example.pokewiki.main.about

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.pokewiki.CoverActivity
import com.example.pokewiki.R
import com.example.pokewiki.main.profile.advice.AdviceActivity
import com.example.pokewiki.utils.ToastUtils

class AboutFragment : Fragment(R.layout.info_fragment) {
    private lateinit var mCheckUpdateBtn: CardView
    private lateinit var mLikeAppBtn: CardView
    private lateinit var mWelcomeBtn: CardView
    private lateinit var mFeedbackBtn: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view : View) {
        mCheckUpdateBtn = view.findViewById(R.id.about_check_update)
        mCheckUpdateBtn.setOnClickListener {
            ToastUtils.getInstance(requireContext())?.showShortToast("当前已经是最新版本")
        }
        mLikeAppBtn = view.findViewById(R.id.about_like)
        mLikeAppBtn.setOnClickListener {
            ToastUtils.getInstance(requireContext())?.showShortToast("感谢支持！！")
        }
        mWelcomeBtn = view.findViewById(R.id.about_welcome)
        mWelcomeBtn.setOnClickListener {
            startActivity(
                Intent(requireContext(), CoverActivity::class.java).putExtra(
                    "type",
                    "welcome"
                )
            )
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        mFeedbackBtn = view.findViewById(R.id.about_feedback)
        mFeedbackBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AdviceActivity::class.java))
        }
    }
}