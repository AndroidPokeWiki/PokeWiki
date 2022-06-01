package com.example.pokewiki.main.profile.collection

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.adapter.CollectionAdapter
import com.example.pokewiki.bean.PokemonSearchBean
import com.example.pokewiki.utils.LoadingDialogUtils
import com.example.pokewiki.utils.ToastUtils
import com.zj.mvi.core.observeEvent
import com.zj.mvi.core.observeState
import qiu.niorgai.StatusBarCompat

/**
 * created by DWF on 2022/5/29.
 */
class CollectionActivity : AppCompatActivity() {
    private val viewModel by viewModels<CollectionViewModel>()

    private lateinit var mItemContainer: RecyclerView
    private lateinit var mBackBtn: ImageButton

    private val collection = ArrayList<PokemonSearchBean>()

    private lateinit var loading: LoadingDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_collection)

        StatusBarCompat.setStatusBarColor(
            this,
            resources.getColor(R.color.poke_ball_red, theme)
        )

        initView()
        initViewModel()
        initViewEvent()

        viewModel.dispatch(CollectionViewAction.GetMyCollection)
    }

    private fun initView() {
        loading = LoadingDialogUtils(this)
        mItemContainer = findViewById(R.id.profile_collection_recylerView)
        mBackBtn = findViewById(R.id.profile_collection_back_btn)
        mBackBtn.setOnClickListener { finish() }

        val collectionAdapter = CollectionAdapter(this, collection, viewModel)
        mItemContainer.adapter = collectionAdapter
        mItemContainer.layoutManager = LinearLayoutManager(this)

    }

    private fun initViewModel() {
        viewModel.viewState.let { states ->
            states.observeState(this, CollectionViewState::data) {
                collection.clear()
                collection.addAll(it)
                (mItemContainer.adapter as CollectionAdapter)
                    .notifyItemRangeChanged(0, collection.size)
            }
        }
    }

    private fun initViewEvent() {
        viewModel.viewEvent.observeEvent(this) {
            when (it) {
                is CollectionViewEvent.ShowToast -> ToastUtils.getInstance(this)
                    ?.showLongToast(it.msg)
                is CollectionViewEvent.ShowLoadingDialog -> loading =
                    LoadingDialogUtils.show(this, "正在获取")
                is CollectionViewEvent.DismissLoadingDialog -> loading.dismiss()
                is CollectionViewEvent.NeedChange -> {
                    Log.e("TAG", "initViewEvent: ${viewModel.viewState.value.data}")
                    collection.clear()
                    collection.addAll(viewModel.viewState.value.data)
                    (mItemContainer.adapter as CollectionAdapter)
                        .notifyItemRangeChanged(0, collection.size)
                }
            }
        }
    }

}