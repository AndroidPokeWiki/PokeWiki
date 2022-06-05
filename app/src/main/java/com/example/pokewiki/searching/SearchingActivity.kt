package com.example.pokewiki.searching

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import com.example.pokewiki.R
import com.example.pokewiki.customView.FlowLayout
import com.example.pokewiki.main.searchResult.SearchResultActivity
import com.example.pokewiki.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.ruffian.library.widget.RTextView
import qiu.niorgai.StatusBarCompat

class SearchingActivity : AppCompatActivity() {

    private lateinit var mHistoryContainer: ConstraintLayout
    private lateinit var mHistoryTagContainer: FlowLayout
    private lateinit var mAttrTagContainer: FlowLayout
    private lateinit var mGenContainer: FlowLayout
    private lateinit var mInput: EditText
    private lateinit var mBackBtn: ImageButton
    private lateinit var mSearchBtn: ImageButton
    private lateinit var mHistoryClrBtn: ImageButton
    private lateinit var sp: SharedPreferences
    private lateinit var hint: HintDialogUtils

    private var historyMap = HashMap<String, ArrayList<String>>()
    private val mHistoryList = ArrayList<String>()
    private var keyword = ""
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

        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.poke_ball_red, theme))
        initView()
    }

    private fun initView() {
        sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)

        mHistoryContainer = findViewById(R.id.search_tag_history_container)
        mHistoryTagContainer = findViewById(R.id.search_tag_history_tag_container)
        mHistoryClrBtn = findViewById(R.id.search_tag_history_clr)
        mHistoryClrBtn.setOnClickListener {
            hint = HintDialogUtils.show(this, "是否删除全部历史记录") {
                when (it.id) {
                    R.id.hint_cancel_btn -> hint.dismiss()
                    R.id.hint_ok_btn -> {
                        hint.dismiss()
                        // 清除历史记录
                        mHistoryList.clear()
                        historyMap[AppContext.userData.userId] = mHistoryList
                        sp.edit().putString(USER_HISTORY, Gson().toJson(historyMap)).apply()
                        mHistoryContainer.visibility = View.GONE
                    }
                }
            }
        }
        mAttrTagContainer = findViewById(R.id.search_tag_attr_container)
        mGenContainer = findViewById(R.id.search_tag_generation_container)
        mInput = findViewById(R.id.search_tag_input)
        mInput.addTextChangedListener { keyword = it.toString() }
        mInput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //隐藏键盘
                (getSystemService (INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(
                        this.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    );
                //搜索
                mSearchBtn.performClick()
            }
            false
        }
        mBackBtn = findViewById(R.id.search_tag_back_btn)
        mBackBtn.setOnClickListener { finish() }
        mSearchBtn = findViewById(R.id.search_tag_search_btn)
        mSearchBtn.setOnClickListener {
            val intent = Intent(this, SearchResultActivity::class.java)
            intent.putExtra("type", "name").putExtra("keyword", keyword)

            // 记录历史记录
            if (mHistoryList.contains(keyword))
                mHistoryList.remove(keyword)
            mHistoryList.add(0, keyword)
            historyMap[AppContext.userData.userId] = mHistoryList
            sp.edit().putString(USER_HISTORY, Gson().toJson(historyMap)).apply()

            startActivity(intent)
            finish()
        }
        initTag()
        initHistory()
    }

    private fun initTag() {
        if (mAttrTagContainer.size > 0) mAttrTagContainer.removeAllViews()
        for (type in typeArray) {
            mAttrTagContainer.addView(layout2View(type, "type"))
        }
        if (mGenContainer.size > 0) mGenContainer.removeAllViews()
        for (gen in generationArray) {
            mGenContainer.addView(layout2View(gen, "gen"))
        }
    }

    private fun initHistory() {
        val historyStr = sp.getString(USER_HISTORY, null)
        try {
            if (historyStr == null) throw JsonParseException("历史记录为空")
            historyMap = Gson().fromJson(
                historyStr,
                object : TypeToken<HashMap<String, ArrayList<String>>>() {}.type
            )

            mHistoryList.addAll(historyMap.getOrDefault(AppContext.userData.userId, ArrayList()))
            for (history in mHistoryList) {
                mHistoryTagContainer.addView(layout2View(history, "name"))
            }

            if (mHistoryTagContainer.size > 0)
                mHistoryContainer.visibility = View.VISIBLE

        } catch (e: JsonParseException) {
            Log.e("JSON ERROR!!", "initHistory: JSON PARSE ERROR!! ${e.message}")
        }
    }

    private fun layout2View(content: String, type: String): View {
        val attrView = LayoutInflater.from(this).inflate(R.layout.attr_item, null)
        val attrText = attrView.findViewById<RTextView>(R.id.attr_container)
        attrText.text = content
        attrText.setOnClickListener {
            val intent = Intent(this, SearchResultActivity::class.java)
            when (type) {
                "name" -> {
                    intent.putExtra("type", "name").putExtra("keyword", content)
                    // 更新历史记录
                    mHistoryList.remove(content)
                    mHistoryList.add(0, content)
                    historyMap[AppContext.userData.userId] = mHistoryList
                    sp.edit().putString(USER_HISTORY, Gson().toJson(historyMap)).apply()
                }
                "type" ->
                    intent.putExtra("type", "type").putExtra("keyword", content)
                "gen" ->
                    intent.putExtra("type", "gen").putExtra("keyword", content)
            }
            startActivity(intent)
            finish()
        }

        val helper = attrText.helper
        val color = ColorDict.color[content]
        if (color != null && type != "name")
            helper.backgroundColorNormal = resources.getColor(color, theme)
        else
            helper.backgroundColorNormal = resources.getColor(ColorDict.color["第六世代"]!!, theme)

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