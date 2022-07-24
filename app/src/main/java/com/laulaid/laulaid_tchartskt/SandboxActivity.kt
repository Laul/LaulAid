package com.laulaid.laulaid_tchartskt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.csadev.kellocharts.gesture.ZoomType
import co.csadev.kellocharts.listener.ColumnChartOnValueSelectListener
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.util.ChartUtils
import co.csadev.kellocharts.view.ColumnChartView
import co.csadev.kellocharts.view.LineChartView

class SandboxActivity : AppCompatActivity() {

    private var btnRequest: Button? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)


    }

//        (activity as AppCompatActivity).setSupportActionBar(view.bgView_Toolbar)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//
//        // Set up the RecyclerView
//        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
//        recyclerView.setHasFixedSize(false)
//        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext, RecyclerView.VERTICAL, false)
//        val adapter = Main_RecyclerViewAdapter(Main_Module.initModuleList(this))
//        recyclerView.adapter = adapter
//        val smallPadding = 30
//        val largePadding = 200
//        recyclerView.addItemDecoration(ModuleDecoration(largePadding, smallPadding))
}
