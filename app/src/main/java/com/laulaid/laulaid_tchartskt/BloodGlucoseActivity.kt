package com.laulaid.laulaid_tchartskt

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartData
import com.klim.tcharts.entities.ChartItem

class BloodGlucoseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bloodglucose)

         val url = "http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10"

        displayChartWithPreview(DataHealth.dataDetailedView)

    }

    private fun displayChartWithPreview(data: ArrayList<ChartItem>) {
//        data:ArrayList<ChartItem>, dataViewID: TChart, dataTitle: String, keys:List<String>, names:List<String>, colors:ArrayList<Int>){
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
//        dataViewID
        // val data = DataHealth.connectXDrip("http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10", this )

        val dataTitle = "Blood Glucose"

        val keys = ArrayList<String>(1)
        keys.add("y")

        val names = ArrayList<String>(1)
        names.add("sgv")

        val colors = ArrayList<Int>(1)
        colors.add(Color.parseColor("#3DC23F"))

        val dataViewID = findViewById<TChart>(R.id.graph_gluco)
        dataViewID.setData(ChartData(keys, names, colors, data))
//    }

    }




    //    private fun pushGlucoToGFit(jsonstring: String): Task<Void> {
//
//        val gFitGlucodsource = DataSource.Builder()
//            .setAppPackageName(this)
//            .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
//            .setType(DataSource.TYPE_RAW)
//            .build()
//
//        // Create dataset
//        val gFitGlucodset = DataSet.builder(gFitGlucodsource)
//        val json = JSONArray(jsonstring)
//        val minDate = json.getJSONObject(json.length()-1).getLong("date")
//        val maxDate = json.getJSONObject(0).getLong("date")
//
//        for (i in 0 until json.length()){
//            val measure = json.getJSONObject(i)
//            val date = measure.getLong("date")
//            val sgv = measure.getInt("sgv")
//
//
//            // Add new datapoint to dataset
//            gFitGlucodset.add(DataPoint.builder(gFitGlucodsource)
//                .setTimestamp(date, TimeUnit.MILLISECONDS)
//                .setField(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL, sgv/18.0f)
//                .build()
//            )
//        }
//
//        // Request dataset update
//        val request = DataUpdateRequest.Builder()
//            .setDataSet(gFitGlucodset.build())
//            .setTimeInterval(minDate, maxDate, TimeUnit.MILLISECONDS)
//            .build()
//
//        return Fitness.getHistoryClient(this, getGoogleAccount())
//            .updateData(request)
//            .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "There was a problem updating the dataset.", e)
//            }
//    }
}