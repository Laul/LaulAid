package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


open class DetailedModule : AppCompatActivity() {
    var mType = ""
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailedmodule)
        setSupportActionBar(findViewById(R.id.detailedmodule_toolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = mType




        // set the references of the declared objects above
        pager = findViewById(R.id.viewPager)

        // Initializing the ViewPagerAdapter
        val adapter = DetailedModule_ViewPagerAdapter(supportFragmentManager)
        tab = findViewById(R.id.tabs)

        // add fragment to the list
        adapter.addFragment(DetailedModule_Fragment(mType, this), "Day")
        adapter.addFragment(DetailedModule_Fragment(mType, this), "Week")
        adapter.addFragment(DetailedModule_Fragment(mType, this), "Month")

        // Adding the Adapter to the ViewPager
        pager.adapter = adapter

        // bind the viewPager with the TabLayout.
        tab.setupWithViewPager(pager)
    }


}