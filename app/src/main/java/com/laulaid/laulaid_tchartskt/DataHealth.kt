package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import co.csadev.kellocharts.gesture.ZoomType
import co.csadev.kellocharts.listener.ViewportChangeListener
import co.csadev.kellocharts.model.*
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
import com.laulaid.laulaid_tchartskt.DataGeneral.Companion.getDate
import com.laulaid.laulaid_tchartskt.R.color.*
import org.json.JSONArray
import java.util.concurrent.TimeUnit


class DataHealth(string: String, context: Context, viewID :Int, previewID: Int, valueID : Int, labelID: Int, dateID : Int)  {

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
                    if (dataSet.dataType==TYPE_STEP_COUNT_DELTA) {
                        if (dataHealth.context::class == MainActivity::class) {
                            dataHealth.kDateMillis.add(bucket.getStartTime(TimeUnit.MILLISECONDS))
                            dataHealth.kDateEEE.add(getDate(bucket.getStartTime(TimeUnit.MILLISECONDS), "EEE"))
                            var steps_temp = 0f

                            for (dp in dataSet.dataPoints) {
                                steps_temp += dp.getValue(Field.FIELD_STEPS).asInt().toFloat()
                            }

                            dataHealth.kLine.add(
                                Line(
                                    arrayListOf(
                                        PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), 0f, ""),
                                        PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), steps_temp, steps_temp.toString()),
                                    )
                                )
                            )
                        }
                        else{
                            for (dp in dataSet.dataPoints) {
                                dataHealth.kDateMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
                                dataHealth.kDateEEE.add(getDate(dp.getTimestamp(TimeUnit.MILLISECONDS), "EEE"))
                                dataHealth.kLineValues.add(PointValue(dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),dp.getValue(Field.FIELD_STEPS).asFloat(), dp.getValue(Field.FIELD_STEPS).asFloat().toString()))
                            }
                        }
                    }


                    else if (dataSet.dataType == TYPE_BLOOD_GLUCOSE) {
                        Log.i(TAG, "prout")
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
            dataHealth.displayMainGraph()
        }
    }

    /** Class variables per category: general, charting, data structure, GoogleFit variables,etc. )
     */
    // Data Initialization
    var context = context

    // Views to plot graph and add values
    var kChartViewID = viewID
    var kChartPreViewID =previewID
    var kValueViewID = valueID
    var kLabelViewID = labelID
    var kDateViewID = dateID

    // Chart variables (Main view)
    var kLineColor = ContextCompat.getColor(context, R.color.orange_primary)
    var kLine = ArrayList<Line>()
    var kLineValues = ArrayList<PointValue>()

    var kXAxis = Axis()
    var kYaxis = Axis()

    var kXAxisValues = ArrayList<AxisValue>()
    var kDateMillis = ArrayList<Long>()
    var kDateEEE = ArrayList<String>()

    // Chart Variables (Detailed view)
    var updatingPreviewViewport = false
    var updatingChartViewport = false

    //GFit variables
    lateinit var gFitDataType: DataType
    lateinit var gFitBucketTime: TimeUnit
    lateinit var gFitStreamName: String

    // Variables initialization for each data type:
    init {
        if (string === "Steps") {
            gFitDataType = TYPE_STEP_COUNT_DELTA
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "estimated_steps"
            kXAxis.name = string
            kYaxis.name = ""
            kLineColor = ContextCompat.getColor(context, R.color.orange_primary)
        }
        if (string === "Blood Pressure") {
            gFitDataType = TYPE_BLOOD_PRESSURE
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Pressure"
            kXAxis.name = string
            kYaxis.name = "mmHg"
            kLineColor = ContextCompat.getColor(context, R.color.orange_primary)
        }
        if (string === "Heart Rate") {
            gFitDataType = TYPE_HEART_RATE_BPM
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Heart Rate"
            kXAxis.name = string
            kYaxis.name = "bpm"
            kLineColor = ContextCompat.getColor(context, R.color.blue_primary)
        }
        if (string === "Blood Glucose") {
            gFitDataType = TYPE_BLOOD_GLUCOSE
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Glucose"
            kXAxis.name = string
            kYaxis.name = "mmol/L"
            kLineColor = ContextCompat.getColor(context, R.color.red_primary)

        }

    }

    /** GFit permissions verification and dispatch
    * @param context: App Context (typically main activity)
     */
    fun connectGFit(context: Activity, isPush:Boolean, duration: Int) {
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
        kLine.clear()
        kLineValues.clear()

//        displayCharts = false
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

    /** Display Main graphs using KelloCharts Lib
     */
    fun displayMainGraph() {
        kXAxisValues.clear()

        // Display Graph
         if (kDateMillis.size > 1) {
                var view_GraphMain = (context as Activity).findViewById<LineChartView>(kChartViewID)

                if (view_GraphMain is LineChartView) {

                    // All cases except Blood Pressure: 1 line with Point Values
                    if (kXAxis.name == "Blood Glucose" || kXAxis.name == "Heart Rate") {
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

                    // Blood Pressure case and steps: x lines with 2 pointvalues
                    if (kXAxis.name == "Blood Pressure" || kXAxis.name == "Steps") {
                        // sort Lines in chronological order
                        kLine = sortData(kDateMillis, kLine).toMutableList() as ArrayList


                    }

                    // Last value to fill textfield + Label update depending on the last value
                    if (kXAxis.name != "Blood Pressure") {
                        formatLabel()
                        kYaxis = Axis(hasLines = true, maxLabels = 4)
                    }
                    // get each distinct value of date in the list of string dates
                    val kXAxisLabels = kDateEEE.distinct()

                    // Get index of each distinct value in the list of string dates
                    var kXAxisIndex = ArrayList<Int>()
                    kXAxisLabels.forEach {
                        kXAxisIndex.add(kDateEEE.indexOf(it))
                    }

                    // Create axis values
                    for (i in 0 until kXAxisIndex.size) {
                        kXAxisValues.add(
                            AxisValue(
                                kDateMillis[kXAxisIndex[i]].toFloat(),
                                kXAxisLabels[i].toCharArray()
                            )
                        )
                    }

                    // Add values for x Axis
                    kXAxis.values = kXAxisValues

                    // Lines formatting
                    kLine.forEach {
                        it.hasLabelsOnlyForSelected = true
                        it.isFilled = true
                        it.hasPoints = false
                        it.strokeWidth = 1
                        it.color = kLineColor
                        it.pointRadius = 1
                        it.hasLabels = true

                    }

                    // Create a LineChartData using time and Line data
                    var kChart = LineChartData(kLine)

                    // Add axis values and push it in the chart
                    kChart.axisXBottom = kXAxis
                    kChart.axisYRight = kYaxis

                    view_GraphMain.lineChartData = kChart

                    val tempViewport = view_GraphMain?.maximumViewport.copy()
                    val dx = tempViewport.width() * 2f / 3f
                    tempViewport.offset(dx, 0f)

                    // If a preview view is available, displayPreviewChart(), i.e. we are not in the main activity view
                    if (this.context::class != MainActivity::class) {
                        displayPreviewGraph(kChart, tempViewport)
                    }
                }

            }

    }

    fun formatLabel() {
        if (context::class == MainActivity::class) {

            val latestPointValue = kLine.last().values.last()
//        val latestDate = getDate(latestPointValue.x, "EEE, MMM d - h:mm a")


            // Display Current Value
            if (kXAxis.name == "Blood Glucose") {
                (context as Activity).findViewById<TextView>(kValueViewID).text =
                    "%.2f".format(latestPointValue.y)
            } else {
                (context as Activity).findViewById<TextView>(kValueViewID).text =
                    "%.0f".format(latestPointValue.y)
            }

            // Display latest date in association to the latest value
            (context as Activity).findViewById<TextView>(kDateViewID).text = getDate(latestPointValue.x.toLong(), "EEE, MMM d - h:mm a")

            // Format Label
            // 1 - Warning

            if ((kXAxis.name == "Blood Glucose" && latestPointValue.y < 4.0) || (kXAxis.name == "Steps" && latestPointValue.y < 1000) || ((kXAxis.name == "Heart Rate" && latestPointValue.y > 100) || (kXAxis.name == "Heart Rate" && latestPointValue.y < 40))) {
                (context as Activity).findViewById<TextView>(kLabelViewID).text = "WARNING"
                (context as Activity).findViewById<TextView>(kLabelViewID).setBackgroundColor(
                    (context as Activity).getResources().getColor(state_warning)
                )

            }
            // 2- Normal
            else if ((kXAxis.name == "Blood Glucose" && latestPointValue.y >= 4.0) || (kXAxis.name == "Steps" && latestPointValue.y >= 1000) || ((kXAxis.name == "Heart Rate" && latestPointValue.y <= 100) || (kXAxis.name == "Heart Rate" && latestPointValue.y >= 40))) {
                (context as Activity).findViewById<TextView>(kLabelViewID).text = "NORMAL"
                (context as Activity).findViewById<TextView>(kLabelViewID)
                    .setBackgroundColor((context as Activity).getResources().getColor(state_normal))
            }
        }
    }




    /** Display Preview graphs using KelloCharts Lib
     * @param kChart: LineChartData of the main chart - used to copy same data to the preview graph
     * @param viewport: temporary viewport to call listener
     */
    fun displayPreviewGraph(kChart: LineChartData, viewPort: Viewport) {
        // Declare views
        var view_GraphPreview = (context as Activity).findViewById<LineChartView>(kChartPreViewID)
        view_GraphPreview.lineChartData = kChart

        var view_GraphMain = (context as Activity).findViewById<LineChartView>(kChartViewID)

        // Viewport listener for main and preview graphs
        view_GraphPreview?.setViewportChangeListener(ChartPreviewPortListener(kChartViewID))
        view_GraphMain?.setViewportChangeListener(ChartViewportListener(kChartPreViewID))


        view_GraphPreview?.currentViewport = viewPort
        view_GraphMain?.currentViewport = viewPort

        view_GraphPreview?.zoomType = ZoomType.HORIZONTAL
    }


    private inner class ChartPreviewPortListener(chartMain_ViewID : Int) : ViewportChangeListener {

        var chartMainView = (context as Activity).findViewById<LineChartView>(chartMain_ViewID)

        override fun onViewportChanged(newViewport: Viewport) {
            if (!updatingPreviewViewport) {
                updatingChartViewport = true
                chartMainView.zoomType = ZoomType.HORIZONTAL
                chartMainView.currentViewport = newViewport
                updatingChartViewport = false
            }
        }
    }

    private inner class ChartViewportListener(chartPreview_ViewID : Int) : ViewportChangeListener {
        var chartPreviewView = (context as Activity).findViewById<LineChartView>(chartPreview_ViewID)

        override fun onViewportChanged(newViewport: Viewport) {
            if (!updatingChartViewport) {
                updatingPreviewViewport = true
                chartPreviewView.zoomType = ZoomType.HORIZONTAL
                chartPreviewView.currentViewport = newViewport
                updatingPreviewViewport = false
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
            kDateEEE.add(getDate(measure.getLong("date"), "EEE"))

            // Get BG values and create associated PointValue
            val sgv = measure.getDouble("sgv")/18
            kLineValues.add(PointValue(measure.getLong("date").toFloat(),sgv.toFloat(), "%.2f".format(sgv)))
        }

        // Add glucose data to GFit (Push)

            pushGlucoData()
    }

    /**
     * Push last 1000 gluco data from XDrip to GFit.
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