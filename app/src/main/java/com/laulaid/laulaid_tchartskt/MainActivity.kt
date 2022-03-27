package com.laulaid.laulaid_tchartskt

// General

// HTTPRequests

// GFit

// Charting
//chart - Detailed views
//chart - main view
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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

        DataHealth_BG.connectXDrip(url, this)


        // Button callback to force get data once app launched
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener { DataHealth_steps.connectGFit( this) }

        Log.i(TAG,"\tdata: ${DataHealth_steps.data}")

        // Show detailed view
//        val buttonClick = findViewById<Button>(R.id.btn_steps)
//        buttonClick.setOnClickListener {
//            val intent = Intent(this, BloodGlucoseActivity::class.java)
//            startActivity(intent)
//        }
    }



}

