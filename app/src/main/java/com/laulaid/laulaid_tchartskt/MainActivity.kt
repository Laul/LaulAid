package com.laulaid.laulaid_tchartskt

// General

// HTTPRequests

// GFit

// Charting
//chart - Detailed views
//chart - main view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// GFit - Parameters variables
const val TAG = "LaulAidTAG"


// Main
class MainActivity : AppCompatActivity() {
    // HTTP request variables
    private var btnRequest: Button? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//
// Set up the RecyclerView
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext, RecyclerView.VERTICAL, false)
        val adapter = ModuleRecyclerViewAdapter(Module.initModuleList(this))
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ModuleDecoration(30,30))







//        // Create 1 instance of DataHealth for each type of data in GFit
//        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.bg_graph, -1 , R.id.bg_value, R.id.bg_label , R.id.bg_date)
////        var DataHealth_BP = DataHealth("Blood Pressure",this, R.id.graph_main_BP, -1, R.id.bp_value)
//        var DataHealth_steps = DataHealth("Steps", this, R.id.steps_graph,-1, R.id.steps_value, R.id.steps_label, R.id.steps_date)
//        var DataHealth_HR = DataHealth("Heart Rate",  this, R.id.ht_graph,-1, R.id.hr_value, R.id.hr_label, R.id.hr_date)
////        var DataHealth_sleep = DataHealth("Sleep", this, R.id.graph_main_sleep-1, R.id.steps_value, R.id.steps_label)
//
//        // Google fit
//        DataHealth_steps.connectGFit( this, false, 6)
////        DataHealth_BP.connectGFit( this, false, 6)
//        DataHealth_BG.connectGFit( this, false, 2)
//        DataHealth_HR.connectGFit( this, false, 6)

        // Button callback to force get data once app launched
        btnRequest = findViewById(R.id.reload_btn)
        btnRequest!!.setOnClickListener {
//            var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.graph_main_BG, -1 , R.id.bg_value)
//            DataHealth_BG.connectXDrip(this, true ,1000)
//            DataHealth_steps.connectGFit( this, false, 6)
////            DataHealth_BP.connectGFit( this, false, 6)
//            DataHealth_BG.connectGFit( this, false, 2)
//            DataHealth_HR.connectGFit( this, false, 6)
            val adapter = ModuleRecyclerViewAdapter(Module.initModuleList(this))
            recyclerView.adapter = adapter
        }

//
//
//        // Start BG activity
//        btnRequest = findViewById(R.id.bg_btn)
//        btnRequest!!.setOnClickListener {
//            val intent = Intent(this, BloodGlucoseActivity::class.java)
//            startActivity(intent)
//        }
//
//
//        btnRequest = findViewById(R.id.steps_btn)
//        btnRequest!!.setOnClickListener {
//            val intent = Intent(this, StepsActivity::class.java)
//            startActivity(intent)
//        }


        // Push data to GFit
        btnRequest = findViewById(R.id.pushgluco_btn)
        btnRequest!!.setOnClickListener {
            var DataHealth_BG = DataHealth("Blood Glucose", this, -1, -1, -1, -1,-1  )
            DataHealth_BG.connectXDrip(this, true ,1000)
        }

        btnRequest = findViewById(R.id.sandbox_btn)
        btnRequest!!.setOnClickListener {
            val intent = Intent(this, SandboxActivity::class.java)
            startActivity(intent)
        }

    }




}

