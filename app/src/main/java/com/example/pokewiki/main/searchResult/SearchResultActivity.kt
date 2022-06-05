package com.example.pokewiki.main.searchResult

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.adapter.SearchResultAdapter
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState

/**
 * created by DWF on 2022/5/25.
 */
class SearchResultActivity : AppCompatActivity() {
    private val viewModel by viewModels<SearchResultViewModel>()

    private lateinit var mInput: EditText
    private lateinit var mSearchBtn: ImageButton
    private lateinit var mItemContainer: RecyclerView
    private lateinit var mBackBtn: ImageButton

    private val data = ArrayList<PokemonSearchBean>()

    private var keyword: String = ""
    private var type: String = ""

    private var historyMap = HashMap<String, ArrayList<String>>()
    private val mHistoryList = ArrayList<String>()

    private lateinit var sp: SharedPreferences
    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result_activity)

        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)

        loading = LoadingDialogUtils(this)
        mInput = findViewById(R.id.search_result_input)
        mSearchBtn = findViewById(R.id.search_result_search_btn)
        mItemContainer = findViewById(R.id.search_result_item_container)
        mBackBtn = findViewById(R.id.search_result_back_btn)
        mBackBtn.setOnClickListener { finish() }
        mInput.addTextChangedListener { keyword = it.toString() }
        mInput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //隐藏键盘
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(
                        this.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    );
                //搜索
                mSearchBtn.performClick()
            }
            false
        }
        mSearchBtn.setOnClickListener {
            viewModel.dispatch(SearchResultViewAction.UpdateKeyword(keyword))
            viewModel.dispatch(SearchResultViewAction.ClickSearching)

            // 记录历史记录
            if (mHistoryList.contains(keyword))
                mHistoryList.remove(keyword)
            mHistoryList.add(0, keyword)
            historyMap[AppContext.userData.userId] = mHistoryList
            sp.edit().putString(USER_HISTORY, Gson().toJson(historyMap)).apply()
        }
        keyword = intent.getStringExtra("keyword").toString()
        type = intent.getStringExtra("type").toString()
        mInput.setText(keyword)

        val searchResultAdapter = SearchResultAdapter(this, data)
        mItemContainer.adapter = searchResultAdapter
        mItemContainer.layoutManager = GridLayoutManager(this, 2)

        initHistory()
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

        } catch (e: JsonParseException) {
            Log.e("JSON ERROR!!", "initHistory: JSON PARSE ERROR!! ${e.message}")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel.dispatch(SearchResultViewAction.UpdateKeyword(keyword))
        Log.e("TAG", "initViewModel: $keyword, $type")
        when (type) {
            "name" -> viewModel.dispatch(SearchResultViewAction.ClickSearching)
            "type" -> viewModel.dispatch(SearchResultViewAction.ClickSearchingType)
            "gen" -> viewModel.dispatch(SearchResultViewAction.ClickSearchingGen)
        }

        viewModel.viewState.let { states ->
            states.observeState(this, SearchResultViewState::result) {
                data.clear()
                data.addAll(it)
                (mItemContainer.adapter as SearchResultAdapter).notifyDataSetChanged()
            }
        }

    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is SearchResultViewEvent.ShowToast -> ToastUtils.getInstance(this)
                    ?.showLongToast(it.msg)
                is SearchResultViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在搜索...")
                is SearchResultViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }

}