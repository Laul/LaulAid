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

        displayChartWithPreview(HealthData.data)

    }

    private fun displayChartWithPreview(data: ArrayList<ChartItem>) {
//        data:ArrayList<ChartItem>, dataViewID: TChart, dataTitle: String, keys:List<String>, names:List<String>, colors:ArrayList<Int>){
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
//        dataViewID
        // val data = HealthData.connectXDrip("http://192.168.1.135:17580/api/v1/entries/sgv.json?count=10", this )

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
}