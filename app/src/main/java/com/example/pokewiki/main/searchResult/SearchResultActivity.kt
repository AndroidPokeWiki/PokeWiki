package com.example.pokewiki.main.searchResult

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import kotlin.math.max

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

    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result_activity)

        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {
        loading = LoadingDialogUtils(this)
        mInput = findViewById(R.id.search_result_input)
        mSearchBtn = findViewById(R.id.search_result_search_btn)
        mItemContainer = findViewById(R.id.search_result_item_container)
        mBackBtn = findViewById(R.id.search_result_back_btn)
        mBackBtn.setOnClickListener { finish() }
        mInput.addTextChangedListener { keyword = it.toString() }
        mSearchBtn.setOnClickListener {
            viewModel.dispatch(SearchResultViewAction.UpdateKeyword(keyword))
            viewModel.dispatch(SearchResultViewAction.ClickSearching)
        }
        keyword = intent.getStringExtra("keyword").toString()
        type = intent.getStringExtra("type").toString()

        val searchResultAdapter = SearchResultAdapter(this, data)
        mItemContainer.adapter = searchResultAdapter
        mItemContainer.layoutManager = GridLayoutManager(this, 2)

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