package com.laulaid.laulaid_tchartskt

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartData
import com.klim.tcharts.entities.ChartItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chart = this@MainActivity.findViewById<TChart>(R.id.tchart)
        chart.setData(DrawChart())
    }


    private fun DrawChart(): ChartData{

        val keys = ArrayList<String>(1)
        keys.add("y")

        val names = ArrayList<String>(1)
        names.add("sgv")

        val colors = ArrayList<Int>(1)
        colors.add(Color.parseColor("#3DC23F"))

        val data: ArrayList<ChartItem> = ArrayList<ChartItem>()
        data.add(ChartItem(154654352431,arrayListOf<Int>(1)))
        data.add(ChartItem(1546543554357,arrayListOf<Int>(18)))
        data.add(ChartItem(36870845544444,arrayListOf<Int>(11)))
        data.add(ChartItem(45554444454054,arrayListOf<Int>(53)))


        return ChartData(keys, names, colors, data)

    }

}