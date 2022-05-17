package com.example.pokewiki.detail.main

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pokewiki.R
import com.example.pokewiki.custom_view.FlowLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ruffian.library.widget.RTextView

class PokemonDetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<PokemonDetailViewModel>()

    private lateinit var mBackBtn : ImageButton
    private lateinit var mLikeBtn : ImageButton
    private lateinit var mPokeImV : ImageView
    private lateinit var mIdTag: RTextView
    private lateinit var mNameTv : TextView
    private lateinit var mAttrContainer : FlowLayout
    private lateinit var mNavBar : BottomNavigationView
    private lateinit var mPageContainer: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokemon_detail_container)

        initView()
    }

    private fun initView(){
        mBackBtn = findViewById(R.id.pokemon_detail_back_btn)
        mBackBtn.setOnClickListener { finish() }
        mLikeBtn = findViewById(R.id.pokemon_detail_love_btn)
        mPokeImV = findViewById(R.id.pokemon_detail_pokemon_img)
        mIdTag = findViewById(R.id.pokemon_detail_id_tag)
        mNameTv = findViewById(R.id.pokemon_detail_name)
        mAttrContainer = findViewById(R.id.pokemon_detail_attr_container)
        mNavBar = findViewById(R.id.pokemon_detail_nav_bar)
        mPageContainer = findViewById(R.id.pokemon_detail_pager_container)

        viewModel.dispatch(PokemonDetailViewAction.GetInitData(1))
    }
}