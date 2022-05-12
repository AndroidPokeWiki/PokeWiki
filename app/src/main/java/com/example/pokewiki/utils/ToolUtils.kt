package com.example.pokewiki.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import java.util.*

class ToastUtils(context: Context) {
    fun showShortToast(mString: String?) {
        if (mToast == null) {
            return
        }
        mToast!!.setText(mString)
        mToast!!.duration = Toast.LENGTH_SHORT
        mToast!!.show()
    }

    fun showLongToast(mString: String?) {
        if (mToast == null) {
            return
        }
        mToast!!.setText(mString)
        mToast!!.duration = Toast.LENGTH_LONG
        mToast!!.show()
    }

    companion object {
        private var mToastUtils: ToastUtils? = null
        private var mToast: Toast? = null
        fun getInstance(context: Context): ToastUtils? {
            if (mToastUtils == null) {
                mToastUtils = ToastUtils(context.applicationContext)
            }
            return mToastUtils
        }
    }

    init {
        if (null == mToast) {
            mToast = Toast.makeText(context.applicationContext, "", Toast.LENGTH_LONG)
        }
    }
}

class LoadingDialogUtils(context: Context) : Dialog(context, R.style.modifiedDialog) {
    companion object {
        @SuppressLint("CutPasteId")
        fun show(context: Context, message: CharSequence?): LoadingDialogUtils {
            val dialog = LoadingDialogUtils(context)
            dialog.setTitle("")
            dialog.setContentView(R.layout.loading_dialog)
            dialog.setCancelable(false)
            if (message == null || message.isEmpty()) {
                dialog.findViewById<TextView>(R.id.loading_text).visibility = View.GONE
            } else {
                val txt = dialog.findViewById<TextView>(R.id.loading_text)
                txt.text = message
            }
            Glide.with(context).load(R.drawable.pokeball_loading)
                .into(dialog.findViewById(R.id.loading_view))

            // 设置居中
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            dialog.window!!.attributes = lp
            dialog.show()
            //计时
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (dialog.isShowing) {
                        dialog.cancel()
                        Looper.prepare()
                        val mHandler = Handler(Looper.myLooper()!!) {
                            ToastUtils.getInstance(context)!!.showShortToast("未知错误!请尝试重试")
                            true
                        }
                        mHandler.sendEmptyMessage(0)
                        Looper.loop()
                    }
                }
            }, 10000)
            return dialog
        }
    }
}