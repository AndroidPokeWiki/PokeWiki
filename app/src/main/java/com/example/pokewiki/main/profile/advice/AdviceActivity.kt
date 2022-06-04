package com.example.pokewiki.main.profile.advice

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.size
import com.example.pokewiki.R
import com.example.pokewiki.utils.*
import qiu.niorgai.StatusBarCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class AdviceActivity : AppCompatActivity() {
    private lateinit var mBackBtn: ImageButton
    private lateinit var mSendBtn: ImageButton
    private lateinit var mAdviceEt: EditText
    private lateinit var mContainer: LinearLayout

    private lateinit var dialog: BottomDialogUtils
    private lateinit var loading: LoadingDialogUtils

    private lateinit var mCamaraArl: ActivityResultLauncher<Uri>
    private lateinit var mAlbumArl: ActivityResultLauncher<String>

    private lateinit var photoImage: File
    private lateinit var imageUri: Uri

    private var isFull = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.profile_suggestion_feedback)
        StatusBarCompat.setStatusBarColor(
            this,
            resources.getColor(R.color.poke_ball_red, theme)
        )

        initRegister()
        initView()
    }

    private fun initRegister() {
        mCamaraArl = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            try {
                val bitmap =
                    BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                if (bitmap != null) {

                    // 更换图片
                    val view = mContainer.getChildAt(mContainer.size - 1)
                    view.findViewById<ConstraintLayout>(R.id.profile_suggestion_add_pic_bg).visibility =
                        View.GONE
                    view.findViewById<ImageView>(R.id.profile_suggestion_pic)
                        .setImageBitmap(bitmap)
                    // 添加删除图片
                    val delBtn =
                        view.findViewById<ImageButton>(R.id.profile_suggestion_feedback_del_pic)
                    delBtn.setOnClickListener {
                        mContainer.removeView(view)
                        if (isFull) {
                            isFull = false
                            addAddPicBtn()
                        }
                    }
                    delBtn.visibility = View.VISIBLE

                    if (mContainer.size < 3) {
                        // 添加新添加图片按钮
                        addAddPicBtn()
                    } else
                        isFull = true
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        mAlbumArl = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result: Uri? ->
            if (result != null) {
                val imagePath = FileManagerUtils.getFilePath(this, result)
                if (imagePath != null) {
                    val bitmap: Bitmap? = FileManagerUtils.getBitmapFromPath(
                        this,
                        FileManagerUtils.getFilePath(this, result)
                    )
                    // 更换图片
                    val view = mContainer.getChildAt(mContainer.size - 1)
                    view.findViewById<ConstraintLayout>(R.id.profile_suggestion_add_pic_bg).visibility =
                        View.GONE
                    view.findViewById<ImageView>(R.id.profile_suggestion_pic)
                        .setImageBitmap(bitmap)
                    // 添加删除图片
                    val delBtn =
                        view.findViewById<ImageButton>(R.id.profile_suggestion_feedback_del_pic)
                    delBtn.setOnClickListener {
                        mContainer.removeView(view)
                        if (isFull) {
                            isFull = false
                            addAddPicBtn()
                        }
                    }
                    delBtn.visibility = View.VISIBLE

                    if (mContainer.size < 3) {
                        // 添加新添加图片按钮
                        addAddPicBtn()
                    } else
                        isFull = true
                }
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

        mBackBtn = findViewById(R.id.profile_suggestion_feedback_confirm)
        mBackBtn.setOnClickListener { finish() }
        mSendBtn = findViewById(R.id.profile_suggestion_feedback_send)
        mSendBtn.setOnClickListener {
            if (mAdviceEt.text.toString().isNotBlank()) {
                loading = LoadingDialogUtils.show(this, "正在上传...")
                if (mContainer.size > 1) {
                    Thread {
                        Thread.sleep(1000)
                        runOnUiThread {
                            loading.dismiss()
                            mContainer.removeAllViews()
                            addAddPicBtn()
                            mAdviceEt.setText("")
                            ToastUtils.getInstance(this)?.showShortToast("感谢您的反馈")
                        }
                    }.start()
                } else {
                    Thread {
                        Thread.sleep(200)
                        runOnUiThread {
                            loading.dismiss()
                            mContainer.removeAllViews()
                            addAddPicBtn()
                            mAdviceEt.setText("")
                            ToastUtils.getInstance(this)?.showShortToast("感谢您的反馈")
                        }
                    }.start()
                }
            }else
                ToastUtils.getInstance(this)?.showShortToast("请输入内容")
        }
        mAdviceEt = findViewById(R.id.profile_suggestion_feedback_info_text)
        mContainer = findViewById(R.id.profile_suggestion_feedback_pic_container)
        addAddPicBtn()
    }

    private fun addAddPicBtn() {
        // 添加图片按钮
        val addPicBtn = LayoutInflater.from(this).inflate(R.layout.profile_add_picture, null)
        addPicBtn.findViewById<ConstraintLayout>(R.id.profile_suggestion_add_pic_bg)
            .setOnClickListener {
                dialog.showAtLocation(
                    findViewById(R.id.profile_suggestion_feedback_bg),
                    Gravity.BOTTOM,
                    0,
                    0
                )
            }

        val p = ViewGroup.MarginLayoutParams(dip2px(this, 110.0), dip2px(this, 110.0))
        p.setMargins(
            0, 0,
            dip2px(this, 10.0), 0
        )
        addPicBtn.layoutParams = p
        mContainer.addView(addPicBtn)
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