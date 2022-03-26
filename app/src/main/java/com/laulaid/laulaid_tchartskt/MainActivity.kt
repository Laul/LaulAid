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

    //    private fun pushGlucoToGFit(jsonstring: String): Task<Void> {
//
//        val gFitGlucodsource = DataSource.Builder()
//            .setAppPackageName(this)
//            .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
//            .setType(DataSource.TYPE_RAW)
//            .build()
//
//        // Create dataset
//        val gFitGlucodset = DataSet.builder(gFitGlucodsource)
//        val json = JSONArray(jsonstring)
//        val minDate = json.getJSONObject(json.length()-1).getLong("date")
//        val maxDate = json.getJSONObject(0).getLong("date")
//
//        for (i in 0 until json.length()){
//            val measure = json.getJSONObject(i)
//            val date = measure.getLong("date")
//            val sgv = measure.getInt("sgv")
//
//
//            // Add new datapoint to dataset
//            gFitGlucodset.add(DataPoint.builder(gFitGlucodsource)
//                .setTimestamp(date, TimeUnit.MILLISECONDS)
//                .setField(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL, sgv/18.0f)
//                .build()
//            )
//        }
//
//        // Request dataset update
//        val request = DataUpdateRequest.Builder()
//            .setDataSet(gFitGlucodset.build())
//            .setTimeInterval(minDate, maxDate, TimeUnit.MILLISECONDS)
//            .build()
//
//        return Fitness.getHistoryClient(this, getGoogleAccount())
//            .updateData(request)
//            .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "There was a problem updating the dataset.", e)
//            }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create 1 instance of DataHealth for each type of data in GFit
        var DataHealth_BP = DataHealth("Blood Pressure", this)
        var DataHealth_steps = DataHealth("Steps", this)
        var DataHealth_HR = DataHealth("Steps", this)

        // Google fit
        DataHealth_steps.connectGFit( this)

        // XDRip
        // DataHealth.connectXDrip(url, this)

        // Button callback to force get data once app launched
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener { DataHealth_steps.connectGFit( this) }

        Log.i(TAG,"\tdata: ${DataHealth_steps.data}")

        val buttonClick = findViewById<Button>(R.id.btn_steps)
        buttonClick.setOnClickListener {
            val intent = Intent(this, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }
    }



}

