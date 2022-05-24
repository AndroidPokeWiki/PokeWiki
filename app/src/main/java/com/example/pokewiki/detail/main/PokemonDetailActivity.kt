package com.example.pokewiki.detail.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.pokewiki.R
import com.example.pokewiki.adapter.PageAdapter
import com.example.pokewiki.bean.PokemonDetailBean
import com.example.pokewiki.detail.info.PokemonDetailInfoFragment
import com.example.pokewiki.detail.move.PokemonDetailMoveFragment
import com.example.pokewiki.detail.states.PokemonDetailStatesFragment
import com.example.pokewiki.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ruffian.library.widget.RTextView
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat

class PokemonDetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<PokemonDetailViewModel>()

    private lateinit var mBg: ConstraintLayout
    private lateinit var mBackBtn: ImageButton
    private lateinit var mLikeBtn: ImageButton
    private lateinit var mPokeImV: ImageView
    private lateinit var mIdTag: RTextView
    private lateinit var mNameTv: TextView
    private lateinit var mAttrContainer: LinearLayout
    private lateinit var mNavBar: BottomNavigationView
    private lateinit var mPageContainer: ViewPager2

    private lateinit var loading: LoadingDialogUtils
    private val fragmentList = ArrayList<Fragment>()

    private val infoFragment = PokemonDetailInfoFragment()
    private val stateFragment = PokemonDetailStatesFragment()
    private val moveFragment = PokemonDetailMoveFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokemon_detail_container)

        //清除缓存
        AppContext.pokeDetail = PokemonDetailBean()
        //加载
        initView()
        initViewModel()
        initViewEvent()
    }

    @SuppressLint("RestrictedApi")
    private fun initView() {
        loading = LoadingDialogUtils(this)

        mBg = findViewById(R.id.pokemon_detail_bg)
        mBackBtn = findViewById(R.id.pokemon_detail_back_btn)
        mBackBtn.setOnClickListener { finish() }
        mLikeBtn = findViewById(R.id.pokemon_detail_love_btn)
        mPokeImV = findViewById(R.id.pokemon_detail_pokemon_img)
        mIdTag = findViewById(R.id.pokemon_detail_id_tag)
        mNameTv = findViewById(R.id.pokemon_detail_name)
        mAttrContainer = findViewById(R.id.pokemon_detail_attr_container)
        mNavBar = findViewById(R.id.pokemon_detail_nav_bar)
        mPageContainer = findViewById(R.id.pokemon_detail_pager_container)

        mNavBar.itemIconTintList = null

        fragmentList.add(infoFragment)
        fragmentList.add(stateFragment)
        fragmentList.add(moveFragment)
        val adapter = PageAdapter(supportFragmentManager, lifecycle, fragmentList)
        mPageContainer.adapter = adapter

        mLikeBtn.setOnClickListener { viewModel.dispatch(PokemonDetailViewAction.SwitchLikeState) }

        mPageContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mNavBar.menu.getItem(position).isChecked = true
                // 重置状态页动画
                stateFragment.clearData()
                when (position) {
                    1 -> stateFragment.refreshData()
                    2 -> moveFragment.refreshData()
                }
            }
        })

        mNavBar.setOnItemSelectedListener {
            mPageContainer.currentItem = it.order
            true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViewModel() {
        val id = intent.getStringExtra("id")
        viewModel.dispatch(PokemonDetailViewAction.GetInitData(id))

        viewModel.viewState.let { state ->
            state.observeState(this, PokemonDetailViewState::name) {
                mNameTv.text = it
            }
            state.observeState(this, PokemonDetailViewState::id) {
                mIdTag.text = it
                // 刷新fragment的数据
                if (it != "")
                    infoFragment.refreshData()
            }
            state.observeState(this, PokemonDetailViewState::img) {
                Glide.with(this).load(it).into(mPokeImV)
            }
            state.observeState(this, PokemonDetailViewState::color) {
                ColorDict.color[it]?.let { color ->
                    mBg.setBackgroundColor(resources.getColor(color, theme))
                    StatusBarCompat.setStatusBarColor(this, resources.getColor(color, theme))
                    window.navigationBarColor = resources.getColor(color, theme)
                }
            }
            state.observeState(this, PokemonDetailViewState::is_like) {
                if (!it)
                    mLikeBtn.setImageDrawable(
                        resources.getDrawable(R.drawable.pokemon_love, theme)
                    )
                else
                    mLikeBtn.setImageDrawable(
                        resources.getDrawable(R.drawable.pokemon_love_select, theme)
                    )
            }
            state.observeState(this, PokemonDetailViewState::attrs) {
                if (mAttrContainer.size > 0) mAttrContainer.removeAllViews()
                for (attr in it) {
                    mAttrContainer.addView(layout2View(attr))
                }
            }
            state.observeState(this, PokemonDetailViewState::likeError){
                if (it)
                    viewModel.dispatch(PokemonDetailViewAction.ResetError)
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is PokemonDetailViewEvent.ShowToast ->
                    ToastUtils.getInstance(this)?.showLongToast(it.msg)
                is PokemonDetailViewEvent.ShowLoadingDialog ->
                    loading = LoadingDialogUtils.show(this, "正在获取...")
                is PokemonDetailViewEvent.DismissLoadingDialog -> loading.dismiss()
            }
        }
    }

    private fun layout2View(content: String): View {
        val attrView = LayoutInflater.from(this).inflate(R.layout.attr_item, null)
        val attrText = attrView.findViewById<RTextView>(R.id.attr_container)

        attrText.text = content
        val helper = attrText.helper
        helper.backgroundColorNormal = resources.getColor(ColorDict.color[content]!!, theme)

        val p = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        p.setMargins(
            dip2px(this, 10.0), 0,
            dip2px(this, 10.0), 0
        )
        attrView.layoutParams = p

        return attrView
    }

    // 刷新主框架
    fun refresh() {
        viewModel.dispatch(PokemonDetailViewAction.RefreshData)
    }
}