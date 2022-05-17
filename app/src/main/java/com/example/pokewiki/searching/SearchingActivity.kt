package com.example.pokewiki.searching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import com.example.pokewiki.R
import com.example.pokewiki.custom_view.FlowLayout
import com.example.pokewiki.utils.ColorDict
import com.example.pokewiki.utils.dip2px
import com.ruffian.library.widget.RTextView

class SearchingActivity : AppCompatActivity() {

    lateinit var mHistoryContainer: ConstraintLayout
    lateinit var mHistoryTagContainer: FlowLayout
    lateinit var mAttrTagContainer: FlowLayout
    lateinit var mGenContainer: FlowLayout
    lateinit var mInput: EditText
    lateinit var mBackBtn: ImageButton

    private val typeArray =
        arrayOf(
            "一般", "飞行", "火", "超能力", "水", "虫", "电", "岩石",
            "草", "幽灵", "冰", "龙", "格斗", "恶", "毒", "钢", "地面", "妖精"
        )
    private val generationArray = arrayOf(
        "第一世代", "第二世代", "第三世代", "第四世代", "第五世代", "第六世代", "第七世代", "第八世代"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_tag_activity)

        initView()
    }

    private fun initView() {
        mHistoryContainer = findViewById(R.id.search_tag_history_container)
        mHistoryTagContainer = findViewById(R.id.search_tag_history_tag_container)
        mAttrTagContainer = findViewById(R.id.search_tag_attr_container)
        mGenContainer = findViewById(R.id.search_tag_generation_container)
        mInput = findViewById(R.id.search_tag_input)
        mBackBtn = findViewById(R.id.search_tag_back_btn)
        mBackBtn.setOnClickListener { finish() }

        initTag()
    }

    private fun initTag() {
        if (mAttrTagContainer.size > 0) mAttrTagContainer.removeAllViews()
        for (type in typeArray) {
            mAttrTagContainer.addView(layout2View(type))
        }
        if (mGenContainer.size > 0) mGenContainer.removeAllViews()
        for (gen in generationArray) {
            mGenContainer.addView(layout2View(gen))
        }
    }

    private fun layout2View(content: String) : View{
        val attrView = LayoutInflater.from(this).inflate(R.layout.attr_item, null)
        val attrText = attrView.findViewById<RTextView>(R.id.attr_container)

        attrText.text = content
        val helper = attrText.helper
        helper.backgroundColorNormal = resources.getColor(ColorDict.color[content]!!, null)

        val p = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        p.setMargins(
            0, 0,
            dip2px(this, 10.0), dip2px(this, 10.0)
        )
        attrView.layoutParams = p

        return attrView
    }
}