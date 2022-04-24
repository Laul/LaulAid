package com.laulaid.laulaid_tchartskt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SandboxActivity : AppCompatActivity() {

    private var btnRequest: Button? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
//        (activity as AppCompatActivity).setSupportActionBar(view.bgView_Toolbar)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.bgView_MainGraph,  R.id.bgView_PreviewGraph , R.id.bgView_Value,R.id.bgView_Label, R.id.bg_date)
        DataHealth_BG.connectGFit( this, false, 15)

// Set up the RecyclerView
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext, RecyclerView.VERTICAL, false)
        val adapter = ModuleRecyclerViewAdapter(Module.initModuleList())
        recyclerView.adapter = adapter
        val smallPadding = 30
        val largePadding = 200
        recyclerView.addItemDecoration(ModuleDecoration(largePadding, smallPadding))
        }



}