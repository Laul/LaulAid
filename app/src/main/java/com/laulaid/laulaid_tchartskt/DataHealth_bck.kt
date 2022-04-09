//////package com.laulaid.laulaid_tchartskt
//////
//////// General
//////import android.app.Activity
//////import android.util.Log
//////import android.content.Context
//////
//////
//////// HTTP Request
//////import com.android.volley.toolbox.Volley
//////import com.android.volley.RequestQueue
//////import com.android.volley.Request
//////import com.android.volley.toolbox.StringRequest
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
//////import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
//////import com.google.android.gms.auth.api.signin.GoogleSignIn
//////import com.google.android.gms.fitness.Fitness
//////import com.google.android.gms.fitness.FitnessOptions
//////import com.google.android.gms.fitness.data.*
//////import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
//////import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
//////import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
//////import com.google.android.gms.fitness.request.DataReadRequest
//////import com.google.android.gms.fitness.result.DataReadResponse
//////import org.json.JSONArray
//////
//////// charting
//////import com.klim.tcharts.entities.ChartItem
//////
//////// General
//////import java.util.concurrent.TimeUnit
//////
//////
//////class DataHealth_bck(string: String, context: Context) {
//////
//////    /** Companion object to access variables and function of the class outside
//////     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
//////     */
//////    companion object {
//////        var dataDetailedView = ArrayList<ChartItem>()
//////        val fitnessOptions = FitnessOptions.builder()
//////            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//////            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//////            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
//////            .build()
//////
//////        /** Steps data parsing + formatting to display graph
//////        @param InitData: Flag to reset data container
//////        @param response: Google fit response
//////        @param dataHealth: structure containing all class variables to display graphs
//////         */
//////        fun parseGFitData(InitData: Boolean, response: DataReadResponse, dataHealth: DataHealth_bck){
//////            lateinit var Timestamp : String
//////
//////            // If 1st call: clear data since a new call
//////            if (InitData==true){
//////                dataHealth.data.clear()
//////                dataHealth.dataXAxis.clear()
//////
//////                for (i in 0 until dataHealth.datasize){
//////                    dataHealth.data.add(ArrayList<Float>(dataHealth.duration))
//////                }
//////            }
//////
//////            // Check each bucket to retrieve data
//////            for (dataSet in response.buckets.flatMap { it.dataSets }) {
//////
//////                // Steps
//////                if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.data[0].add(dp.getValue(Field.FIELD_STEPS).asInt().toFloat())
//////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
//////                    }
//////                }
//////
//////                // Blood Pressure
//////                else if (dataSet.dataType==TYPE_BLOOD_PRESSURE){
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.data[0].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat())
//////                        dataHealth.data[1].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat())
//////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
//////                    }
//////                }
//////
//////                // Heart Rate
//////                else if (dataSet.dataType==TYPE_HEART_RATE_BPM){
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.data[0].add(dp.getValue(Field.FIELD_BPM).asFloat())
//////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
//////                    }
//////                }
//////            }
//////
//////            // Display graph callback only if all data have been aggregated (completed vs non completed days)
//////            if (!InitData){dataHealth.displayGraphPreview()}
//////
//////        }
//////    }
//////
//////    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
//////
//////    /** Class variables per catgeory: general, charting, data structure, GoogleFit variables,etc. )
//////     */
//////    // Data Initialization
//////    var context = context
//////
//////    // Chart variables
//////    var aaChartTitle = string
//////    lateinit var aaChartViewID: AAChartView
//////    lateinit var aaChartType: AAChartType
//////    var aaChartLegend = false
//////    var aaChartLabels = true
//////
//////    // Data structure
//////    var datasize = 1
//////    var data = ArrayList<ArrayList<Float>>()
//////    var dataXAxis= ArrayList<String>()
//////
//////    //GFit variables
//////    var duration: Int = 6
//////    lateinit var gFitDataType: DataType
//////    var gFitDataSource: Int = 0
//////    lateinit var gFitBucketTime: TimeUnit
//////    lateinit var gFitStreamName: String
//////
//////    // Variables initialization for each data type:
//////    init {
//////
//////        if (string === "Steps") {
//////            aaChartViewID =
//////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_steps)
//////            aaChartType = AAChartType.Column
//////
//////            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "estimated_steps"
//////
//////        }
//////        if (string === "Blood Pressure") {
//////            aaChartViewID =
//////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_bloodpressure)
//////            aaChartType = AAChartType.Line
//////            aaChartLabels = true
//////
//////            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "Blood Pressure"
//////            datasize = 2
//////        }
//////        if (string === "Heart Rate") {
//////            aaChartViewID =
//////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_heartrate)
//////            aaChartType = AAChartType.Line
//////            gFitDataType = DataType.TYPE_HEART_RATE_BPM
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "Heart Rate"
//////        }
//////
//////        // Create as many series of data as in the datafields from GFit
//////        for (i in 0 until datasize){
//////            data.add(ArrayList<Float>(duration))
//////        }
//////    }
//////
//////    /** GFit permissions verification and dispatch
//////    @param context: App Context (typically main activity)
//////     */
//////    fun connectGFit(context: Activity) {
//////        // If no permission -> request permission to the user
//////        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
//////            GoogleSignIn.requestPermissions(
//////                context, // your activity
//////                1, // e.g. 1
//////                getGoogleAccount(context),
//////                fitnessOptions
//////            )
//////        }
//////        // If permission -> get Data
//////        else {
//////            this.getGFitData(duration)
//////        }
//////    }
//////
//////    /** GFit connection request based on fitness option list
//////    @param context: App Context (typically main activity)
//////     */
//////    private fun getGoogleAccount(context: Context) =
//////        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
//////
//////    /** GFit connection to retrieve fit data
//////    @param duration: duration to cover (default: last 7 days)
//////     */
//////    fun getGFitData(duration: Int) {
//////        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
//////
//////        // Request for current (non completed) day / hour
//////        var ReqCurrentTimes = DataReadRequest.Builder()
//////            .read(gFitDataType)
//////            .bucketByTime(1, gFitBucketTime)
//////            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//////            .build()
//////
//////        // Default request using ".read" - For steps, we need to use ".aggregate"
//////        // Request for past (completed) days / hours
//////        var ReqCompletedTimes = DataReadRequest.Builder()
//////            .read(gFitDataType)
//////            .bucketByTime(1, gFitBucketTime)
//////            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//////            .build()
//////
//////        if (aaChartTitle == "Steps") {
//////            // Request for current (non completed) day / hour
//////            ReqCurrentTimes = DataReadRequest.Builder()
//////                .aggregate(gFitDataType)
//////                .bucketByTime(1, gFitBucketTime)
//////                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//////                .build()
//////
//////            // Request for past (completed) days / hours
//////            ReqCompletedTimes = DataReadRequest.Builder()
//////                .aggregate(gFitDataType)
//////                .bucketByTime(1, gFitBucketTime)
//////                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//////                .build()
//////        }
//////
//////        // History request and go to parse data
//////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth_bck.fitnessOptions))
//////            .readData(ReqCompletedTimes)
//////            .addOnSuccessListener { response -> parseGFitData(false, response, this)}
//////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//////
//////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth_bck.fitnessOptions))
//////            .readData(ReqCurrentTimes)
//////            .addOnSuccessListener { response -> parseGFitData(true, response, this)}
//////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//////    }
//////
//////    /** Display preview graphs on main using AAChart Lib
//////     */
//////    fun displayGraphPreview() {
//////        // Create data series to display
//////        var dataserie = ArrayList<AASeriesElement>()
//////        for (i in 0 until data.size){
//////            dataserie.add(AASeriesElement()
//////                .name(aaChartTitle)
//////                .data(data[i].toArray()))
//////        }
//////
//////        // Create chart using dataserie and other UI variables
//////        val aaChartModel: AAChartModel = AAChartModel()
//////            .chartType(aaChartType)
//////            .title(aaChartTitle)
//////            .backgroundColor("#EFEFEF")
//////            .legendEnabled(false)
//////            .dataLabelsEnabled(true)
//////            .yAxisLabelsEnabled(false)
//////            .categories(dataXAxis.toTypedArray())
//////            .series(dataserie.toArray())
//////
//////        //The chart view object calls the instance object of AAChartModel and draws the final graphic
//////        aaChartViewID.aa_drawChartWithChartModel(aaChartModel)
//////    }
//////
//////
//////
//////
//////    fun displayGraphDetailed(){
//////    }
//////
//////
//////    fun connectXDrip(url: String, context: Context) {
//////        var mRequestQueue: RequestQueue? = null
//////        var mStringRequest: StringRequest? = null
//////
//////        // Request XDrip connection and permissions
//////        //RequestQueue initialized
//////        mRequestQueue = Volley.newRequestQueue(context)
//////
//////
//////        //String Request initialized
//////        mStringRequest = StringRequest(
//////            Request.Method.GET, url,
//////            { response ->
//////                run {
//////                    getGlucoData(response)
//////                    // pushGlucoToGFit(response)
//////                }
//////            }) { error ->
//////            Log.i(TAG, "Unable to connect to XDrip")
//////        }
//////        mRequestQueue!!.add(mStringRequest)
//////    }
//////
//////    /* Gluco data parsing + formatting to display graph
//////     @param response: XDrip json response
//////    */
//////    fun getGlucoData(jsonstring: String){
//////        // set variables for display
//////        // val dataViewID = findViewById<TChart>(R.id.graph_steps)
//////
//////
//////        // parse gluco data
//////        val json = JSONArray(jsonstring)
//////        dataDetailedView.clear()
//////
//////        for (i in 0 until json.length()) {
//////            val measure = json.getJSONObject(i)
//////            val date = measure.getLong("date")
//////            val sgv = measure.getInt("sgv")
//////            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
//////        }
//////
//////        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
//////    }
//////
//////}
////
////
////
////
////
//////----------------
////
////package com.laulaid.laulaid_tchartskt
////
////// General
////
////
////// HTTP Request
////
////// charting
////
////// General
////import android.app.Activity
////import android.content.Context
////import android.util.Log
////import co.csadev.kellocharts.model.*
////import co.csadev.kellocharts.util.ChartUtils
////import co.csadev.kellocharts.view.AbstractChartView
////import co.csadev.kellocharts.view.ColumnChartView
////import co.csadev.kellocharts.view.LineChartView
////import com.android.volley.Request
////import com.android.volley.RequestQueue
////import com.android.volley.toolbox.StringRequest
////import com.android.volley.toolbox.Volley
////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
////import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
////import com.google.android.gms.auth.api.signin.GoogleSignIn
////import com.google.android.gms.fitness.Fitness
////import com.google.android.gms.fitness.FitnessOptions
////import com.google.android.gms.fitness.data.*
////import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
////import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
////import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
////import com.google.android.gms.fitness.request.DataReadRequest
////import com.google.android.gms.fitness.result.DataReadResponse
////import com.klim.tcharts.entities.ChartItem
////import org.json.JSONArray
////import java.util.concurrent.TimeUnit
////
////
////class DataHealth(string: String, context: Context) {
////
////    /** Companion object to access variables and function of the class outside
////     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
////     */
////    companion object {
////        var dataDetailedView = ArrayList<ChartItem>()
////        val fitnessOptions = FitnessOptions.builder()
////            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
////            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
////            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
////            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
////            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
////            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
////            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
////            .build()
////
////        /** Steps data parsing + formatting to display graph
////        @param InitData: Flag to reset data container
////        @param response: Google fit response
////        @param dataHealth: structure containing all class variables to display graphs
////         */
////        fun parseGFitData(response: DataReadResponse, dataHealth: DataHealth){
////            lateinit var Timestamp : String
////
////            for (dataSet in response.buckets.flatMap { it.dataSets }) {
////                // Steps
////                if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
////                    for (dp in dataSet.dataPoints) {
////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                        dataHealth.kValues_Column.add(Column(arrayListOf(SubcolumnValue(dp.getValue(Field.FIELD_STEPS).asInt().toFloat(),ChartUtils.pickColor()))))
////
////                    }
////                }
////
////                // Blood Pressure
////                else if (dataSet.dataType == TYPE_BLOOD_PRESSURE) {
////                    for (dp in dataSet.dataPoints) {
////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                        dataHealth.kValues_Line.add(
////                            Line(
////                                arrayListOf(
////                                    PointValue(
////                                        dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                        dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC)
////                                            .asFloat(),
////                                        label = ""
////
////                                    ),
////
////                                    PointValue(
////                                        dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                        dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC)
////                                            .asFloat(),
////                                        label = ""
////
////                                    )
////                                )
////                            )
////                        )
////                    }
////                }
////
////                // Heart Rate
////                else if (dataSet.dataType == TYPE_HEART_RATE_BPM) {
////
////                    for (dp in dataSet.dataPoints) {
////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                        // Create entire list of point values
////                        dataHealth.kValues_PointValues.add(PointValue(
////                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                            dp.getValue(Field.FIELD_BPM)
////                                .asFloat(),
////                            ""
////                        ))
////                    }
////                    dataHealth.kValues_Line.add(
////                        Line(
////                            arrayListOf(
////                                PointValue(
//////                                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
//////                                            dp.getValue(Field.FIELD_BPM)
//////                                                .asFloat(),
//////                                            ""
////                                )
////                            )
////                        )
////                    )
////
////                }
////            }
////
////
////            dataHealth.displayGraphPreview_K()
////        }
////    }
////
////    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
////
////    /** Class variables per catgeory: general, charting, data structure, GoogleFit variables,etc. )
////     */
////    // Data Initialization
////    var context = context
////
////    // Chart variables
////    lateinit var kChartViewID: AbstractChartView
////
////    // Data structure - AAChart
////    var datasize = 1
////
////    // Data Structure - KelloCharts
////    val kValues_Column = ArrayList<Column>()
////    val kValues_PointValues = ArrayList<PointValue>()
////    val kValues_PointValuesLine = Line(kValues_PointValues)
////    val kValues_Line = ArrayList(kValues_PointValuesLine)
////
////
////    var kXAxisValues = ArrayList<AxisValue>()
////    var kXAxis = Axis()
////    var kXAxisValuesMillis = ArrayList<Long>()
////
////    //GFit variables
////    var duration: Int = 6
////    lateinit var gFitDataType: DataType
////    lateinit var gFitBucketTime: TimeUnit
////    lateinit var gFitStreamName: String
////
////    // Variables initialization for each data type:
////    init {
////
////        if (string === "Steps") {
////            // KelloChart
////            kChartViewID =(context as Activity).findViewById<ColumnChartView>(R.id.graph_main_steps)
////            kXAxis.name = string
////
////            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
////            gFitBucketTime = TimeUnit.DAYS
////            gFitStreamName = "estimated_steps"
////
////        }
////        if (string === "Blood Pressure") {
////            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_BP)
////            kXAxis.name = string
////
////            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
////            gFitBucketTime = TimeUnit.DAYS
////            gFitStreamName = "Blood Pressure"
////            datasize = 2
////        }
////        if (string === "Heart Rate") {
////            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_HR)
////            kXAxis.name = string
////
////            gFitDataType = DataType.TYPE_HEART_RATE_BPM
////            gFitBucketTime = TimeUnit.DAYS
////            gFitStreamName = "Heart Rate"
////        }
////
////
////    }
////
////    /** GFit permissions verification and dispatch
////    @param context: App Context (typically main activity)
////     */
////    fun connectGFit(context: Activity) {
////        // If no permission -> request permission to the user
////        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
////            GoogleSignIn.requestPermissions(
////                context, // your activity
////                1, // e.g. 1
////                getGoogleAccount(context),
////                fitnessOptions
////            )
////        }
////        // If permission -> get Data
////        else {
////            this.getGFitData(duration)
////        }
////    }
////
////    /** GFit connection request based on fitness option list
////    @param context: App Context (typically main activity)
////     */
////    private fun getGoogleAccount(context: Context) =
////        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
////
////    /** GFit connection to retrieve fit data
////    @param duration: duration to cover (default: last 7 days)
////     */
////    fun getGFitData(duration: Int) {
////        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
////
////        // Request for current (non completed) day / hour
////        var ReqCurrentTimes = DataReadRequest.Builder()
////            .read(gFitDataType)
////            .bucketByTime(1, gFitBucketTime)
////            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
////            .build()
////
////        // Default request using ".read" - For steps, we need to use ".aggregate"
////        // Request for past (completed) days / hours
////        var ReqCompletedTimes = DataReadRequest.Builder()
////            .read(gFitDataType)
////            .bucketByTime(1, gFitBucketTime)
////            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
////            .build()
////
////        if (kXAxis.name == "Steps") {
////            // Request for current (non completed) day / hour
////            ReqCurrentTimes = DataReadRequest.Builder()
////                .aggregate(gFitDataType)
////                .bucketByTime(1, gFitBucketTime)
////                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
////                .build()
////
////            // Request for past (completed) days / hours
////            ReqCompletedTimes = DataReadRequest.Builder()
////                .aggregate(gFitDataType)
////                .bucketByTime(1, gFitBucketTime)
////                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
////                .build()
////        }
////
////        // Clear Data before retrieving other data from GFit
////        kXAxisValuesMillis.clear()
////        kValues_Column.clear()
////        kValues_Line.clear()
////
////        // History request and go to parse data
////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
////            .readData(ReqCompletedTimes)
////            .addOnSuccessListener { response -> parseGFitData(response, this)}
////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
////
////
////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
////            .readData(ReqCurrentTimes)
////            .addOnSuccessListener { response -> parseGFitData(response, this)}
////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
////    }
////
////    /** Data sorting based on object data index
////    @param valMilli: Values in milliseconds - Used to determine the order to sort the sort
////    @param valData: Values to sort based on valMilli order
////     */
////    fun <T>sortData (valMilli: ArrayList<Long>, valData: ArrayList<T>):MutableList<T> {
////        val dateComparator = Comparator { col1: T, col2:T  ->
////            (valMilli[valData.indexOf(col1)] - valMilli[valData.indexOf(col2)]).toInt()
////        }
////
////        return valData.sortedWith(dateComparator) as MutableList<T>
////    }
////
////    /** Display preview graphs on main using KelloCharts Lib
////     */
////    fun displayGraphPreview_K() {
////        kXAxisValues.clear()
////
////        // Compute x-Axis values and sort all values by chronological order
////        if (kXAxisValuesMillis.size != 0) {
////            // Sort time in milliseconds
////            var kXAxisValuesMillisSorted = ArrayList<Long>(kXAxisValuesMillis)
////            kXAxisValuesMillisSorted.sort()
////
////            // Sort values to plot
////            var kXAxisValuesSorted = ArrayList<String>()
////            kXAxisValuesMillisSorted.forEach {
////                kXAxisValuesSorted.add(DataGeneral.getDate(it, "EEE").toString())
////            }
////
////            // Display Graph
////            if (kXAxisValuesSorted.size > 1){
////                // Format column charts
////                if (kChartViewID is ColumnChartView) {
////                    for (i in 0 until kXAxisValuesSorted.size) {
////                        kXAxisValues.add(AxisValue(i, kXAxisValuesSorted[i]))
////                    }
////                    kXAxis.values = kXAxisValues
////
////                    var kChart = ColumnChartData(sortData(kXAxisValuesMillis, kValues_Column), false, false)
////
////                    kChart.axisXBottom = kXAxis
////                    (kChartViewID as ColumnChartView).columnChartData = kChart
////                }
////
////                // Format Line charts
////                else if (kChartViewID is LineChartView) {
////                    for (i in 0 until kXAxisValuesSorted.size) {
////                        kXAxisValues.add(AxisValue(kXAxisValuesMillisSorted[i].toFloat(), kXAxisValuesSorted[i].toCharArray()))
////                    }
////                    kXAxis.values = kXAxisValues
////
////                    var kChart = LineChartData(sortData(kXAxisValuesMillis, kValues_Line))
//////                    kXAxis.hasLines = true
////                    kChart.axisXBottom = kXAxis
////                    kXAxis.isAutoGenerated = true
////                    (kChartViewID as LineChartView).lineChartData = kChart
////                }
////            }
////        }
////    }
////
////
////    fun displayGraphDetailed(){
////    }
////
////
////    fun connectXDrip(url: String, context: Context) {
////        var mRequestQueue: RequestQueue? = null
////        var mStringRequest: StringRequest? = null
////
////        // Request XDrip connection and permissions
////        //RequestQueue initialized
////        mRequestQueue = Volley.newRequestQueue(context)
////
////
////        //String Request initialized
////        mStringRequest = StringRequest(
////            Request.Method.GET, url,
////            { response ->
////                run {
////                    getGlucoData(response)
////                    // pushGlucoToGFit(response)
////                }
////            }) { error ->
////            Log.i(TAG, "Unable to connect to XDrip")
////        }
////        mRequestQueue!!.add(mStringRequest)
////    }
////
////    /* Gluco data parsing + formatting to display graph
////     @param response: XDrip json response
////    */
////    fun getGlucoData(jsonstring: String){
////        // set variables for display
////        // val dataViewID = findViewById<TChart>(R.id.graph_steps)
////
////
////        // parse gluco data
////        val json = JSONArray(jsonstring)
////        dataDetailedView.clear()
////
////        for (i in 0 until json.length()) {
////            val measure = json.getJSONObject(i)
////            val date = measure.getLong("date")
////            val sgv = measure.getInt("sgv")
////            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
////        }
////
////        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
////    }
////
////}//package com.laulaid.laulaid_tchartskt
////////
////////// General
////////import android.app.Activity
////////import android.util.Log
////////import android.content.Context
////////
////////
////////// HTTP Request
////////import com.android.volley.toolbox.Volley
////////import com.android.volley.RequestQueue
////////import com.android.volley.Request
////////import com.android.volley.toolbox.StringRequest
////////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
////////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
////////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
////////import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
////////import com.google.android.gms.auth.api.signin.GoogleSignIn
////////import com.google.android.gms.fitness.Fitness
////////import com.google.android.gms.fitness.FitnessOptions
////////import com.google.android.gms.fitness.data.*
////////import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
////////import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
////////import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
////////import com.google.android.gms.fitness.request.DataReadRequest
////////import com.google.android.gms.fitness.result.DataReadResponse
////////import org.json.JSONArray
////////
////////// charting
////////import com.klim.tcharts.entities.ChartItem
////////
////////// General
////////import java.util.concurrent.TimeUnit
////////
////////
////////class DataHealth_bck(string: String, context: Context) {
////////
////////    /** Companion object to access variables and function of the class outside
////////     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
////////     */
////////    companion object {
////////        var dataDetailedView = ArrayList<ChartItem>()
////////        val fitnessOptions = FitnessOptions.builder()
////////            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
////////            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
////////            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
////////            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
////////            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
////////            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
////////            .build()
////////
////////        /** Steps data parsing + formatting to display graph
////////        @param InitData: Flag to reset data container
////////        @param response: Google fit response
////////        @param dataHealth: structure containing all class variables to display graphs
////////         */
////////        fun parseGFitData(InitData: Boolean, response: DataReadResponse, dataHealth: DataHealth_bck){
////////            lateinit var Timestamp : String
////////
////////            // If 1st call: clear data since a new call
////////            if (InitData==true){
////////                dataHealth.data.clear()
////////                dataHealth.dataXAxis.clear()
////////
////////                for (i in 0 until dataHealth.datasize){
////////                    dataHealth.data.add(ArrayList<Float>(dataHealth.duration))
////////                }
////////            }
////////
////////            // Check each bucket to retrieve data
////////            for (dataSet in response.buckets.flatMap { it.dataSets }) {
////////
////////                // Steps
////////                if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
////////                    for (dp in dataSet.dataPoints) {
////////                        dataHealth.data[0].add(dp.getValue(Field.FIELD_STEPS).asInt().toFloat())
////////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
////////                    }
////////                }
////////
////////                // Blood Pressure
////////                else if (dataSet.dataType==TYPE_BLOOD_PRESSURE){
////////                    for (dp in dataSet.dataPoints) {
////////                        dataHealth.data[0].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat())
////////                        dataHealth.data[1].add(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat())
////////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
////////                    }
////////                }
////////
////////                // Heart Rate
////////                else if (dataSet.dataType==TYPE_HEART_RATE_BPM){
////////                    for (dp in dataSet.dataPoints) {
////////                        dataHealth.data[0].add(dp.getValue(Field.FIELD_BPM).asFloat())
////////                        dataHealth.dataXAxis.add(DataGeneral.getDate(dp.getTimestamp(TimeUnit.MILLISECONDS).toLong(),"dd/MM" ).toString())
////////                    }
////////                }
////////            }
////////
////////            // Display graph callback only if all data have been aggregated (completed vs non completed days)
////////            if (!InitData){dataHealth.displayGraphPreview()}
////////
////////        }
////////    }
////////
////////    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
////////
////////    /** Class variables per catgeory: general, charting, data structure, GoogleFit variables,etc. )
////////     */
////////    // Data Initialization
////////    var context = context
////////
////////    // Chart variables
////////    var aaChartTitle = string
////////    lateinit var aaChartViewID: AAChartView
////////    lateinit var aaChartType: AAChartType
////////    var aaChartLegend = false
////////    var aaChartLabels = true
////////
////////    // Data structure
////////    var datasize = 1
////////    var data = ArrayList<ArrayList<Float>>()
////////    var dataXAxis= ArrayList<String>()
////////
////////    //GFit variables
////////    var duration: Int = 6
////////    lateinit var gFitDataType: DataType
////////    var gFitDataSource: Int = 0
////////    lateinit var gFitBucketTime: TimeUnit
////////    lateinit var gFitStreamName: String
////////
////////    // Variables initialization for each data type:
////////    init {
////////
////////        if (string === "Steps") {
////////            aaChartViewID =
////////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_steps)
////////            aaChartType = AAChartType.Column
////////
////////            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
////////            gFitBucketTime = TimeUnit.DAYS
////////            gFitStreamName = "estimated_steps"
////////
////////        }
////////        if (string === "Blood Pressure") {
////////            aaChartViewID =
////////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_bloodpressure)
////////            aaChartType = AAChartType.Line
////////            aaChartLabels = true
////////
////////            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
////////            gFitBucketTime = TimeUnit.DAYS
////////            gFitStreamName = "Blood Pressure"
////////            datasize = 2
////////        }
////////        if (string === "Heart Rate") {
////////            aaChartViewID =
////////                (context as Activity).findViewById<AAChartView>(R.id.graph_preview_heartrate)
////////            aaChartType = AAChartType.Line
////////            gFitDataType = DataType.TYPE_HEART_RATE_BPM
////////            gFitBucketTime = TimeUnit.DAYS
////////            gFitStreamName = "Heart Rate"
////////        }
////////
////////        // Create as many series of data as in the datafields from GFit
////////        for (i in 0 until datasize){
////////            data.add(ArrayList<Float>(duration))
////////        }
////////    }
////////
////////    /** GFit permissions verification and dispatch
////////    @param context: App Context (typically main activity)
////////     */
////////    fun connectGFit(context: Activity) {
////////        // If no permission -> request permission to the user
////////        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
////////            GoogleSignIn.requestPermissions(
////////                context, // your activity
////////                1, // e.g. 1
////////                getGoogleAccount(context),
////////                fitnessOptions
////////            )
////////        }
////////        // If permission -> get Data
////////        else {
////////            this.getGFitData(duration)
////////        }
////////    }
////////
////////    /** GFit connection request based on fitness option list
////////    @param context: App Context (typically main activity)
////////     */
////////    private fun getGoogleAccount(context: Context) =
////////        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
////////
////////    /** GFit connection to retrieve fit data
////////    @param duration: duration to cover (default: last 7 days)
////////     */
////////    fun getGFitData(duration: Int) {
////////        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
////////
////////        // Request for current (non completed) day / hour
////////        var ReqCurrentTimes = DataReadRequest.Builder()
////////            .read(gFitDataType)
////////            .bucketByTime(1, gFitBucketTime)
////////            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
////////            .build()
////////
////////        // Default request using ".read" - For steps, we need to use ".aggregate"
////////        // Request for past (completed) days / hours
////////        var ReqCompletedTimes = DataReadRequest.Builder()
////////            .read(gFitDataType)
////////            .bucketByTime(1, gFitBucketTime)
////////            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
////////            .build()
////////
////////        if (aaChartTitle == "Steps") {
////////            // Request for current (non completed) day / hour
////////            ReqCurrentTimes = DataReadRequest.Builder()
////////                .aggregate(gFitDataType)
////////                .bucketByTime(1, gFitBucketTime)
////////                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
////////                .build()
////////
////////            // Request for past (completed) days / hours
////////            ReqCompletedTimes = DataReadRequest.Builder()
////////                .aggregate(gFitDataType)
////////                .bucketByTime(1, gFitBucketTime)
////////                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
////////                .build()
////////        }
////////
////////        // History request and go to parse data
////////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth_bck.fitnessOptions))
////////            .readData(ReqCompletedTimes)
////////            .addOnSuccessListener { response -> parseGFitData(false, response, this)}
////////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
////////
////////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth_bck.fitnessOptions))
////////            .readData(ReqCurrentTimes)
////////            .addOnSuccessListener { response -> parseGFitData(true, response, this)}
////////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
////////    }
////////
////////    /** Display preview graphs on main using AAChart Lib
////////     */
////////    fun displayGraphPreview() {
////////        // Create data series to display
////////        var dataserie = ArrayList<AASeriesElement>()
////////        for (i in 0 until data.size){
////////            dataserie.add(AASeriesElement()
////////                .name(aaChartTitle)
////////                .data(data[i].toArray()))
////////        }
////////
////////        // Create chart using dataserie and other UI variables
////////        val aaChartModel: AAChartModel = AAChartModel()
////////            .chartType(aaChartType)
////////            .title(aaChartTitle)
////////            .backgroundColor("#EFEFEF")
////////            .legendEnabled(false)
////////            .dataLabelsEnabled(true)
////////            .yAxisLabelsEnabled(false)
////////            .categories(dataXAxis.toTypedArray())
////////            .series(dataserie.toArray())
////////
////////        //The chart view object calls the instance object of AAChartModel and draws the final graphic
////////        aaChartViewID.aa_drawChartWithChartModel(aaChartModel)
////////    }
////////
////////
////////
////////
////////    fun displayGraphDetailed(){
////////    }
////////
////////
////////    fun connectXDrip(url: String, context: Context) {
////////        var mRequestQueue: RequestQueue? = null
////////        var mStringRequest: StringRequest? = null
////////
////////        // Request XDrip connection and permissions
////////        //RequestQueue initialized
////////        mRequestQueue = Volley.newRequestQueue(context)
////////
////////
////////        //String Request initialized
////////        mStringRequest = StringRequest(
////////            Request.Method.GET, url,
////////            { response ->
////////                run {
////////                    getGlucoData(response)
////////                    // pushGlucoToGFit(response)
////////                }
////////            }) { error ->
////////            Log.i(TAG, "Unable to connect to XDrip")
////////        }
////////        mRequestQueue!!.add(mStringRequest)
////////    }
////////
////////    /* Gluco data parsing + formatting to display graph
////////     @param response: XDrip json response
////////    */
////////    fun getGlucoData(jsonstring: String){
////////        // set variables for display
////////        // val dataViewID = findViewById<TChart>(R.id.graph_steps)
////////
////////
////////        // parse gluco data
////////        val json = JSONArray(jsonstring)
////////        dataDetailedView.clear()
////////
////////        for (i in 0 until json.length()) {
////////            val measure = json.getJSONObject(i)
////////            val date = measure.getLong("date")
////////            val sgv = measure.getInt("sgv")
////////            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
////////        }
////////
////////        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
////////    }
////////
////////}
//////
//////
//////
//////
//////
////////----------------
//////
//////package com.laulaid.laulaid_tchartskt
//////
//////// General
//////
//////
//////// HTTP Request
//////
//////// charting
//////
//////// General
//////import android.app.Activity
//////import android.content.Context
//////import android.util.Log
//////import co.csadev.kellocharts.model.*
//////import co.csadev.kellocharts.util.ChartUtils
//////import co.csadev.kellocharts.view.AbstractChartView
//////import co.csadev.kellocharts.view.ColumnChartView
//////import co.csadev.kellocharts.view.LineChartView
//////import com.android.volley.Request
//////import com.android.volley.RequestQueue
//////import com.android.volley.toolbox.StringRequest
//////import com.android.volley.toolbox.Volley
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
//////import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
//////import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
//////import com.google.android.gms.auth.api.signin.GoogleSignIn
//////import com.google.android.gms.fitness.Fitness
//////import com.google.android.gms.fitness.FitnessOptions
//////import com.google.android.gms.fitness.data.*
//////import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
//////import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
//////import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
//////import com.google.android.gms.fitness.request.DataReadRequest
//////import com.google.android.gms.fitness.result.DataReadResponse
//////import com.klim.tcharts.entities.ChartItem
//////import org.json.JSONArray
//////import java.util.concurrent.TimeUnit
//////
//////
//////class DataHealth(string: String, context: Context) {
//////
//////    /** Companion object to access variables and function of the class outside
//////     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
//////     */
//////    companion object {
//////        var dataDetailedView = ArrayList<ChartItem>()
//////        val fitnessOptions = FitnessOptions.builder()
//////            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//////            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
//////            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//////            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
//////            .build()
//////
//////        /** Steps data parsing + formatting to display graph
//////        @param InitData: Flag to reset data container
//////        @param response: Google fit response
//////        @param dataHealth: structure containing all class variables to display graphs
//////         */
//////        fun parseGFitData(response: DataReadResponse, dataHealth: DataHealth){
//////            lateinit var Timestamp : String
//////
//////            for (dataSet in response.buckets.flatMap { it.dataSets }) {
//////                // Steps
//////                if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
//////                        dataHealth.kValues_Column.add(Column(arrayListOf(SubcolumnValue(dp.getValue(Field.FIELD_STEPS).asInt().toFloat(),ChartUtils.pickColor()))))
//////
//////                    }
//////                }
//////
//////                // Blood Pressure
//////                else if (dataSet.dataType == TYPE_BLOOD_PRESSURE) {
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
//////                        dataHealth.kValues_Line.add(
//////                            Line(
//////                                arrayListOf(
//////                                    PointValue(
//////                                        dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
//////                                        dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC)
//////                                            .asFloat(),
//////                                        label = ""
//////
//////                                    ),
//////
//////                                    PointValue(
//////                                        dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
//////                                        dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC)
//////                                            .asFloat(),
//////                                        label = ""
//////
//////                                    )
//////                                )
//////                            )
//////                        )
//////                    }
//////                }
//////
//////                // Heart Rate
//////                else if (dataSet.dataType == TYPE_HEART_RATE_BPM) {
//////
//////                    for (dp in dataSet.dataPoints) {
//////                        dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
//////                        // Create entire list of point values
//////                        dataHealth.kValues_PointValues.add(PointValue(
//////                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
//////                            dp.getValue(Field.FIELD_BPM)
//////                                .asFloat(),
//////                            ""
//////                        ))
//////                    }
//////                    dataHealth.kValues_Line.add(
//////                        Line(
//////                            arrayListOf(
//////                                PointValue(
////////                                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////////                                            dp.getValue(Field.FIELD_BPM)
////////                                                .asFloat(),
////////                                            ""
//////                                )
//////                            )
//////                        )
//////                    )
//////
//////                }
//////            }
//////
//////
//////            dataHealth.displayGraphPreview_K()
//////        }
//////    }
//////
//////    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
//////
//////    /** Class variables per catgeory: general, charting, data structure, GoogleFit variables,etc. )
//////     */
//////    // Data Initialization
//////    var context = context
//////
//////    // Chart variables
//////    lateinit var kChartViewID: AbstractChartView
//////
//////    // Data structure - AAChart
//////    var datasize = 1
//////
//////    // Data Structure - KelloCharts
//////    val kValues_Column = ArrayList<Column>()
//////    val kValues_PointValues = ArrayList<PointValue>()
//////    val kValues_PointValuesLine = Line(kValues_PointValues)
//////    val kValues_Line = ArrayList(kValues_PointValuesLine)
//////
//////
//////    var kXAxisValues = ArrayList<AxisValue>()
//////    var kXAxis = Axis()
//////    var kXAxisValuesMillis = ArrayList<Long>()
//////
//////    //GFit variables
//////    var duration: Int = 6
//////    lateinit var gFitDataType: DataType
//////    lateinit var gFitBucketTime: TimeUnit
//////    lateinit var gFitStreamName: String
//////
//////    // Variables initialization for each data type:
//////    init {
//////
//////        if (string === "Steps") {
//////            // KelloChart
//////            kChartViewID =(context as Activity).findViewById<ColumnChartView>(R.id.graph_main_steps)
//////            kXAxis.name = string
//////
//////            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "estimated_steps"
//////
//////        }
//////        if (string === "Blood Pressure") {
//////            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_BP)
//////            kXAxis.name = string
//////
//////            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "Blood Pressure"
//////            datasize = 2
//////        }
//////        if (string === "Heart Rate") {
//////            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_HR)
//////            kXAxis.name = string
//////
//////            gFitDataType = DataType.TYPE_HEART_RATE_BPM
//////            gFitBucketTime = TimeUnit.DAYS
//////            gFitStreamName = "Heart Rate"
//////        }
//////
//////
//////    }
//////
//////    /** GFit permissions verification and dispatch
//////    @param context: App Context (typically main activity)
//////     */
//////    fun connectGFit(context: Activity) {
//////        // If no permission -> request permission to the user
//////        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
//////            GoogleSignIn.requestPermissions(
//////                context, // your activity
//////                1, // e.g. 1
//////                getGoogleAccount(context),
//////                fitnessOptions
//////            )
//////        }
//////        // If permission -> get Data
//////        else {
//////            this.getGFitData(duration)
//////        }
//////    }
//////
//////    /** GFit connection request based on fitness option list
//////    @param context: App Context (typically main activity)
//////     */
//////    private fun getGoogleAccount(context: Context) =
//////        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
//////
//////    /** GFit connection to retrieve fit data
//////    @param duration: duration to cover (default: last 7 days)
//////     */
//////    fun getGFitData(duration: Int) {
//////        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
//////
//////        // Request for current (non completed) day / hour
//////        var ReqCurrentTimes = DataReadRequest.Builder()
//////            .read(gFitDataType)
//////            .bucketByTime(1, gFitBucketTime)
//////            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//////            .build()
//////
//////        // Default request using ".read" - For steps, we need to use ".aggregate"
//////        // Request for past (completed) days / hours
//////        var ReqCompletedTimes = DataReadRequest.Builder()
//////            .read(gFitDataType)
//////            .bucketByTime(1, gFitBucketTime)
//////            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//////            .build()
//////
//////        if (kXAxis.name == "Steps") {
//////            // Request for current (non completed) day / hour
//////            ReqCurrentTimes = DataReadRequest.Builder()
//////                .aggregate(gFitDataType)
//////                .bucketByTime(1, gFitBucketTime)
//////                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//////                .build()
//////
//////            // Request for past (completed) days / hours
//////            ReqCompletedTimes = DataReadRequest.Builder()
//////                .aggregate(gFitDataType)
//////                .bucketByTime(1, gFitBucketTime)
//////                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//////                .build()
//////        }
//////
//////        // Clear Data before retrieving other data from GFit
//////        kXAxisValuesMillis.clear()
//////        kValues_Column.clear()
//////        kValues_Line.clear()
//////
//////        // History request and go to parse data
//////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
//////            .readData(ReqCompletedTimes)
//////            .addOnSuccessListener { response -> parseGFitData(response, this)}
//////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//////
//////
//////        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
//////            .readData(ReqCurrentTimes)
//////            .addOnSuccessListener { response -> parseGFitData(response, this)}
//////            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//////    }
//////
//////    /** Data sorting based on object data index
//////    @param valMilli: Values in milliseconds - Used to determine the order to sort the sort
//////    @param valData: Values to sort based on valMilli order
//////     */
//////    fun <T>sortData (valMilli: ArrayList<Long>, valData: ArrayList<T>):MutableList<T> {
//////        val dateComparator = Comparator { col1: T, col2:T  ->
//////            (valMilli[valData.indexOf(col1)] - valMilli[valData.indexOf(col2)]).toInt()
//////        }
//////
//////        return valData.sortedWith(dateComparator) as MutableList<T>
//////    }
//////
//////    /** Display preview graphs on main using KelloCharts Lib
//////     */
//////    fun displayGraphPreview_K() {
//////        kXAxisValues.clear()
//////
//////        // Compute x-Axis values and sort all values by chronological order
//////        if (kXAxisValuesMillis.size != 0) {
//////            // Sort time in milliseconds
//////            var kXAxisValuesMillisSorted = ArrayList<Long>(kXAxisValuesMillis)
//////            kXAxisValuesMillisSorted.sort()
//////
//////            // Sort values to plot
//////            var kXAxisValuesSorted = ArrayList<String>()
//////            kXAxisValuesMillisSorted.forEach {
//////                kXAxisValuesSorted.add(DataGeneral.getDate(it, "EEE").toString())
//////            }
//////
//////            // Display Graph
//////            if (kXAxisValuesSorted.size > 1){
//////                // Format column charts
//////                if (kChartViewID is ColumnChartView) {
//////                    for (i in 0 until kXAxisValuesSorted.size) {
//////                        kXAxisValues.add(AxisValue(i, kXAxisValuesSorted[i]))
//////                    }
//////                    kXAxis.values = kXAxisValues
//////
//////                    var kChart = ColumnChartData(sortData(kXAxisValuesMillis, kValues_Column), false, false)
//////
//////                    kChart.axisXBottom = kXAxis
//////                    (kChartViewID as ColumnChartView).columnChartData = kChart
//////                }
//////
//////                // Format Line charts
//////                else if (kChartViewID is LineChartView) {
//////                    for (i in 0 until kXAxisValuesSorted.size) {
//////                        kXAxisValues.add(AxisValue(kXAxisValuesMillisSorted[i].toFloat(), kXAxisValuesSorted[i].toCharArray()))
//////                    }
//////                    kXAxis.values = kXAxisValues
//////
//////                    var kChart = LineChartData(sortData(kXAxisValuesMillis, kValues_Line))
////////                    kXAxis.hasLines = true
//////                    kChart.axisXBottom = kXAxis
//////                    kXAxis.isAutoGenerated = true
//////                    (kChartViewID as LineChartView).lineChartData = kChart
//////                }
//////            }
//////        }
//////    }
//////
//////
//////    fun displayGraphDetailed(){
//////    }
//////
//////
//////    fun connectXDrip(url: String, context: Context) {
//////        var mRequestQueue: RequestQueue? = null
//////        var mStringRequest: StringRequest? = null
//////
//////        // Request XDrip connection and permissions
//////        //RequestQueue initialized
//////        mRequestQueue = Volley.newRequestQueue(context)
//////
//////
//////        //String Request initialized
//////        mStringRequest = StringRequest(
//////            Request.Method.GET, url,
//////            { response ->
//////                run {
//////                    getGlucoData(response)
//////                    // pushGlucoToGFit(response)
//////                }
//////            }) { error ->
//////            Log.i(TAG, "Unable to connect to XDrip")
//////        }
//////        mRequestQueue!!.add(mStringRequest)
//////    }
//////
//////    /* Gluco data parsing + formatting to display graph
//////     @param response: XDrip json response
//////    */
//////    fun getGlucoData(jsonstring: String){
//////        // set variables for display
//////        // val dataViewID = findViewById<TChart>(R.id.graph_steps)
//////
//////
//////        // parse gluco data
//////        val json = JSONArray(jsonstring)
//////        dataDetailedView.clear()
//////
//////        for (i in 0 until json.length()) {
//////            val measure = json.getJSONObject(i)
//////            val date = measure.getLong("date")
//////            val sgv = measure.getInt("sgv")
//////            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
//////        }
//////
//////        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
//////    }
//////
//////}
//
//
//
//
//
//package com.laulaid.laulaid_tchartskt
//
//// General
//
//
//// HTTP Request
//
//// charting
//
//// General
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import co.csadev.kellocharts.model.*
//import co.csadev.kellocharts.util.ChartUtils
//import co.csadev.kellocharts.view.AbstractChartView
//import co.csadev.kellocharts.view.ColumnChartView
//import co.csadev.kellocharts.view.LineChartView
//import com.android.volley.Request
//import com.android.volley.RequestQueue
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
//import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
//import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
//import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.fitness.Fitness
//import com.google.android.gms.fitness.FitnessOptions
//import com.google.android.gms.fitness.data.*
//import com.google.android.gms.fitness.data.DataType.TYPE_HEART_RATE_BPM
//import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
//import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
//import com.google.android.gms.fitness.request.DataReadRequest
//import com.google.android.gms.fitness.result.DataReadResponse
//import com.klim.tcharts.entities.ChartItem
//import org.json.JSONArray
//import java.util.concurrent.TimeUnit
//
//
//class DataHealth(string: String, context: Context) {
//
//    /** Companion object to access variables and function of the class outside
//     * @param fitnessOptions: authorization to all data types to retrieve from Google Fit
//     */
//    companion object {
//        var dataDetailedView = ArrayList<ChartItem>()
//        val fitnessOptions = FitnessOptions.builder()
//            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
//            .build()
//
//        /** Steps data parsing + formatting to display graph
//        @param InitData: Flag to reset data container
//        @param response: Google fit response
//        @param dataHealth: structure containing all class variables to display graphs
//         */
//        fun parseGFitData(response: DataReadResponse, dataHealth: DataHealth){
//            lateinit var Timestamp : String
//for (bucket in response.buckets) {
//
//                for (dataSet in bucket.dataSets) {
//                    // Steps
//                    if (dataSet.dataType==TYPE_STEP_COUNT_DELTA){
//                        for (dp in dataSet.dataPoints) {
//                            dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
//                            dataHealth.kValues_Column.add(Column(arrayListOf(SubcolumnValue(dp.getValue(Field.FIELD_STEPS).asInt().toFloat(),ChartUtils.pickColor()))))
//
//                        }
//                    }
//
//                    // Blood Pressure
//                    else if (dataSet.dataType == TYPE_BLOOD_PRESSURE) {
//                        var dia_temp = 0f
//                        var sys_temp = 0f
//
//                        for (dp in dataSet.dataPoints) {
//                            // Add all values for a given bucket of time (typically a day)
////                            dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
//                            dia_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat()
//                            sys_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat()
//
//
////
//                        }
//
//                        // Compute BP mean for a given bucket
//                        var dia_mean = dia_temp/dataSet.dataPoints.size
//                        var sys_mean = sys_temp/dataSet.dataPoints.size
//
//
//                        // Get bucket Start time
//                        dataHealth.kXAxisValuesMillis.add(bucket.getStartTime(TimeUnit.MILLISECONDS))
////                        dataHealth.kValues_PointValues.add(arrayListOf(
//////                            (PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), dia_mean)),
//////                            (PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), sys_mean))
//////
//////                            )
//
//                        // Blood Pressure
////                        else if (dataSet.dataType == TYPE_BLOOD_PRESSURE) {
////                            var dia_temp = 0f
////                            var sys_temp = 0f
////
////                            for (dp in dataSet.dataPoints) {
////                                // Add all values for a given bucket of time (typically a day)
//////                            dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                                dia_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat()
////                                sys_temp += dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat()
////
////
//////
////                            }
////
////                            // Compute BP mean for a given bucket
////                            var dia_mean = dia_temp/dataSet.dataPoints.size
////                            var sys_mean = sys_temp/dataSet.dataPoints.size
////
////
////                            // Get bucket Start time
////                            dataHealth.kXAxisValuesMillis.add(bucket.getStartTime(TimeUnit.MILLISECONDS))
////                            dataHealth.kValues_Line.add(
////                                Line(
////                                    arrayListOf(
////                                        PointValue(
////                                            bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),
////                                            dia_mean,
////                                            label = ""
////                                        ),
////                                        PointValue(
////                                            bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(),
////                                            sys_mean,
////                                            label = ""
////                                        )
////                                    )
////                                )
////                            )
//
////
////                        dataHealth.kValues_Line.add(
////                                Line(
////                                    arrayListOf(
////                                        PointValue(
////                                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                            dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC)
////                                                .asFloat(),
////                                            label = ""
////
////                                        ),
////
////                                        PointValue(
////                                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                            dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC)
////                                                .asFloat(),
////                                            label = ""
////
////                                        )
////                                    )
////                                )
////                            )
//
//
//
//
//                        dataHealth.kValues_Line[0].values.add(PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), dia_mean))
//                        dataHealth.kValues_Line[1].values.add(PointValue(bucket.getStartTime(TimeUnit.MILLISECONDS).toFloat(), sys_mean))
////
////                            for (dp in dataSet.dataPoints) {
////                            dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                            // Create entire list of point values
////                            dataHealth.kValues_PointValues.add(PointValue(
////                                dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                dp.getValue(Field.FIELD_BPM)
////                                    .asFloat(),
////                                ""
////                            ))
////                        }
//                    }
//
//                    // Heart Rate
////                    else if (dataSet.dataType == TYPE_HEART_RATE_BPM) {
////                        for (dp in dataSet.dataPoints) {
////                            dataHealth.kXAxisValuesMillis.add(dp.getTimestamp(TimeUnit.MILLISECONDS))
////                            dataHealth.kValues_Line[0].values.add(
////                                PointValue(
////                                            dp.getTimestamp(TimeUnit.MILLISECONDS).toFloat(),
////                                            dp.getValue(Field.FIELD_BPM)
////                                                .asFloat(),
////                                            ""
////                                        )
////                                    )
////
////                        }
////                    }
//                }
//
//}
//
//            dataHealth.displayGraphPreview_K()
//        }
//    }
//
//    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"
//
//    /** Class variables per catgeory: general, charting, data structure, GoogleFit variables,etc. )
//     */
//    // Data Initialization
//    var context = context
//
//    // Chart variables
//    lateinit var kChartViewID: AbstractChartView
//
//    // Data structure - AAChart
//    var datasize = 1
//
//    // Data Structure - KelloCharts
//    var kValues_Column = ArrayList<Column>()
//    var kValues_Line = ArrayList<Line>()
//    var kValues_PointValues = ArrayList<PointValue>()
//    var kValues_PointValuesLine = Line(kValues_PointValues)
////    val kValues_Line = ArrayList(kValues_PointValuesLine)
//
//    var kXAxisValues = ArrayList<AxisValue>()
//    var kXAxis = Axis()
//    var kXAxisValuesMillis = ArrayList<Long>()
//
//    //GFit variables
//    var duration: Int = 6
//    lateinit var gFitDataType: DataType
//    lateinit var gFitBucketTime: TimeUnit
//    lateinit var gFitStreamName: String
//
//    // Variables initialization for each data type:
//    init {
//
//        if (string === "Steps") {
//            // KelloChart
//            kChartViewID =(context as Activity).findViewById<ColumnChartView>(R.id.graph_main_steps)
//            kXAxis.name = string
//
//            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
//            gFitBucketTime = TimeUnit.DAYS
//            gFitStreamName = "estimated_steps"
//            kValues_Line = ArrayList<Line>()
//
//        }
//        if (string === "Blood Pressure") {
//            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_BP)
//            kXAxis.name = string
//
//            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE
//            gFitBucketTime = TimeUnit.DAYS
//            gFitStreamName = "Blood Pressure"
//            datasize = 2
//            for (i in 0 .. 1) {
//                kValues_Line.add(Line())
//            }
//        }
//        if (string === "Heart Rate") {
//            kChartViewID =(context as Activity).findViewById<LineChartView>(R.id.graph_main_HR)
//            kXAxis.name = string
//
//            gFitDataType = DataType.TYPE_HEART_RATE_BPM
//            gFitBucketTime = TimeUnit.DAYS
//            gFitStreamName = "Heart Rate"
//            kValues_Line = ArrayList<Line>()
//        }
//
//
//    }
//
//    /** GFit permissions verification and dispatch
//    @param context: App Context (typically main activity)
//     */
//    fun connectGFit(context: Activity) {
//        // If no permission -> request permission to the user
//        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context), fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                context, // your activity
//                1, // e.g. 1
//                getGoogleAccount(context),
//                fitnessOptions
//            )
//        }
//        // If permission -> get Data
//        else {
//            this.getGFitData(duration)
//        }
//    }
//
//    /** GFit connection request based on fitness option list
//    @param context: App Context (typically main activity)
//     */
//    private fun getGoogleAccount(context: Context) =
//        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
//
//    /** GFit connection to retrieve fit data
//    @param duration: duration to cover (default: last 7 days)
//     */
//    fun getGFitData(duration: Int) {
//        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
//
//        // Request for current (non completed) day / hour
//        var ReqCurrentTimes = DataReadRequest.Builder()
//            .read(gFitDataType)
//            .bucketByTime(1, gFitBucketTime)
//            .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//            .build()
//
//        // Default request using ".read" - For steps, we need to use ".aggregate"
//        // Request for past (completed) days / hours
//        var ReqCompletedTimes = DataReadRequest.Builder()
//            .read(gFitDataType)
//            .bucketByTime(1, gFitBucketTime)
//            .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//            .build()
//
//        if (kXAxis.name == "Steps") {
//            // Request for current (non completed) day / hour
//            ReqCurrentTimes = DataReadRequest.Builder()
//                .aggregate(gFitDataType)
//                .bucketByTime(1, gFitBucketTime)
//                .setTimeRange(Time_End, Time_Now, TimeUnit.MILLISECONDS)
//                .build()
//
//            // Request for past (completed) days / hours
//            ReqCompletedTimes = DataReadRequest.Builder()
//                .aggregate(gFitDataType)
//                .bucketByTime(1, gFitBucketTime)
//                .setTimeRange(Time_Start, Time_End, TimeUnit.MILLISECONDS)
//                .build()
//        }
//
//        // Clear Data before retrieving other data from GFit
//        kXAxisValuesMillis.clear()
//        kValues_Column.clear()
//
//        kValues_Line.forEach{it.values.clear()}
//
//        // History request and go to parse data
//        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
//            .readData(ReqCompletedTimes)
//            .addOnSuccessListener { response -> parseGFitData(response, this)}
//            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//
//
//        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, DataHealth.fitnessOptions))
//            .readData(ReqCurrentTimes)
//            .addOnSuccessListener { response -> parseGFitData(response, this)}
//            .addOnFailureListener { response -> Log.i(TAG, response.toString()) }
//    }
//
//    /** Data sorting based on object data index
//     @param valMilli: Values in milliseconds - Used to determine the order to sort the sort
//     @param valData: Values to sort based on valMilli order
//     */
//    fun <T>sortData (valMilli: ArrayList<Long>, valData: MutableList<T>):MutableList<T> {
//        val dateComparator = Comparator { col1: T, col2:T  ->
//            (valMilli[valData.indexOf(col1)] - valMilli[valData.indexOf(col2)]).toInt()
//        }
//
//        return valData.sortedWith(dateComparator) as MutableList<T>
//    }
//
//    /** Display preview graphs on main using KelloCharts Lib
//     */
//    fun displayGraphPreview_K() {
//        kXAxisValues.clear()
//
//        // Compute x-Axis values and sort all values by chronological order
//        if (kXAxisValuesMillis.size != 0) {
//            // Sort time in milliseconds
//            var kXAxisValuesMillisSorted = ArrayList<Long>(kXAxisValuesMillis)
//            kXAxisValuesMillisSorted.sort()
//
//            // Sort values to plot
//            var kXAxisValuesSorted = ArrayList<String>()
//            kXAxisValuesMillisSorted.forEach {
//                kXAxisValuesSorted.add(DataGeneral.getDate(it, "EEE").toString())
//            }
//
//            // Display Graph
//            if (kXAxisValuesSorted.size > 1){
//                // Format column charts
//                if (kChartViewID is ColumnChartView) {
//                    for (i in 0 until kXAxisValuesSorted.size) {
//                        kXAxisValues.add(AxisValue(i, kXAxisValuesSorted[i]))
//                    }
//                    kXAxis.values = kXAxisValues
//
//                    var kChart = ColumnChartData(sortData(kXAxisValuesMillis, kValues_Column), false, false)
//
//                    kChart.axisXBottom = kXAxis
//                    (kChartViewID as ColumnChartView).columnChartData = kChart
//                }
//
//                // Format Line charts
//                else if (kChartViewID is LineChartView) {
//                    for (i in 0 until kXAxisValuesSorted.size) {
//                        kXAxisValues.add(AxisValue(kXAxisValuesMillisSorted[i].toFloat(), kXAxisValuesSorted[i].toCharArray()))
//                    }
//                    kXAxis.values = kXAxisValues
//
//
//                    kValues_Line.forEach {
//                        it.values = sortData(kXAxisValuesMillis, it.values)
//                   }
////                    kXAxis.hasLines = true
//                    var kChart = LineChartData(kValues_Line)
//                    kChart.axisXBottom = kXAxis
//                    (kChartViewID as LineChartView).lineChartData = kChart
//                }
//            }
//        }
//    }
//
//
//    fun displayGraphDetailed(){
//    }
//
//
//    fun connectXDrip(url: String, context: Context) {
//        var mRequestQueue: RequestQueue? = null
//        var mStringRequest: StringRequest? = null
//
//        // Request XDrip connection and permissions
//        //RequestQueue initialized
//        mRequestQueue = Volley.newRequestQueue(context)
//
//
//        //String Request initialized
//        mStringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->
//                run {
//                    getGlucoData(response)
//                    // pushGlucoToGFit(response)
//                }
//            }) { error ->
//            Log.i(TAG, "Unable to connect to XDrip")
//        }
//        mRequestQueue!!.add(mStringRequest)
//    }
//
//    /* Gluco data parsing + formatting to display graph
//     @param response: XDrip json response
//    */
//    fun getGlucoData(jsonstring: String){
//        // set variables for display
//        // val dataViewID = findViewById<TChart>(R.id.graph_steps)
//
//
//        // parse gluco data
//        val json = JSONArray(jsonstring)
//        dataDetailedView.clear()
//
//        for (i in 0 until json.length()) {
//            val measure = json.getJSONObject(i)
//            val date = measure.getLong("date")
//            val sgv = measure.getInt("sgv")
//            dataDetailedView.add(ChartItem(date, arrayListOf(sgv)))
//        }
//
//        // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
//    }
//
//}