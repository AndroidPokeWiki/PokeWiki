package com.example.pokewiki.main.profile.collection

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pokewiki.R
import com.example.pokewiki.main.searchResult.SearchResultViewModel

/**
 * created by DWF on 2022/5/29.
 */
class CollectionActivity : AppCompatActivity() {
    private val viewModel by viewModels<CollectionViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_collection)

        initView()
        initViewModel()
        initViewEvent()
    }

    private fun initView() {

    }

    private fun initViewModel() {

    }

    private fun initViewEvent() {

    }

}