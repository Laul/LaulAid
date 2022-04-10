package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Context
import android.util.Log
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.util.ChartUtils
import co.csadev.kellocharts.view.AbstractChartView
import co.csadev.kellocharts.view.LineChartView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_GLUCOSE
import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.laulaid.laulaid_tchartskt.DataGeneral.Companion.getDate
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class DataHealth(string: String, context: Context, viewID :Int, duration: Int)  {

    /** Companion object to access variables and function of the class outside
     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
     */

    companion object {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
            .build()

        val xDripUrl = "http://127.0.0.1:17580/api/v1/entries/sgv.json"


        /** Steps data parsing + formatting to display graph
        * @param InitData: Flag to reset data container
        * @param response: Google fit response
        * @param dataHealth: structure containing all class variables to display graphs
         */
        fun parseGFitData(response: DataReadResponse, dataHealth: DataHealth){
            for (bucket in response.buckets) {

                for (dataSet in bucket.dataSets) {
                    // Steps
                    if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
                        dataHealth.kDateMillis.add(bucket.getStartTime(TimeUnit.MILLISECONDS))
                        dataHealth.kDateEEE.add(getDate(bucket.getStartTime(TimeUnit.MILLISECONDS), "EEE"))
                        var steps_temp = 0f

                        for (dp in dataSet.dataPoints) {
                            steps_temp += dp.getValue(Field.FIELD_STEPS).asInt().toFloat()
                        }

                        dataHealth.kLine.add(Line(
                            arrayListOf(
                                PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),0f, ""),
                                PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),steps_temp,steps_temp.toString() ),
                            )
                        ))
                    }


                    else if (dataSet.dataType == TYPE_BLOOD_GLUCOSE) {
                        for (dp in dataSet.dataPoints) {
                            dataHealth.kDateMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
                            dataHealth.kDateEEE.add(getDate(dp.getTimestamp(TimeUnit.MILLISECONDS), "EEE"))
                            dataHealth.kLineValues.add(PointValue(dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),dp.getValue(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL).asFloat(), dp.getValue(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL).asFloat().toString()))
                        }

                    }

                    else if (dataSet.dataType == TYPE_HEART_RATE_BPM) {
                        for (dp in dataSet.dataPoints) {
                            dataHealth.kDateMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
                            dataHealth.kDateEEE.add(getDate(dp.getTimestamp(TimeUnit.MILLISECONDS), "EEE"))
                            dataHealth.kLineValues.add(PointValue(dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),dp.getValue(Field.FIELD_BPM).asFloat(), dp.getValue(Field.FIELD_BPM).asFloat().toString()))
                        }

                    }

                    // Blood Pressure
                    else if (dataSet.dataType == TYPE_BLOOD_PRESSURE) {
                        dataHealth.kDateMillis.add(bucket.getStartTime(TimeUnit.MILLISECONDS))
                        dataHealth.kDateEEE.add(getDate(bucket.getStartTime(TimeUnit.MILLISECONDS), "EEE"))

                        // Initialize BP means
                        var dia_temp = 0f
                        var sys_temp = 0f


                        // Create a new line between systolic and diastolic blood pressureÙ
                        for (dp in dataSet.dataPoints) {
                            dia_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat()
                            sys_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat()


                        }

                        // Calculate averages for systolic and diastolic BP per day
                        dia_temp /= dataSet.dataPoints.size
                        sys_temp /= dataSet.dataPoints.size
                        dataHealth.kLine.add(Line(
                            arrayListOf(
                                PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),dia_temp, dia_temp.toString()),
                                PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),sys_temp, sys_temp.toString()),
                            )
                        )
                        )
                    }
                }
            }

            // Display Graph
            dataHealth.displayGraphPreview_K()
        }
    }

    /** Class variables per category: general, charting, data structure, GoogleFit variables,etc. )
     */
    // Data Initialization
    var context = context

    // Chart variables
    lateinit var kChartViewID: AbstractChartView

    // Data Structure - KelloCharts

    // Column variables
    var kColumn = ArrayList<Column>()
    // var kValues_SubColumn = ArrayList<SubcolumnValue>()

    // Line variables
    var kLine = ArrayList<Line>()
    var kLineValues = ArrayList<PointValue>()

    var kXAxis = Axis()
    var kXAxisValues = ArrayList<AxisValue>()
    var kDateMillis = ArrayList<Long>()
    var kDateEEE = ArrayList<String>()

    //GFit variables
    var duration: Int
    lateinit var gFitDataType: DataType
    lateinit var gFitBucketTime: TimeUnit
    lateinit var gFitStreamName: String

    // Variables initialization for each data type:
    init {
        this.duration = duration

        if (string === "Steps") {
            // KelloChart
            kChartViewID =(context as Activity).findViewById<LineChartView>(viewID)
            kXAxis.name = string

            gFitDataType = TYPE_STEP_COUNT_DELTA
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "estimated_steps"

        }
        if (string === "Blood Pressure") {
            kChartViewID =(context as Activity).findViewById<LineChartView>(viewID)
            kXAxis.name = string

            gFitDataType = TYPE_BLOOD_PRESSURE
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Pressure"
        }
        if (string === "Heart Rate") {
            kChartViewID =(context as Activity).findViewById<LineChartView>(viewID)
            kXAxis.name = string

            gFitDataType = TYPE_HEART_RATE_BPM
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Heart Rate"
        }
        if (string === "Blood Glucose") {
            kChartViewID =(context as Activity).findViewById<LineChartView>(viewID)
            kXAxis.name = string

            gFitDataType = TYPE_BLOOD_GLUCOSE
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Glucose"
        }

    }

    /** GFit permissions verification and dispatch
    * @param context: App Context (typically main activity)
     */
    fun connectGFit(context: Activity, isPush:Boolean) {
        // If no permission -> request permission to the user
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                context, // your activity
                1, // e.g. 1
                getGoogleAccount(context),
                fitnessOptions
            )
        }
        // If permission -> get Data
        else {
            if (!isPush) {
                this.getGFitData(duration)
            }
            else{
                pushGlucoData()
            }
        }
    }

    /** GFit connection request based on fitness option list
    * @param context: App Context (typically main activity)
     */
    private fun getGoogleAccount(context: Context) =
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)

    /** GFit connection to retrieve fit data
    * @param duration: duration to cover (default: last 7 days)
     */
    fun getGFitData(duration: Int) {
        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)

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

        if (kXAxis.name == "Steps") {
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

        // Clear Data before retrieving other data from GFit
        kDateMillis.clear()
        kDateEEE.clear()
        kColumn.clear()
        kLine.clear()
        kLineValues.clear()

        kLine.forEach{it.values.clear()}

        // History request and go to parse data
        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
            .readData(ReqCurrentTimes)
            .addOnSuccessListener { response -> parseGFitData(response, this)}
            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }

        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
            .readData(ReqCompletedTimes)
            .addOnSuccessListener { response -> parseGFitData(response, this)}
            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
    }

    /** Data sorting based on object data index
     * @param valMilli: Values in milliseconds - Used to determine the order to sort the sort
     * @param valData: Values to sort based on valMilli order
     */
    fun <T>sortData (valMilli: ArrayList<Long>, valData: MutableList<T>):MutableList<T> {
        val dateComparator = Comparator { col1: T, col2:T  ->
            (valMilli[valData.indexOf(col1)] - valMilli[valData.indexOf(col2)]).toInt()
        }

        return valData.sortedWith(dateComparator) as MutableList<T>
    }

    /** Display preview graphs on main using KelloCharts Lib
     */
    fun displayGraphPreview_K() {
        kXAxisValues.clear()

        // Compute x-Axis values and sort all values by chronological order


            // Display Graph
        if (kDateMillis.size > 1) {

            if (kChartViewID is LineChartView) {

                // All cases except Blood Pressure: 1 line with Point Values
                if(kXAxis.name == "Blood Glucose" || kXAxis.name == "Heart Rate"){
                    // Create a line with all PointValues
                    kLine.clear()
                    kLine.add(Line(kLineValues))

                    // sort Point Values in chronological order
                    kLine.forEach {
                        if (it.values != null) {
                            it.values = sortData(kDateMillis, it.values)
                        }
                    }
                }

                // Blood Pressure case: x lines with 2 pointvalues
                if(kXAxis.name == "Blood Pressure" ||kXAxis.name == "Steps"){
                    // sort Lines in chronological order
                    kLine = sortData(kDateMillis, kLine).toMutableList() as ArrayList
                }

                // get each distinct value of date in the list of string dates
                val kXAxisLabels = kDateEEE.distinct()

                // Get index of each distinct value in the list of string dates
                var kXAxisIndex = ArrayList<Int>()
                kXAxisLabels.forEach{
                    kXAxisIndex.add(kDateEEE.indexOf(it))
                }

                // Create axis values
                for (i in 0 until kXAxisIndex.size) {
                    kXAxisValues.add(AxisValue(kDateMillis[kXAxisIndex[i]].toFloat(), kXAxisLabels[i].toCharArray()))
                }

                // Add values for x Axis
                kXAxis.values = kXAxisValues

                // Lines formatting
                kLine.forEach{
                    it.isFilled = false
                    it.hasPoints = true
                    it.strokeWidth = 1
                    it.color = ChartUtils.COLOR_GREEN
                    it.pointRadius = 1
                    it.hasLabels = false
                }

                // Create a LineChartData using time and Line data
                var kChart = LineChartData(kLine)

                // Add axis values and push it in the chart
                kChart.axisXBottom = kXAxis
                (kChartViewID as LineChartView).lineChartData = kChart
            }

        }
    }

    
    /** XDrip permissions verification and dispatch
    * @param count: number of data to retrieve from XDrip. Max is 1000, XDrip does neither allow nor to specify time interval, nor more than the last 1000 values (~3.5 days of data)
    * @param context: App Context (typically main activity)
     */
    fun connectXDrip(context: Context, isPush: Boolean, count: Int) {
        var mRequestQueue: RequestQueue? = null
        var mStringRequest: StringRequest? = null

        // Request XDrip connection and permissions
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(context)


        //String Request initialized
        mStringRequest = StringRequest(
            Request.Method.GET, xDripUrl + "?count=" + count,
            { response ->
                run {
                    getGlucoData(response)
                }
            }) { error ->
            Log.i(TAG, "Unable to connect to XDrip")
        }
        mRequestQueue!!.add(mStringRequest)
    }

    /** Gluco data parsing + formatting to display graph
     * @param jsonstring: XDrip json string retrieved from connectXDrip
     * @param flag: "push"/"pull" boolean
    */
    fun getGlucoData(jsonstring: String){
        // parse gluco data
        val json = JSONArray(jsonstring)
        // Reset data
        kLine.clear()

        // Loop to get BG data
        for (i in 0 until json.length()) {
            // Get one set of data from JSON
            var measure = json.getJSONObject(i)

            // Get dates in millis and in strings
            kDateMillis.add(measure.getLong("date"))
            kDateEEE.add(getDate(measure.getLong("date"), "EEE").toString())

            // Get BG values and create associated PointValue
            val sgv = measure.getDouble("sgv")/18
            kLineValues.add(PointValue(measure.getLong("date").toFloat(),sgv.toFloat(), "%.2f".format(sgv)))
        }

        // Add glucose data to GFit (Push)

            pushGlucoData()
    }

    /**
     * Push gluco data to GFit.
     * @param
     * @return
     */

    fun pushGlucoData(){
// Create DataSource
        val gFitGlucoDSource = DataSource.Builder()
            .setAppPackageName(context)
            .setDataType(TYPE_BLOOD_GLUCOSE)
            .setType(DataSource.TYPE_RAW)
            .build()



        // Create dataset
        val gFitGlucoDSet = DataSet.builder(gFitGlucoDSource)

        for (i in 0 until kLineValues.size){
            val date = kLineValues[i].x.toLong()
            val sgv = kLineValues[i].y


        // Add new datapoint to dataset
        gFitGlucoDSet.add(DataPoint.builder(gFitGlucoDSource)
                .setTimestamp(date, TimeUnit.MILLISECONDS)
                .setField(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL, sgv)
                .build()
            )
        }

        // Request dataset update
        val request = DataUpdateRequest.Builder()
            .setDataSet(gFitGlucoDSet.build())
            .setTimeInterval(kLineValues[kLineValues.size -1 ].x.toLong(), kLineValues[0].x.toLong(), TimeUnit.MILLISECONDS)
            .build()


        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .updateData(request)
            .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem updating the dataset.", e)
            }

        }

}