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

//    companion object {
//        val fitnessOptions = FitnessOptions.builder()
//            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//            .build()
//    }


    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, DataHealth.fitnessOptions)

    // GFit connection to retrieve steps data
    private fun getSteps(dataHealth: DataHealth, duration: Int){
        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)

        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(dataHealth.gFitDataType)
            .setType(dataHealth.gFitDataSource)
            .setStreamName(dataHealth.gFitStreamName)
            .build()
//
//        // Request for current (non completed) time (i.e. current day, current hour, etc)
//        val requestCurrentTime = DataReadRequest.Builder()
//            .aggregate(datasource)
//            .bucketByTime(1, dataHealth.gFitBucketTime)
//            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//            .build()
//        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, DataHealth.fitnessOptions))
//            .readData(requestCurrentTime)
//            .addOnSuccessListener { response -> parseSteps(response, dataHealth)}

        // Request for completed time (i.e. last days, last hours, etc.)
//        val requestCompletedTimes = DataReadRequest.Builder()
//            .aggregate(datasource)
//            .bucketByTime(1, dataHealth.gFitBucketTime)
//            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//            .build()

        val requestCompletedTimes = DataReadRequest.Builder()
            .read(dataHealth.gFitDataType)
            .bucketByTime(1, dataHealth.gFitBucketTime)
            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, DataHealth.fitnessOptions))
            .readData(requestCompletedTimes)
            .addOnSuccessListener { response -> parseSteps(response, dataHealth)}
    }

    /** Steps data parsing + formatting to display graph
    @param response: Google fit response
    */
    private fun parseSteps(response:DataReadResponse, dataHealth: DataHealth) {

        val data = ArrayList<Float>(7)

        for (dataSet in response.buckets.flatMap { it.dataSets }) {
            for (dp in dataSet.dataPoints) {
                for (field in dp.dataType.fields) {
                    Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                    val step = dp.getValue(field).asFloat()
                    data.add(step)
                }
            }
        }


        displayGraph_preview(data, dataHealth.aaChartType, dataHealth.aaChartViewID, dataHealth.chartTitle)
    }

    private fun pushGlucoToGFit(jsonstring: String): Task<Void> {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Google fit
        sendAndRequestResponse()

        // XDRip
        // DataHealth.connectXDrip(url, this)

        // Button callback to force get data once app launched
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener { sendAndRequestResponse() }

        val buttonClick = findViewById<Button>(R.id.btn_steps)
        buttonClick.setOnClickListener {
            val intent = Intent(this, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendAndRequestResponse() {
        // Request GFit connection and permissions
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), DataHealth.fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                getGoogleAccount(),
                DataHealth.fitnessOptions)
        } else {
//            var DataHealth_steps = DataHealth("Steps", this)
//            getSteps(DataHealth_steps, 7)

            var DataHealth_BP = DataHealth("Blood Pressure", this)
            getSteps(DataHealth_BP, 7)
        }
    }


    private fun displayGraph_preview(data: ArrayList<Float>,dataType: AAChartType,dataViewID: AAChartView, dataTitle:String) {

        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(dataType)
            .title("i")
            .subtitle("")
            .backgroundColor("white")
            .dataLabelsEnabled(true)

            .series(
                arrayOf(
                    AASeriesElement()
                        .name(dataTitle)
                        .data(data.toArray())

                )
            )
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        dataViewID.aa_drawChartWithChartModel(aaChartModel)
    }


}

