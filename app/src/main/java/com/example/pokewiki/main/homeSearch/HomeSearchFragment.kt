package com.example.pokewiki.main.homeSearch

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.adapter.SearchMainAdapter
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.searching.SearchingActivity
import com.example.pokewiki.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.ruffian.library.widget.RTextView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat
import java.util.concurrent.CountDownLatch
import kotlin.math.abs

class HomeSearchFragment : Fragment(R.layout.search_main_fragment) {
    companion object {
        init {
            ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多"
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载"
            ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载..."
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新..."
            ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成"
            ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败"
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了"
        }
    }

    private val viewModel by viewModels<HomeSearchViewModel>()

    private lateinit var mExSearchBtn: RTextView
    private lateinit var mClSearchBtn: RTextView
    private lateinit var mContainer: RecyclerView
    private lateinit var mTitleBar: AppBarLayout
    private lateinit var mCloseBar: Toolbar
    private lateinit var mExMask: View
    private lateinit var mClMask: View
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private lateinit var loading: LoadingDialogUtils
    private lateinit var hint: HintDialogUtils
    private lateinit var sp: SharedPreferences

    private val syncList = ArrayList<PokemonSearchBean>()
    val countDown = CountDownLatch(1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askAutoSave(view)

        // 避免阻塞主线程
        Thread {
            // 第一次先等待用户授权再进行初始化
            if (!sp.getBoolean(FIRST_ASK_AUTO_SAVE, false))
                countDown.await()
            viewModel.dispatch(HomeSearchViewAction.GetCacheData(sp))
        }.start()
    }

    private fun askAutoSave(view: View) {
        loading = LoadingDialogUtils(requireContext())
        hint = HintDialogUtils(requireContext())

        sp = requireActivity().getSharedPreferences(SHARED_NAME, AppCompatActivity.MODE_PRIVATE)
        AppContext.autoSave = sp.getBoolean(AUTO_SAVE, false)

        if (!sp.getBoolean(FIRST_ASK_AUTO_SAVE, false)) {
            hint = HintDialogUtils.show(
                requireContext(),
                "是否开启自动缓存\n可离线查看已缓存数据\n可后续在 我的-设置 中开关"
            ) {
                when (it.id) {
                    R.id.hint_ok_btn -> {
                        AppContext.autoSave = true
                        sp.edit()
                            .putBoolean(AUTO_SAVE, true)
                            .putBoolean(FIRST_ASK_AUTO_SAVE, true)
                            .apply()
                        hint.dismiss()

                        //获取权限
                        PermissionUtils.isGrantExternalRW(requireActivity(), SEARCH_GET_PERMISSION_FLAG)
                    }
                    R.id.hint_cancel_btn -> {
                        countDown.countDown()
                        AppContext.autoSave = false
                        sp.edit()
                            .putBoolean(AUTO_SAVE, false)
                            .putBoolean(FIRST_ASK_AUTO_SAVE, true)
                            .apply()
                        hint.dismiss()
                    }
                }
            }
        }
        initView(view)
        initViewModel()
        initViewEvent()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView(view: View) {
        mExSearchBtn = view.findViewById(R.id.search_expand_search)
        mClSearchBtn = view.findViewById(R.id.search_close_btn)
        mContainer = view.findViewById(R.id.search_main_item_container)
        mTitleBar = view.findViewById(R.id.search_main_title_bar)
        mCloseBar = view.findViewById(R.id.search_main_close_bar)
        mExMask = view.findViewById(R.id.search_expand_mask)
        mClMask = view.findViewById(R.id.search_close_mask)
        mRefreshLayout = view.findViewById(R.id.search_main_refresh_container)

        (activity as AppCompatActivity).setSupportActionBar(mCloseBar)

        val adapter = SearchMainAdapter(requireContext(), syncList)
        mContainer.adapter = adapter
        mContainer.layoutManager = LinearLayoutManager(requireContext())

        //渐变动画
        mTitleBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = abs(verticalOffset)
            val total = appBarLayout.totalScrollRange

            val alphaOut = if ((200 - offset) < 0) 0 else 200 - offset
            val mMaskColor = resources.getColor(R.color.poke_ball_red, null)

            val maskColorIn = Color.argb(
                offset, Color.red(mMaskColor),
                Color.green(mMaskColor), Color.blue(mMaskColor)
            )
            val maskColorOut = Color.argb(
                alphaOut, Color.red(mMaskColor),
                Color.green(mMaskColor), Color.blue(mMaskColor)
            )

            if (offset <= total / 3 * 2) {
                mCloseBar.visibility = View.GONE
                mExMask.setBackgroundColor(maskColorIn)
            } else {
                mCloseBar.visibility = View.VISIBLE
                mClMask.setBackgroundColor(maskColorOut)
            }
        })

        //设置加载逻辑
        mRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                viewModel.dispatch(
                    HomeSearchViewAction.RefreshPage(sp)
                )
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                viewModel.dispatch(
                    HomeSearchViewAction.LoadingNextPage(sp)
                )
            }
        })

        mExSearchBtn.setOnClickListener {
            startActivity(Intent(context, SearchingActivity::class.java))
        }
        mClSearchBtn.setOnClickListener {
            startActivity(Intent(context, SearchingActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(
                this,
                HomeSearchViewState::loadingState,
                HomeSearchViewState::pokemonList
            ) { loading, list ->
                when (loading) {
                    HomeSearchViewState.FINISH -> {
                        mRefreshLayout.finishLoadMore()

                        syncList.clear()
                        syncList.addAll(list)
                        val adapter = (mContainer.adapter as SearchMainAdapter)
                        adapter.notifyItemRangeChanged(adapter.itemCount, syncList.size)
                    }
                    HomeSearchViewState.ERROR -> mRefreshLayout.finishLoadMore(false)
                    HomeSearchViewState.NO_MORE -> mRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
            states.observeState(
                this,
                HomeSearchViewState::refreshState,
                HomeSearchViewState::pokemonList
            ) { refresh, list ->
                when (refresh) {
                    HomeSearchViewState.FINISH -> {
                        mRefreshLayout.finishRefresh()

                        syncList.clear()
                        syncList.addAll(list)
                        val adapter = (mContainer.adapter as SearchMainAdapter)
                        val num = adapter.itemCount - syncList.size
                        adapter.notifyItemRangeChanged(list.size - 1, num)
                    }
                    HomeSearchViewState.ERROR -> mRefreshLayout.finishRefresh(false)
                }
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is HomeSearchViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(requireContext(), "正在查询...")
                is HomeSearchViewEvent.DismissLoadingDialog -> loading.dismiss()
                is HomeSearchViewEvent.WriteToStorage -> viewModel.dispatch(
                    HomeSearchViewAction.WriteToStorage(
                        requireActivity().getExternalFilesDir("pokemon_thumbnail")!!.path, sp
                    )
                )
            }
        }
    }

}