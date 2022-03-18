package com.laulaid.laulaid_tchartskt

import androidx.appcompat.app.AppCompatActivity
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class DisplayGraphs : AppCompatActivity() {

    private fun displayGraph_preview(
        data: ArrayList<Int>,
        dataType: AAChartType,
        dataViewID: AAChartView
    ) {

        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(dataType)
            .title("title")
            .subtitle("subtitle")
            .backgroundColor("#4b2b7f")
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Steps")
                        .data(data.toArray())

                )
            )
        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        dataViewID.aa_drawChartWithChartModel(aaChartModel)
    }
}