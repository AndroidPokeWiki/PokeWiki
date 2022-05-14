package com.example.pokewiki.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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

class HintDialogUtils(context: Context) : Dialog(context, R.style.modifiedDialog) {
    companion object {
        fun show(
            context: Context,
            hint: CharSequence,
            listener: View.OnClickListener
        ): HintDialogUtils {
            val dialog = HintDialogUtils(context)
            dialog.setTitle("")
            dialog.setContentView(R.layout.hint_dialog)
            dialog.setCancelable(false)
            val hintTv = dialog.findViewById<TextView>(R.id.hint_hint)
            hintTv.text = hint
            val mCancelBtn = dialog.findViewById<Button>(R.id.hint_cancel_btn)
            val mCertainBtn = dialog.findViewById<Button>(R.id.hint_ok_btn)
            mCancelBtn.setOnClickListener(listener)
            mCertainBtn.setOnClickListener(listener)

            // 设置居中
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            dialog.window!!.attributes = lp
            dialog.show()
            return dialog
        }
    }
}

object PermissionUtils {
    //这是要申请的权限
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @param requestCode
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isGrantExternalRW(activity: Activity, requestCode: Int): Boolean {
        val storagePermission =
            activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //检测是否有权限，如果没有权限，就需要申请
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            activity.requestPermissions(PERMISSIONS_STORAGE, requestCode)
            //返回false。说明没有授权
            return false
        }
        //说明已经授权
        return true
    }
}