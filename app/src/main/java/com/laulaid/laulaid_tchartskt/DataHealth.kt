package com.laulaid.laulaid_tchartskt

// General
import android.app.Activity
import android.widget.Toast
import android.graphics.Color
import android.util.Log
import android.content.Context
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


// HTTP Request
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.DataSource.TYPE_DERIVED
import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import org.json.JSONArray

// charting
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartItem

// General
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit


class DataHealth(string: String, context: Context) {
    companion object {
        var dataDetailedView = ArrayList<ChartItem>()
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
            .build()

        /** Steps data parsing + formatting to display graph
        @param response: Google fit response
         */
        fun parseGFitData(InitData: Boolean, response: DataReadResponse, dataHealth: DataHealth){

            // If 1st call: clear data since a new call
            if (InitData==true){
                dataHealth.data.clear()
                for (i in 0 until dataHealth.datasize){
                    dataHealth.data.add(ArrayList<Float>(dataHealth.duration))
                }
            }


            for (dataSet in response.buckets.flatMap { it.dataSets }) {

                if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
                    for (dp in dataSet.dataPoints) {
                        dataHealth.data[0].add(dp.getValue(Field.FIELD_STEPS).asInt().toFloat())
                    }
                }

                else if (dataSet.dataType==TYPE_BLOOD_PRESSURE){
                    for (dp in dataSet.dataPoints) {
                        dataHealth.data[0].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat())
                        dataHealth.data[1].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat())
                    }
                }

                else if (dataSet.dataType==TYPE_HEART_RATE_BPM){
                    for (dp in dataSet.dataPoints) {
                        dataHealth.data[0].add(dp.getValue(Field.FIELD_BPM).asFloat())
                    }
                }
            }
            if (!InitData){dataHealth.displayGraph_preview()}

        }
    }

    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"

    // Data Initialization
    var context = context
    var title = string
    lateinit var aaChartViewID: AAChartView
    lateinit var aaChartType: AAChartType
    var duration: Int = 7
    lateinit var gFitDataType: DataType
    var gFitDataSource: Int = 0
    lateinit var gFitBucketTime: TimeUnit
    lateinit var gFitStreamName: String
    var data = ArrayList<ArrayList<Float>>()
    var datasize = 1

    init {
        if (string === "Steps") {
            aaChartViewID =
                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_steps)
            aaChartType = AAChartType.Column
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
            gFitDataSource = DataSource.TYPE_DERIVED
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "estimated_steps"
        }
        if (string === "Blood Pressure") {
            aaChartViewID =
                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_bloodpressure)
            aaChartType = AAChartType.Spline
            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
            gFitDataSource = DataSource.TYPE_RAW
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Pressure"
            datasize = 2
        }
        if (string === "Heart Rate") {
            aaChartViewID =
                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_heartrate)
            aaChartType = AAChartType.Spline
            gFitDataType = DataType.TYPE_HEART_RATE_BPM
            gFitDataSource = DataSource.TYPE_RAW
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Heart Rate"
        }

        // Create as many series of data as in the datafields from GFit
        for (i in 0 until datasize){
            data.add(ArrayList<Float>(duration))
        }
    }

    // GFit connection to retrieve fit data
    fun getGFitData(duration: Int) {
        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)

        Log.i(TAG, "getGFitData")

        // Request for current (non completed) day / hour
        var ReqCurrentTimes = DataReadRequest.Builder()
            .read(gFitDataType)
            .bucketByTime(1, gFitBucketTime)
            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
            .build()

        // Default request using ".read" - For steps, we need to use ".aggregate"
        // Request for past (completed) days / hours
        var ReqCompletedTimes = DataReadRequest.Builder()
            .read(gFitDataType)
            .bucketByTime(1, gFitBucketTime)
            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
            .build()

        if (title == "Steps") {
            // Request for current (non completed) day / hour
            ReqCurrentTimes = DataReadRequest.Builder()
                .aggregate(gFitDataType)
                .bucketByTime(1, gFitBucketTime)
                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
                .build()

            // Request for past (completed) days / hours
            ReqCompletedTimes = DataReadRequest.Builder()
                .aggregate(gFitDataType)
                .bucketByTime(1, gFitBucketTime)
                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
                .build()
        }

        // History request and go to parse data
        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
            .readData(ReqCurrentTimes)
            .addOnSuccessListener { response -> parseGFitData(true, response, this)}
            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }

        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
            .readData(ReqCompletedTimes)
            .addOnSuccessListener { response -> parseGFitData(false, response, this)}
            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }

    }


    fun displayGraph_preview() {

        var dataserie = ArrayList<AASeriesElement>()
        for (i in 0 until data.size){
            dataserie.add(AASeriesElement()
                .name(title)
                .data(data[i].toArray()))
        }

        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(aaChartType)
            .title(title)
            .backgroundColor("white")
            .dataLabelsEnabled(true)

            .series(dataserie.toArray())

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        aaChartViewID.aa_drawChartWithChartModel(aaChartModel)

    }

    private fun getGoogleAccount(context: Context) =
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)

    fun connectGFit(context: Activity) {
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                context, // your activity
                1, // e.g. 1
                getGoogleAccount(context),
                fitnessOptions
            )
        } else {
            this.getGFitData(7)
//            var DataHealth_steps = DataHealth("Steps", this)
//            getSteps(DataHealth_steps, 7)

        }
    }

    fun connectXDrip(url: String, context: Context) {
        var mRequestQueue: RequestQueue? = null
        var mStringRequest: StringRequest? = null

        // Request XDrip connection and permissions
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context)


        //String Request initialized
        mStringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                run {
                    getGlucoData(response)
                    // pushGlucoToGFit(response)
                }
            }) { error ->
            Log.i(TAG, "Unable to connect to XDrip")
        }
        mRequestQueue!!.add(mStringRequest)
    }

    /* Gluco data parsing + formatting to display graph
     @param response: XDrip json response
    */
    fun getGlucoData(jsonstring: String){
        // set variables for display
        // val dataViewID = findViewById<TChart>(R.id.graph_steps)


        // parse gluco data
        val json = JSONArray(jsonstring)
        dataDetailedView.clear()

        for (i in 0 until json.length()) {
            val measure = json.getJSONObject(i)
            val date = measure.getLong("date")
            val sgv = measure.getInt("sgv")
            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
        }

        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
    }

}