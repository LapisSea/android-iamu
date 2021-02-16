package com.example.iamu.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.iamu.R
import com.example.iamu.ui.main.tabs.Tabs
import com.google.android.material.tabs.TabLayout


const val TAB_ID_KEY = "com.example.iamu.ui.main.tabID"

class MainActivity : AppCompatActivity() {

    private lateinit var viewOfTabs: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewOfTabs = findViewById(R.id.view_tabs)
        val tabBar: TabLayout = findViewById(R.id.tab_bar)

        viewOfTabs.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int) = Tabs.values()[position].makeScreen()
            override fun getPageTitle(position: Int) = stringResByName(Tabs.values()[position].name)
            override fun getCount() = Tabs.values().size
        }
        tabBar.setupWithViewPager(viewOfTabs)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_ID_KEY, viewOfTabs.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewOfTabs.currentItem = savedInstanceState.getInt(TAB_ID_KEY) ?: 0
    }

    override fun onResume() {
        super.onResume()
        val sp = getSharedPreferences(MainActivity::class.java.name, MODE_PRIVATE);
        viewOfTabs.currentItem = sp.getInt(TAB_ID_KEY, 0)
    }

    override fun onPause() {
        super.onPause()
        val ed = getSharedPreferences(MainActivity::class.java.name, MODE_PRIVATE).edit()
        ed.putInt(TAB_ID_KEY, viewOfTabs.currentItem)
        ed.apply()
    }

    fun openSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }


}