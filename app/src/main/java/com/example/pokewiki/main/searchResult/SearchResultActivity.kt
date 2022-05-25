package com.example.pokewiki.main.searchResult

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pokewiki.R

class SearchResultActivity : AppCompatActivity() {
    private lateinit var mInput : EditText
    private lateinit var mSearchBtn : ImageButton
    private lateinit var mItemContainer : RecyclerView
    private lateinit var mBackBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result_activity)
        initView()
    }

    private fun initView(){
        mInput = findViewById(R.id.search_result_input)
        mSearchBtn = findViewById(R.id.search_result_search_btn)
        mItemContainer = findViewById(R.id.search_result_item_container)
        mBackBtn = findViewById(R.id.search_result_back_btn)
        mBackBtn.setOnClickListener { finish() }
    }
}