package com.example.pokewiki.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import java.text.DecimalFormat
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

@SuppressLint("ClickableViewAccessibility")
class BottomDialogUtils(mContext: Context, itemsOnClick: View.OnClickListener?) :
    PopupWindow() {
    private val view: View = LayoutInflater.from(mContext).inflate(R.layout.photo_dialog, null)
    private val photoBtn: Button = view.findViewById<View>(R.id.dialog_photo_btn) as Button
    private val albumBtn: Button = view.findViewById<View>(R.id.dialog_album_btn) as Button
    private val cancelBtn: Button = view.findViewById<View>(R.id.dialog_cancel_btn) as Button

    init {
        // 取消按钮
        cancelBtn.setOnClickListener {
            // 销毁弹出框
            dismiss()
        }
        // 设置按钮监听
        albumBtn.setOnClickListener(itemsOnClick)
        photoBtn.setOnClickListener(itemsOnClick)

        // 设置外部可点击
        this.isOutsideTouchable = true
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener { _: View?, event: MotionEvent ->
            val height = view.findViewById<View>(R.id.dialog_bottom_photo).top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }

        /* 设置弹出窗口特征 */
        // 设置视图
        this.contentView = view
        // 设置弹出窗体的宽和高
        this.height = dip2px(mContext, 200.0)
        this.width = LinearLayout.LayoutParams.MATCH_PARENT

        // 设置弹出窗体可点击
        this.isFocusable = true

        // 实例化一个ColorDrawable颜色为全透明
        val dw = ColorDrawable(0x00000000)
        // 设置弹出窗体的背景
        setBackgroundDrawable(dw)

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.animationStyle = R.style.DialogAnimation
        update()
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

object FileManagerUtils {
    //获取图片
    fun getFilePath(context: Context, uri: Uri?): String? {
        var imagePath: String? = null
        if (uri != null) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                //如果是document类型的Uri，则通过document id处理
                val docId = DocumentsContract.getDocumentId(uri)
                if ("com.android.providers.media.documents" == uri.authority) {
                    val id = docId.split(":").toTypedArray()[1] //解析出数字格式的id
                    val selection = MediaStore.Images.Media._ID + "=" + id
                    imagePath = getImagePath(
                        context,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection
                    )
                } else if ("com.android.providers.downloads.documents" == uri.authority) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public downloads"),
                        docId.toLong()
                    )
                    imagePath = getImagePath(context, contentUri, null)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                //如果是file类型的Uri，直接获取图片路径即可
                imagePath = getImagePath(context, uri, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                //如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.path
            }
        }
        return imagePath
    }

    fun getBitmapFromPath(context: Context?, path: String?): Bitmap? {
        return if (path != null && path != "") {
            BitmapFactory.decodeFile(path)
        } else {
            Toast.makeText(context, "获取图片失败", Toast.LENGTH_SHORT).show()
            null
        }
    }

    //通过uri获取图片路径
    @SuppressLint("Range")
    private fun getImagePath(context: Context, uri: Uri, selection: String?): String? {
        var path: String? = null
        //通过Uri和selection来获取真实的图片路径
        val cursor = context.contentResolver
            .query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    //转换文件大小
    fun formatFileSize(fileS: Int): String {
        val df = DecimalFormat("#.00")
        val wrongSize = "0B"
        if (fileS == 0) {
            return wrongSize
        }
        val fileSizeString: String = when {
            (fileS < 1024) -> df.format(fileS.toDouble()) + "B"
            (fileS < 1024 * 1024) -> df.format(fileS.toDouble() / 1024) + "KB"
            (fileS < 1024 * 1024 * 1024) -> df.format(fileS.toDouble() / (1024 * 1024)) + "MB"
            else -> df.format(fileS.toDouble() / (1024 * 1024 * 1024)) + "GB"
        }
        return fileSizeString
    }
}
