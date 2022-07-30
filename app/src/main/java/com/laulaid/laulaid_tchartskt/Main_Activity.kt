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

        // Set up the RecyclerView
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext, RecyclerView.VERTICAL, false)
        val adapter = Main_RecyclerViewAdapter(Main_Module.initModuleList(this))
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ModuleDecoration(30,30))

        // Button callback to force get data once app launched
        btnRequest = findViewById(R.id.reload_btn)
        btnRequest!!.setOnClickListener {
            val adapter = Main_RecyclerViewAdapter(Main_Module.initModuleList(this))
            recyclerView.adapter = adapter
        }

        // Push data to GFit
        btnRequest = findViewById(R.id.pushgluco_btn)
        btnRequest!!.setOnClickListener {
            var DataHealth_BG = DataHealth("Blood Glucose", this )
            DataHealth_BG.connectXDrip(this, true ,1000)
        }

        // Sandbox
        btnRequest = findViewById(R.id.sandbox_btn)
        btnRequest!!.setOnClickListener {
            val intent = Intent(this, SandboxActivity::class.java)
            startActivity(intent)
        }

    }




}

