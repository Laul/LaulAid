package com.laulaid.laulaid_tchartskt

// General

// HTTPRequests

// GFit

// Charting
//chart - Detailed views
//chart - main view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.util.ChartUtils
import co.csadev.kellocharts.view.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.fitness.data.*


// GFit - Parameters variables
const val TAG = "LaulAidTAG"


// Main
class MainActivity : AppCompatActivity() {
    // HTTP request variables
    private var btnRequest: Button? = null
    private var mRequestQueue: RequestQueue? = null
    private var mStringRequest: StringRequest? = null
    private val url = "http://127.0.0.1:17580/api/v1/entries/sgv.json?count=10"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create 1 instance of DataHealth for each type of data in GFit
        var DataHealth_BG = DataHealth("Blood Glucose", this)
        var DataHealth_BP = DataHealth("Blood Pressure", this)
        var DataHealth_steps = DataHealth("Steps", this)
        var DataHealth_HR = DataHealth("Heart Rate", this)

        // Google fit
        DataHealth_steps.connectGFit( this)
        DataHealth_BP.connectGFit( this)
        DataHealth_HR.connectGFit( this)

        // XDRip
        DataHealth_BG.connectXDrip(url, this)

        // Button callback to force get data once app launched
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener {
            DataHealth_steps.connectGFit( this)
            DataHealth_BP.connectGFit( this)
            DataHealth_HR.connectGFit( this)
            DataHealth_BG.connectXDrip(url, this)
        }



    }




}

