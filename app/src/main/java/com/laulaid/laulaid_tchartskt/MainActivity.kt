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
    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"


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

//        DataHealth_BG.connectXDrip(url, this)
//        connectXDrip(url, this)

        // Button callback to force get data once app launched
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener { DataHealth_steps.connectGFit( this)}
//        btnRequest!!.setOnClickListener { connectXDrip(url, this)}


        // Show detailed view
//        val buttonClick = findViewById<Button>(R.id.btn_steps)
//        buttonClick.setOnClickListener {
//            val intent = Intent(this, BloodGlucoseActivity::class.java)
//            startActivity(intent)
//        }
//

        var linevalues = arrayListOf(PointValue(0, 2), PointValue(1, 4), PointValue(2, 3), PointValue(3, 4))
        var chartLine = findViewById<View>(R.id.chart1) as LineChartView
        val line = Line(linevalues)
        val lines = arrayListOf(line)
        val linedata = LineChartData(lines)
        chartLine.lineChartData = linedata

//        var colvalues = arrayListOf(PointValue(0, 2), PointValue(1, 4), PointValue(2, 3), PointValue(3, 4))
//        var chartCol = findViewById<View>(R.id.chart2) as ColumnChartView
////        val col = Column(colvalues)
////        val cols = arrayListOf(col)
////        val coldata =  ColumnChartData(cols)
////        chartCol.columnChartData = coldata
//
//
//        val numSubcolumns = 4
//        val numColumns = 4
//        // Column can have many subcolumns, here I use 4 subcolumn in each of 8 columns.
//        val columns = ArrayList<Column>()
//        var values: MutableList<SubcolumnValue>
//        for (i in 0 until numColumns) {
//
//            values = ArrayList()
//            for (j in 0 until numSubcolumns) {
//                values.add(SubcolumnValue(Math.random().toFloat() * 50f + 5, ChartUtils.pickColor()))
//            }
//
//            val column = Column(values)
//
//            columns.add(column)
//        }
//
//        val newData = ColumnChartData(columns)
//        chartCol.columnChartData = ColumnChartData(columns)




    }



    fun connectXDrip(url: String, context: Context) {

        // HTTP request variables
        var mRequestQueue: RequestQueue? = null
        var mStringRequest: StringRequest? = null
        val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
//
//        var mRequestQueue: RequestQueue? = null
//        var mStringRequest: StringRequest? = null

        // Request XDrip connection and permissions
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context)


        //String Request initialized
        mStringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                run {
                    Log.i(TAG, "XDRip connection OK")
//                    getGlucoData(response)
                    // pushGlucoToGFit(response)
                }
            }) { error ->
            Log.i(TAG, "Unable to connect to XDrip")
        }
        mRequestQueue!!.add(mStringRequest)
    }




}

