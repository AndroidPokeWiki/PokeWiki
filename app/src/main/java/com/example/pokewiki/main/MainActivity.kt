package com.example.pokewiki.main

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pokewiki.R
import com.example.pokewiki.adapter.MainPageAdapter
import com.example.pokewiki.main.community.CommunityFragment
import com.example.pokewiki.main.homeSearch.HomeSearchFragment
import com.example.pokewiki.main.profile.ProfileFragment
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

        fragmentList.add(CommunityFragment())
        fragmentList.add(HomeSearchFragment())
        fragmentList.add(ProfileFragment())
        val adapter = MainPageAdapter(supportFragmentManager, lifecycle, fragmentList)
        mMainContainer.adapter = adapter
        mMainContainer.currentItem = 1

        mMainContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBottomNaviBar.menu.getItem(position).isChecked = true
            }
        })

        mBottomNaviBar.setOnItemSelectedListener { item ->
            mMainContainer.currentItem = item.order
            true
        }
        mBottomNaviBar.menu.getItem(1).isChecked = true
    }
}