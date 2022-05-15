package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import co.csadev.kellocharts.gesture.ZoomType
import co.csadev.kellocharts.listener.ViewportChangeListener
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.view.AbstractChartView
import co.csadev.kellocharts.view.LineChartView
import co.csadev.kellocharts.view.PreviewLineChartView
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
                                dataHealth.kLineValues.add(PointValue(dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),dp.getValue(Field.FIELD_STEPS).asInt().toFloat(), dp.getValue(Field.FIELD_STEPS).asInt().toString()))
                            }
                        }
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
            dataHealth.displayMainGraph()
        }
    }

    /** Class variables per category: general, charting, data structure, GoogleFit variables,etc. )
     */
    // Data Initialization
    var context = context

    // Views to plot graph and add values
    var kChartView: AbstractChartView? = null
    var kChartPreView: AbstractChartView? = null

    var mValueView: TextView? = null
    var mLabelView: TextView? = null
    var mDateView : TextView? = null
    var mButton : Button? = null
    lateinit var mname: String
    var micon = R.drawable.icn_bg

    // Chart variables (Main view)
    var mcolor_primary = ContextCompat.getColor(context, R.color.orange_primary)
    var mcolor_secondary = ContextCompat.getColor(context, R.color.orange_secondary)
    var kLine = ArrayList<Line>()
    var kLineValues = ArrayList<PointValue>()

    var kXAxis = Axis()
    var kYaxis = Axis()

    var kXAxisValues = ArrayList<AxisValue>()
    var kDateMillis = ArrayList<Long>()
    var kDateEEE = ArrayList<String>()
    var kStrokeWidth = 1

    // Chart Variables (Detailed view)
    var updatingPreviewViewport = false
    var updatingChartViewport = false

    //GFit variables
    lateinit var gFitDataType: DataType
    lateinit var gFitBucketTime: TimeUnit


    // Variables initialization for each data type:
    init {
        if (viewID != -1) {kChartView = (context as Activity).findViewById(viewID)}
        if (previewID != -1) {kChartPreView = (context as Activity).findViewById(previewID)}
        if (valueID != -1) {mValueView = (context as Activity).findViewById(valueID)}
        if (labelID != -1) {mLabelView = (context as Activity).findViewById(labelID)}
        if (dateID != -1) {mDateView = (context as Activity).findViewById(dateID)}

        if (string === "Blood Glucose") {
            gFitDataType = TYPE_BLOOD_GLUCOSE
            gFitBucketTime = TimeUnit.DAYS
            kXAxis.name = string
            kYaxis.name = "mmol/L"
            kStrokeWidth = 1

            mname = "Blood Glucose"
            mcolor_primary = ContextCompat.getColor(context, red_primary)
            mcolor_secondary = ContextCompat.getColor(context, red_secondary)
            micon = R.drawable.icn_bg
        }

        else if (string === "Steps") {
            gFitDataType = TYPE_STEP_COUNT_DELTA
            if (context::class == MainActivity::class ) {gFitBucketTime = TimeUnit.DAYS}
            else                                        {gFitBucketTime = TimeUnit.HOURS}

            kXAxis.name = string
            kYaxis.name = ""
            kStrokeWidth = 4

            mname = "Steps"
            mcolor_primary = ContextCompat.getColor(context, orange_primary)
            mcolor_secondary = ContextCompat.getColor(context, orange_secondary)
            micon = R.drawable.icn_steps
        }

        else if (string === "Heart Rate") {
            gFitDataType = TYPE_HEART_RATE_BPM
            gFitBucketTime = TimeUnit.DAYS
            kXAxis.name = string
            kYaxis.name = "bpm"
            kStrokeWidth = 1

            mname = "Heart Rate"
            mcolor_primary = ContextCompat.getColor(context, blue_primary)
            mcolor_secondary = ContextCompat.getColor(context, blue_secondary)
            micon = R.drawable.icn_hr
        }

        else if (string === "Blood Pressure") {
            gFitDataType = TYPE_BLOOD_PRESSURE
            gFitBucketTime = TimeUnit.DAYS
            kXAxis.name = string
            kYaxis.name = "mmHg"
            kStrokeWidth = 4

            mname = "Blood Pressure"
            mcolor_primary = ContextCompat.getColor(context, pink_primary)
            mcolor_secondary = ContextCompat.getColor(context, pink_secondary)
            micon = R.drawable.icn_bp
        }



    }

    fun bind(holder: ModuleViewHolder){
        kChartView = holder.moduleChart
        mValueView = holder.moduleValue
        mLabelView = holder.moduleLabel
        mDateView  = holder.moduleDate
        mButton  = holder.moduleBtn
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
         if (kDateMillis.size > 1 && kChartView != null) {

                if (kChartView is LineChartView) {

                    // All cases except Blood Pressure: 1 line with Point Values
                    if (mname == "Blood Glucose" || mname == "Heart Rate"|| (mname == "Steps" && context::class != MainActivity::class )) {
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
                    if (mname == "Blood Pressure" || (mname == "Steps" && context::class == MainActivity::class ) ) {
                        // sort Lines in chronological order
                        kLine = sortData(kDateMillis, kLine).toMutableList() as ArrayList


                    }

                    // Last value to fill textfield + Label update depending on the last value
                    formatLabel()
                    kYaxis = Axis(hasLines = true, maxLabels = 4)

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
                        it.strokeWidth = kStrokeWidth
                        it.color = mcolor_primary
                        it.pointRadius = 1
                        it.hasLabels = true

                    }

                    // Create a LineChartData using time and Line data
                    var kChart = LineChartData(kLine)

                    // Add axis values and push it in the chart
                    kChart.axisXBottom = kXAxis
                    kChart.axisYRight = kYaxis

                    (kChartView as LineChartView).lineChartData = kChart
                    val tempViewport = kChartView?.maximumViewport.copy()
                    val tempPreViewport = tempViewport.copy()

                    // If in main activity, add an inset to have the entire labels for axis
                    if (this.context::class == MainActivity::class) {
                        tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.05f)
                        kChartView?.maximumViewport = tempViewport
                        kChartView?.currentViewport = tempViewport
                    }

                    // If a preview view is available, displayPreviewChart(), i.e. we are not in the main activity view
                    if (this.context::class != MainActivity::class) {
                        val dx = tempPreViewport.width() * 2f / 3f
                        tempPreViewport.offset(dx, 0f)
                        displayPreviewGraph(kChart, tempPreViewport)
                    }
                }
            }
    }


    fun getLastValue(): MutableList<PointValue> {
        if (mname == "Steps" || mname == "Blood Pressure"){
            for (i in kLine.size-1 downTo 0) {
                if (!kLine[i].values.last().y.isNaN()) {
                    return kLine[i].values
                }
            }
        }
        else {
            return mutableListOf(kLine.last().values.last())
        }
        return mutableListOf()
    }


    /** Label formater + date of last value
     */
    fun formatLabel() {
        if (mValueView != null) {
            var latestValueList = getLastValue()
//            val latestPointValue = kLine.last().values.last()

            if (!latestValueList.isEmpty()) {
                // Display Current Value
                if (mname == "Blood Glucose") {
                    mValueView!!.text = "%.2f".format(latestValueList[0].y)
                } else if (mname == "Blood Pressure") {
                    mValueView!!.text = "%.0f-%.0f".format(latestValueList[1].y, latestValueList[0].y)
                } else if (mname == "Steps") {
                    mValueView!!.text = "%.0f".format(latestValueList[1].y)
                } else {
                    mValueView!!.text = "%.0f".format(latestValueList[0].y)
                }

                // Display latest date in association to the latest value
                if (mDateView != null) {
                    mDateView!!.text = getDate(latestValueList[0].x.toLong(), "EEE, MMM d - h:mm a")

                    // Set text colors
                    mValueView!!.setTextColor(mcolor_primary)
                    mDateView!!.setTextColor(mcolor_primary)
                }

                // Format Label
                // 1 - Warning

                if (
                    (mname == "Blood Glucose" && latestValueList[0].y < 4.0)
                    || (mname == "Steps" && latestValueList[1].y < 1000)
                    || (mname == "Heart Rate" && (latestValueList[0].y > 100 || latestValueList[0].y < 40))
                    || (mname == "Blood Pressure" && (latestValueList[0].y < 60 || latestValueList[1].y > 120))
                ) {
                    mLabelView!!.text = "WARNING"
                    mLabelView!!.setBackgroundColor(
                        (context as Activity).getResources().getColor(state_warning)
                    )

                }
                // 2- Normal
                else {
//            else if ((kXAxis.name == "Blood Glucose" && latestPointValue.y >= 4.0) || (kXAxis.name == "Steps" && latestPointValue.y >= 1000) || ((kXAxis.name == "Heart Rate" && latestPointValue.y <= 100) || (kXAxis.name == "Heart Rate" && latestPointValue.y >= 40))) {
                    mLabelView!!.text = "NORMAL"
                    mLabelView!!.setBackgroundColor((context as Activity).getResources().getColor(state_normal))
                }
            }
        }
    }

    /** Display Preview graphs using KelloCharts Lib
     * @param kChart: LineChartData of the main chart - used to copy same data to the preview graph
     * @param viewport: temporary viewport to call listener
     */
    fun displayPreviewGraph(kChart: LineChartData, viewPort: Viewport) {
        // display graph on preview view
        (kChartPreView as PreviewLineChartView).lineChartData = kChart

        // Viewport listener for main and preview graphs
        (kChartPreView as PreviewLineChartView)?.setViewportChangeListener(ChartPreviewPortListener())
        (kChartView as LineChartView)?.setViewportChangeListener(ChartViewportListener())

        (kChartPreView as PreviewLineChartView)?.currentViewport = viewPort
        (kChartView as LineChartView)?.currentViewport = viewPort

        (kChartPreView as PreviewLineChartView)?.zoomType = ZoomType.HORIZONTAL
    }

    /** Viewport listener to adapt the Main view depending on preview viewport
     */
    private inner class ChartPreviewPortListener() : ViewportChangeListener {
        override fun onViewportChanged(newViewport: Viewport) {
            if (!updatingPreviewViewport) {
                updatingChartViewport = true
                (kChartView as LineChartView).zoomType = ZoomType.HORIZONTAL
                (kChartView as LineChartView).currentViewport = newViewport
                updatingChartViewport = false
            }
        }
    }

    /** Viewport listener to adapt the Preview view depending on main view viewport
     */
    private inner class ChartViewportListener() : ViewportChangeListener {
        override fun onViewportChanged(newViewport: Viewport) {
            if (!updatingChartViewport) {
                updatingPreviewViewport = true
                (kChartPreView as LineChartView).zoomType = ZoomType.HORIZONTAL
                (kChartPreView as LineChartView).currentViewport = newViewport
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