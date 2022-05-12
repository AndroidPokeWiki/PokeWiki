package com.example.pokewiki.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pokewiki.R
import com.example.pokewiki.adapter.MainPageAdapter
import com.example.pokewiki.main.homeSearch.HomeSearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var mMainContainer: ViewPager2
    private lateinit var mFab: FloatingActionButton
    private lateinit var mBottomNaviBar: BottomNavigationView

    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)
        initView()
    }

    private fun initView() {
        mMainContainer = findViewById(R.id.main_container)
        mFab = findViewById(R.id.main_home_btn)
        mBottomNaviBar = findViewById(R.id.main_navigation)

        fragmentList.add(HomeSearchFragment())
        val adapter = MainPageAdapter(supportFragmentManager, lifecycle, fragmentList)
        mMainContainer.adapter = adapter
    }
}