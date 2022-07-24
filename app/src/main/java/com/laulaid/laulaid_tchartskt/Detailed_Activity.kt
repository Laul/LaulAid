package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


open class Detailed_Activity : AppCompatActivity() {
    var mType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailedmodule)
        setSupportActionBar(findViewById(R.id.detailedmodule_toolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = mType
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


//        var DataHealth = DataHealth(mType, this,R.id.detailedmodule_Graph_Week,R.id.detailedmodule_Graph_Day, R.id.detailedmodule_Graph_PreviewWeek, R.id.detailedmodule_value, R.id.detailedmodule_label,R.id.detailedmodule_date)
//        DataHealth.connectGFit( DataHealth.context as Activity, false, 1)

        // Set up the RecyclerView
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view_detailed)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext, RecyclerView.VERTICAL, false)
        val adapter = Detailed_RecyclerViewAdapter(Detailed_Module.initModuleList(this, mType))
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ModuleDecoration(30,30))

    }


}