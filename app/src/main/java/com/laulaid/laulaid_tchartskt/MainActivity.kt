package com.laulaid.laulaid_tchartskt

// General
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

// HTTPRequests
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

// GFit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task

// Charting
//chart - Detailed views
import com.klim.tcharts.entities.ChartData
import com.klim.tcharts.entities.ChartItem
//chart - main view
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

// GFit - Parameters variables
const val TAG = "LaulAidTAG"


// Main
class MainActivity : AppCompatActivity() {
    // HTTP request variables
    private var btnRequest: Button? = null
    private var mRequestQueue: RequestQueue? = null
    private var mStringRequest: StringRequest? = null
    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"

    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
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

    // GFit connection to retrieve steps data
    private fun getSteps(startTime:Long, endTime: Long ){
        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build()

        val request = DataReadRequest.Builder()
            .aggregate(datasource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readData(request)
            .addOnSuccessListener { response -> parseSteps(response)}
    }

    /** Steps data parsing + formatting to display graph
    @param response: Google fit response
    */
    private fun parseSteps(response:DataReadResponse ) {
/* Preview page using AAChart lib -> keep TChart for ref.

       // set variables for display

        val dataViewID = findViewById<TChart>(R.id.graph_steps)
        val dataTitle = "Steps"
        val keys = ArrayList<String>(1)
        keys.add("y")

        val names = ArrayList<String>(1)
        names.add("sgv")

        val colors = ArrayList<Int>(1)
        colors.add(Color.parseColor("#F5576C"))

        // Get steps per day for last 7 days
        val data: ArrayList<ChartItem> = ArrayList<ChartItem>()

        var i: Long = 1
        for (dataSet in response.buckets.flatMap { it.dataSets }) {
            for (dp in dataSet.dataPoints) {
                for (field in dp.dataType.fields) {
                    Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                    val step = dp.getValue(field).asInt()
                    data.add(ChartItem(i, arrayListOf(step)))
                    i++
                }
            }
        }
        displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
*/


        // Set chart type and view ID
        val dataType = AAChartType.Column
        val dataViewID1 = findViewById<AAChartView>(R.id.graph_preview_steps)
        val dataTitle = "Steps"

        // Get steps per day for last 7 days
        val data1 = ArrayList<Int>(7)

        for (dataSet in response.buckets.flatMap { it.dataSets }) {
            for (dp in dataSet.dataPoints) {
                for (field in dp.dataType.fields) {
                    Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                    val step = dp.getValue(field).asInt()
                    data1.add(step)
                }
            }
        }
        displayGraph_preview(data1, dataType, dataViewID1, dataTitle)
    }
//
//    /* Gluco data parsing + formatting to display graph
//     @param response: XDrip json response
//    */
//    private fun parseGluco(jsonstring: String){
//        // set variables for display
//        val dataViewID = findViewById<TChart>(R.id.graph_steps)
//        val dataTitle = "Blood Glucose"
//
//        val keys = ArrayList<String>(1)
//        keys.add("y")
//
//        val names = ArrayList<String>(1)
//        names.add("sgv")
//
//        val colors = ArrayList<Int>(1)
//        colors.add(Color.parseColor("#3DC23F"))
//
//        // parse gluco data
//        val json = JSONArray(jsonstring)
//        val data: ArrayList<ChartItem> = ArrayList<ChartItem>()
//        for (i in 0 until json.length()) {
//            val measure = json.getJSONObject(i)
//            val date = measure.getLong("date")
//            val sgv = measure.getInt("sgv")
//            data.add(ChartItem(date, arrayListOf(sgv)))
//        }
//
//        displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
//    }

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
        HealthData.connectXDrip(url, this)

        // Button callback
        btnRequest = findViewById<Button>(R.id.buttonRequest2)
        btnRequest!!.setOnClickListener { sendAndRequestResponse() }
//
//
//        btnRequest = findViewById<Button>(R.id.btn_steps)
//        btnRequest!!.setOnClickListener { sendAndRequestResponse() }


        val buttonClick = findViewById<Button>(R.id.btn_steps)
        buttonClick.setOnClickListener {
            val intent = Intent(this, BloodGlucoseActivity::class.java)
            startActivity(intent)
        }


    }


    private fun sendAndRequestResponse() {
        // Request GFit connection and permissions
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                getGoogleAccount(),
                fitnessOptions)
        } else {
            getSteps(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toEpochSecond(),LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())
        }
//
//        // Request XDrip connection and permissions
//        //RequestQueue initialized
//        mRequestQueue = Volley.newRequestQueue(this)
//
//
//        //String Request initialized
//        mStringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->run{
//                parseGluco(response)
//                pushGlucoToGFit(response)
//
//            }
//            }) { error ->
//            Toast.makeText(getApplicationContext(), "Response :$error", Toast.LENGTH_LONG)
//                .show() //display the response on screen
//            }
//        mRequestQueue!!.add(mStringRequest)

    }
//
//
//
//    private fun displayGraph_advanced(data:ArrayList<ChartItem>, dataViewID: TChart, dataTitle: String, keys:List<String>, names:List<String>, colors:ArrayList<Int>){
//
//        //The chart view object calls the instance object of AAChartModel and draws the final graphic
//        dataViewID.setData(ChartData(keys, names, colors, data))
//
//    }


    private fun displayGraph_preview(data: ArrayList<Int>,dataType: AAChartType,dataViewID: AAChartView, dataTitle:String) {

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

