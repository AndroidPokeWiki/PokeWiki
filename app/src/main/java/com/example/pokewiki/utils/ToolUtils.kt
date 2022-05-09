package com.example.pokewiki.utils

import android.content.Context
import android.widget.Toast

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