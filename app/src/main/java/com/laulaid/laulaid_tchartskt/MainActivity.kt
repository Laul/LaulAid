package com.laulaid.laulaid_tchartskt

// General android
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log


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
import java.time.LocalDateTime
import com.google.android.gms.auth.api.signin.GoogleSignIn

// Fit
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import java.time.ZoneId
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

    /** GFit connection to retrieve steps data
     * @param startTime: start of timerange - typically minus 7 days relative to end time
     * @param endTime: end of timerange - typically current day
     */
    private fun getSteps(startTime:Long, endTime: Long ){
        val datasource = DataSource.Builder()
            .setAppPackageName(this)
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

    /** GFit connection to parse and format steps data and send to display function
     * @param response: Google fit response
     */
    private fun parseSteps(response:DataReadResponse ) {
        // Set chart type and view ID
        val dataType = AAChartType.Bar
        val dataViewID = findViewById<AAChartView>(R.id.aa_chart_view_step)

        // Get steps per day for last 7 days
        val data = ArrayList<Int>(7)

        for (dataSet in response.buckets.flatMap { it.dataSets }) {
            for (dp in dataSet.dataPoints) {
                for (field in dp.dataType.fields) {
                    Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                    val step = dp.getValue(field).asInt()
                    data.add(step)
                }
            }
        }
        displayGraph(data, dataType)
    }


    /** Push gluco data from XDrip to GFit
     * @param jsonstring: json content from XDrip
     */
    private fun updateFitnessData(jsonstring: String): Task<Void> {

        val gFitGlucodsource = DataSource.Builder()
            .setAppPackageName(this)
            .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .setType(DataSource.TYPE_RAW)
            .setStreamName("estimated_steps")
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


    /** Button callback
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRequest = findViewById(R.id.buttonRequest2) as Button?
        btnRequest!!.setOnClickListener { sendAndRequestResponse() }

    }package com.laulaid.laulaid_tchartskt

// General android
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log


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
    import java.time.LocalDateTime
    import com.google.android.gms.auth.api.signin.GoogleSignIn

// Fit
    import com.google.android.gms.fitness.Fitness
    import com.google.android.gms.fitness.request.DataReadRequest
    import com.google.android.gms.fitness.FitnessOptions
    import com.google.android.gms.fitness.data.*
    import com.google.android.gms.fitness.data.HealthDataTypes
    import com.google.android.gms.fitness.request.DataUpdateRequest
    import com.google.android.gms.fitness.result.DataReadResponse
    import com.google.android.gms.tasks.Task
    import java.time.ZoneId
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

        /** GFit connection to retrieve steps data
         * @param startTime: start of timerange - typically minus 7 days relative to end time
         * @param endTime: end of timerange - typically current day
         */
        private fun getSteps(startTime:Long, endTime: Long ){
            val datasource = DataSource.Builder()
                .setAppPackageName(this)
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

        /** GFit connection to parse and format steps data and send to display function
         * @param response: Google fit response
         */
        private fun parseSteps(response:DataReadResponse ) {
            // Set chart type and view ID
            val dataType = AAChartType.Bar
            val dataViewID = findViewById<AAChartView>(R.id.aa_chart_view_step)

            // Get steps per day for last 7 days
            val data = ArrayList<Int>(7)

            for (dataSet in response.buckets.flatMap { it.dataSets }) {
                for (dp in dataSet.dataPoints) {
                    for (field in dp.dataType.fields) {
                        Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                        val step = dp.getValue(field).asInt()
                        data.add(step)
                    }
                }
            }
            displayGraph(data, dataType, dataViewID)
        }


        /** GFit connection to retrieve glucose data (already pushed from XDrip to GFit)
         * @param startTime: start of timerange - typically minus 24 hours relative to end time
         * @param endTime: end of timerange - typically current time
         */
        private fun getGluco(startTime:Long, endTime: Long ) {
            val datasource = DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("Glucose")
                .build()

            val request = DataReadRequest.Builder()
                .aggregate(datasource)
                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .build()

            Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(request)
                .addOnSuccessListener { response -> parseGluco(response)}
        }

        /** GFit connection to parse and format glucose data and send to display function
         * @param response: Google fit response
         */
        private fun parseGluco(response:DataReadResponse ) {
            // Set chart type and view ID
            val dataType = AAChartType.Line
            val dataViewID = findViewById<AAChartView>(R.id.aa_chart_view_gluco)

            // Get steps per day for last 7 days
            val data = ArrayList<Int>(24)

            for (dataSet in response.buckets.flatMap { it.dataSets }) {
                for (dp in dataSet.dataPoints) {
                    for (field in dp.dataType.fields) {
                        Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                        val step = dp.getValue(field).asInt()
                        data.add(step)
                    }
                }
            }
            displayGraph(data, dataType, dataViewID)
        }



        /** Push gluco data from XDrip to GFit
         * @param jsonstring: json content from XDrip
         */
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


        /** Button callback
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            btnRequest = findViewById(R.id.buttonRequest2) as Button?
            btnRequest!!.setOnClickListener { sendAndRequestResponse() }

        }


        /** Request access to GFit
         */
        private fun sendAndRequestResponse() {
            if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    this, // your activity
                    1, // e.g. 1
                    getGoogleAccount(),
                    fitnessOptions)
            } else {
                getSteps(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toEpochSecond(),LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())
                // getGluco(LocalDateTime.now().minusHours(24).atZone(ZoneId.systemDefault()).toEpochSecond(),LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())

            }

            // Add blood glucose data to GFit
            //updateFitnessData()


            //RequestQueue initialized
            mRequestQueue = Volley.newRequestQueue(this)

            //String Request initialized
            mStringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->run{
                    convertJson(response)
                    // updateFitnessData(response)
                }
                }) { error ->
                Toast.makeText(getApplicationContext(), "Response :$error", Toast.LENGTH_LONG)
                    .show() //display the response on screen
            }
            mRequestQueue!!.add(mStringRequest)

        }


        /** Convert Json from XDrip into a gluco data from XDrip to GFit
         * @param jsonstring: json content from XDrip
         */
        private fun convertJson(jsonstring: String){
            val json = JSONArray(jsonstring)
            val data = ArrayList<Int>(json.length())
            for (i in 0 until json.length()){
                val measure = json.getJSONObject(i)
                val date = measure.getLong("date")

                val sgv = measure.getInt("sgv")
                data.add(sgv)
            }

            // displayGraph(data,  AAChartType.Line, dataViewID)
        }

        private fun displayGraph(data:ArrayList<Int>, dataType:AAChartType, dataViewID: AAChartView ){
            val dataViewID1 = findViewById<AAChartView>(R.id.aa_chart_view_gluco)

            val aaChartModel : AAChartModel = AAChartModel()
                .chartType(dataType)
                .title("title")
                .subtitle("subtitle")
                .backgroundColor("#4b2b7f")
                .dataLabelsEnabled(true)
                .series(arrayOf(
                    AASeriesElement()
                        .name("Steps")
                        .data(data.toArray())

                )
                )
            //The chart view object calls the instance object of AAChartModel and draws the final graphic
            dataViewID1.aa_drawChartWithChartModel(aaChartModel)
        }



    }




    /** Request access to GFit
     */
    private fun sendAndRequestResponse() {
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                getGoogleAccount(),
                fitnessOptions)
        } else {
            getSteps(LocalDateTime.now().minusDays(10).atZone(ZoneId.systemDefault()).toEpochSecond(),LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toEpochSecond())
        }

        // Add blood glucose data to GFit
        //updateFitnessData()
//
//
//        //RequestQueue initialized
//        mRequestQueue = Volley.newRequestQueue(this)
//
//        //String Request initialized
//        mStringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->run{
//                convertJson(response)
//                updateFitnessData(response)
//            }
//            }) { error ->
//            Toast.makeText(getApplicationContext(), "Response :$error", Toast.LENGTH_LONG)
//                .show() //display the response on screen
//            }
//        mRequestQueue!!.add(mStringRequest)

    }


    /** Convert Json from XDrip into a gluco data from XDrip to GFit
     * @param jsonstring: json content from XDrip
     */
    private fun convertJson(jsonstring: String){
        // Set chart type and view ID
        val dataType = AAChartType.Line
        val dataViewID = 2

        val json = JSONArray(jsonstring)
        val data = ArrayList<Int>(json.length())
        for (i in 0 until json.length()){
            val measure = json.getJSONObject(i)
            val date = measure.getLong("date")

            val sgv = measure.getInt("sgv")
            data.add(sgv)
        }

    // displayGraph(data,  AAChartType.Line, dataViewID)
    }

    private fun displayGraph(data:ArrayList<Int>, dataType:AAChartType ){
        val id= findViewById(R.id.aa_chart_view_step) as AAChartView

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(dataType)
            .title("title")
            .subtitle("subtitle")
            .backgroundColor("#4b2b7f")
            .dataLabelsEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Steps")
                    .data(data.toArray())

            )
            )
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        id.aa_drawChartWithChartModel(aaChartModel)
    }



    }

