package com.example.pokewiki.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pokewiki.R
import com.example.pokewiki.adapter.PageAdapter
import com.example.pokewiki.main.community.CommunityFragment
import com.example.pokewiki.main.homeSearch.HomeSearchFragment
import com.example.pokewiki.main.profile.ProfileFragment
import com.example.pokewiki.utils.PROFILE_GET_PERMISSION_FLAG
import com.example.pokewiki.utils.SEARCH_GET_PERMISSION_FLAG
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import qiu.niorgai.StatusBarCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mMainContainer: ViewPager2
    private lateinit var mFab: FloatingActionButton
    private lateinit var mBottomNaviBar: BottomNavigationView

    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)

        StatusBarCompat.translucentStatusBar(this)
        initView()
    }

    private fun initView() {
        mMainContainer = findViewById(R.id.main_container)
        mFab = findViewById(R.id.main_home_btn)
        mBottomNaviBar = findViewById(R.id.main_navigation)

        fragmentList.add(CommunityFragment())
        fragmentList.add(HomeSearchFragment())
        fragmentList.add(ProfileFragment())
        val adapter = PageAdapter(supportFragmentManager, lifecycle, fragmentList)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SEARCH_GET_PERMISSION_FLAG -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(
                        this, "拒绝了相关权限，无法自动缓存，请尝试重新授权",
                        Toast.LENGTH_LONG
                    ).show()
                }
                (fragmentList[1] as HomeSearchFragment).countDown.countDown()
            }
            PROFILE_GET_PERMISSION_FLAG -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(
                        this, "拒绝了相关权限，无法自动缓存，请尝试重新授权",
                        Toast.LENGTH_LONG
                    ).show()
                }else
                    (fragmentList[2] as ProfileFragment).getPermission()
            }
        }
    }
}