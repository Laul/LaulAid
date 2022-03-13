package com.laulaid.laulaid_tchartskt

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

// Charting
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartData
import com.klim.tcharts.entities.ChartItem

//HTTPRequests
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import org.json.JSONArray

// GFit
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import java.time.LocalDateTime
import android.net.Uri
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn

// Fit
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.IntToDoubleFunction
import kotlin.collections.ArrayList


// GFit - Parameters variables
const val TAG = "LaulAidTAG"

// For the purposes of this sample, a hard-coded period of time is defined, covering the nights of
// sleep that will be written and read.
const val PERIOD_START_DATE_TIME = "2022-03-03T12:00:00Z"
const val PERIOD_END_DATE_TIME = "2022-03-04T12:00:00Z"



class MainActivity : AppCompatActivity() {
    // HTTP request variables
    private var btnRequest: Button? = null
    private var mRequestQueue: RequestQueue? = null
    private var mStringRequest: StringRequest? = null
    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"


    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
        .build()

    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    private fun accessGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener({ gresponse ->
                printData(gresponse)
            })
            .addOnFailureListener({ e -> Log.d("GFIT", "OnFailure()", e) })
    }

    private fun updateFitnessData(jsonstring: String): Task<Void> {

        val gFitGlucodsource = DataSource.Builder()
            .setAppPackageName(this)
            .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create dataset
        val gFitGlucodset = DataSet.builder(gFitGlucodsource)
        val json = JSONArray(jsonstring)
        val minDate = json.getJSONObject(json.length()-1).getLong("date")
        val maxDate = json.getJSONObject(0).getLong("date")

        for (i in 0 until json.length()){
            val measure = json.getJSONObject(i)
            val date = measure.getLong("date")
            val sgv = measure.getInt("sgv")

            // Add new datapoint to dataset
            gFitGlucodset.add(DataPoint.builder(gFitGlucodsource)
                .setTimestamp(date, TimeUnit.MILLISECONDS)
                .setField(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL, sgv/18.0f)
                .build()
            )
        }


        // Request dataset update
        val request = DataUpdateRequest.Builder()
            .setDataSet(gFitGlucodset.build())
            .setTimeInterval(minDate, maxDate, TimeUnit.MILLISECONDS)
            .build()

        return Fitness.getHistoryClient(this, getGoogleAccount())
            .updateData(request)
            .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem updating the dataset.", e)
            }
    }



    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.buckets.isNotEmpty()) {
            Log.e(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                bucket.dataSets.forEach { dumpDataSet(it) }
            }
        } else if (dataReadResult.dataSets.isNotEmpty()) {
            Log.e(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
            dataReadResult.dataSets.forEach { dumpDataSet(it) }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTime(TimeUnit.MILLISECONDS)}")
            Log.i(TAG, "\tEnd: ${dp.getEndTime(TimeUnit.MILLISECONDS)}")
            dp.dataType.fields.forEach {
                Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
            }
        }
    }
    // [END parse_dataset]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRequest = findViewById(R.id.buttonRequest2) as Button?
        btnRequest!!.setOnClickListener { sendAndRequestResponse() }

    }

    private fun sendAndRequestResponse() {
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                getGoogleAccount(),
                fitnessOptions)
        } else {
            accessGoogleFit()
        }

        // Add blood glucose data to GFit
        //updateFitnessData()


        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this)

        //String Request initialized
        mStringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->run{
                // convertJsonToChartData(response)
                updateFitnessData(response)
            }
            }) { error ->
            Toast.makeText(getApplicationContext(), "Response :$error", Toast.LENGTH_LONG)
                .show() //display the response on screen
            }
        mRequestQueue!!.add(mStringRequest)

    }

    private fun convertJsonToHelloChartData(jsonstring: String){


        val json = JSONArray(jsonstring)
        val data = ArrayList<Int>(json.length())
        for (i in 0 until json.length()){
            val measure = json.getJSONObject(i)
            val date = measure.getLong("date")

            val sgv = measure.getInt("sgv")
            data.add(sgv)
        }


        val aaChartView = findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title("title")
            .subtitle("subtitle")
            .backgroundColor("#4b2b7f")
            .dataLabelsEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Tokyo")
                    .data(data.toArray())

            )
            )
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

}