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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest


// GFit - Parameters variables
const val TAG = "LaulAidTAG"


// Main
class MainActivity : AppCompatActivity() {
    // HTTP request variables
    private var btnRequest: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create 1 instance of DataHealth for each type of data in GFit
        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.graph_main_BG, -1 )
        var DataHealth_BP = DataHealth("Blood Pressure",this, R.id.graph_main_BP, -1)
        var DataHealth_steps = DataHealth("Steps", this, R.id.graph_main_steps,-1)
        var DataHealth_HR = DataHealth("Heart Rate",  this, R.id.graph_main_HR,-1)

        // Google fit
        DataHealth_steps.connectGFit( this, false, 6)
        DataHealth_BP.connectGFit( this, false, 6)
        DataHealth_BG.connectGFit( this, false, 6)
        DataHealth_HR.connectGFit( this, false, 6)

        // Button callback to force get data once app launched
        btnRequest = findViewById(R.id.btn_GetData)
        btnRequest!!.setOnClickListener {
            DataHealth_steps.connectGFit( this, false, 6)
            DataHealth_BP.connectGFit( this, false, 6)
            DataHealth_BG.connectGFit( this, false, 6)
            DataHealth_HR.connectGFit( this, false, 6)
        }

        btnRequest = findViewById(R.id.btn_BG)
        btnRequest!!.setOnClickListener {
            val intent = Intent(this, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }

    }




}

