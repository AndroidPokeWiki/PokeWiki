package com.example.pokewiki.main.homeSearch

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R
import com.example.pokewiki.adapter.SearchMainAdapter
import com.example.pokewiki.bean.SearchMainItemBean
import com.google.android.material.appbar.AppBarLayout
import com.ruffian.library.widget.RTextView
import kotlin.math.abs

class HomeSearchFragment : Fragment(R.layout.search_main_fragment) {

    private lateinit var mExSearchBtn: RTextView
    private lateinit var mClSearchBtn: RTextView
    private lateinit var mContainer: RecyclerView
    private lateinit var mTitleBar: AppBarLayout
    private lateinit var mCloseBar: Toolbar
    private lateinit var mExMask: View
    private lateinit var mClMask: View

    private val mItemList = ArrayList<SearchMainItemBean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
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

        (activity as AppCompatActivity).setSupportActionBar(mCloseBar)

        //测试代码
        val attrList = ArrayList<String>()
        attrList.add("草")
        attrList.add("毒")
        for (i in 0..10) {
            createItem(
                resources.getDrawable(R.drawable.test_pokemon_item, null),
                "妙蛙种子",
                attrList,
                "#001"
            )
        }

        val adapter = SearchMainAdapter(requireContext(), mItemList)
        mContainer.adapter = adapter
        mContainer.layoutManager = LinearLayoutManager(requireContext())

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
            }
            else {
                mCloseBar.visibility = View.VISIBLE
                mClMask.setBackgroundColor(maskColorOut)
            }
        })
    }

    //测试方法
    private fun createItem(
        drawable: Drawable,
        name: String,
        attrList: ArrayList<String>,
        id: String
    ) {
        mItemList.add(SearchMainItemBean(drawable, name, attrList, id))
    }
}