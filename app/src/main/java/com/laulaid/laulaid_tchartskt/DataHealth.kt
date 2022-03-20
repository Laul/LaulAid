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
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataSource.TYPE_DERIVED
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import org.json.JSONArray

// charting
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartItem

// General
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit


class DataHealth(string: String, context: Context) {
    private val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"

    var context = context
    var chartTitle = string
    lateinit var aaChartViewID: AAChartView
    lateinit var aaChartType: AAChartType
    var duration: Int= 7
    lateinit var gFitDataType: DataType
    var gFitDataSource: Int=0
    lateinit var gFitBucketTime: TimeUnit
    lateinit var gFitStreamName: String

    init {
        if (string === "Steps"){
            aaChartViewID = (context as Activity).findViewById<AAChartView>(R.id.graph_preview_steps)
            aaChartType = AAChartType.Column
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
            gFitDataSource = DataSource.TYPE_DERIVED
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "estimated_steps"
        }
        if (string === "Blood Pressure"){
            aaChartViewID = (context as Activity).findViewById<AAChartView>(R.id.graph_preview_bloodpressure)
            aaChartType = AAChartType.Spline
            gFitDataType = HealthDataTypes.TYPE_BLOOD_GLUCOSE
            gFitDataSource = DataSource.TYPE_RAW
            gFitBucketTime = TimeUnit.DAYS
            gFitStreamName = "Blood Pressure"
        }



    }
    companion object {
        var dataDetailedView = ArrayList<ChartItem>()
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
            .build()


//        private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, MainActivity.fitnessOptions)
//
//        fun connectGFit(context: Activity){
//            if (!GoogleSignIn.hasPermissions(getGoogleAccount(), MainActivity.fitnessOptions)) {
//                GoogleSignIn.requestPermissions(
//                    context, // your activity
//                    1, // e.g. 1
//                    getGoogleAccount(),
//                    MainActivity.fitnessOptions)
//            } else {
//            Log.i(TAG, "ehgrfvg")
//            }
//

//        }
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
}