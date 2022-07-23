package com.laulaid.laulaid_tchartskt

import android.content.Context
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.view.LineChartView
import com.laulaid.laulaid_tchartskt.DataGeneral.Companion.getDate
import com.laulaid.laulaid_tchartskt.R.color.*


class DisplayData {

    companion object {


        /** Formatting for datapoint as lines - to be used with linechart.
         */
        fun formatAsLine(dH: DataHealth) {
            dH.kLine.clear()
            dH.kLineValues.clear()
            for (i in 0 until dH.dataPoint.size) {
                dH.kLineValues.add(PointValue(dH.dataPoint[i].dateMillis.toFloat(), dH.dataPoint[i].value[0], ""))

            }
            dH.kLine.add(Line(dH.kLineValues))




            displayMainGraph(dH)
        }
        /** Formatting for datapoint as columns - to be used with ColumnChart.
         */
        fun formatAsColumn(dH: DataHealth){
            dH.kLine.clear()

            for (i in 0 until dH.dataPoint.size) {
                dH.kLine.add(
                    Line(
                        arrayListOf(
                            PointValue(dH.dataPoint[i].dateMillis.toFloat(), dH.dataPoint[i].value[0], ""),
                            PointValue(dH.dataPoint[i].dateMillis.toFloat(), dH.dataPoint[i].value[1], dH.dataPoint[i].value[1].toString()),
                        )
                    )
                )
            }



            displayMainGraph(dH)
        }



        /** Display Main graphs using KelloCharts Lib
         */
        fun displayMainGraph(dH: DataHealth) {
            dH.kXAxisValues.clear()

            // Display Graph
            if (dH.dataPoint.size > 0 && dH.kChartView != null) {

                if (dH.kChartView is LineChartView) {

                    // Last value to fill textfield + Label update depending on the last value
                    dH.formatLabel()
                    dH.kYaxis = Axis(hasLines = true, maxLabels = 4)

                    // Create distinct Xaxis value based on the date
                    for (i in 0 until dH.dataPoint.size) {
                        dH.kDateEEE.add(getDate(dH.dataPoint[i].dateMillis.toLong(), "EEE"))
                    }
                    val kXAxisLabels = dH.kDateEEE.distinct()

                    // Get index of each distinct value in the list of string dates
                    var kXAxisIndex = ArrayList<Int>()
                    kXAxisLabels.forEach {
                        kXAxisIndex.add(dH.kDateEEE.indexOf(it))
                    }

                    // Create axis values
                    for (i in 0 until kXAxisIndex.size) {
                        dH.kXAxisValues.add(
                            AxisValue(
                                dH.kDateMillis[kXAxisIndex[i]].toFloat(),
                                kXAxisLabels[i].toCharArray()
                            )
                        )
                    }

                    // Add values for x Axis
                    dH.kXAxis.values = dH.kXAxisValues

                    // Lines formatting
                    dH.kLine.forEach {
                        it.hasLabelsOnlyForSelected = true
                        it.isFilled = true
                        it.hasPoints = false
                        it.strokeWidth = dH.kStrokeWidth
                        it.color = dH.mcolor_primary
                        it.pointRadius = 1
                        it.hasLabels = true

                    }

                    // Create a LineChartData using time and Line data
                    var kChart = LineChartData(dH.kLine)

                    // Add axis values and push it in the chart
                    kChart.axisXBottom = dH.kXAxis
                    kChart.axisYRight = dH.kYaxis

                    (dH.kChartView as LineChartView).lineChartData = kChart
                    val tempViewport = dH.kChartView?.maximumViewport.copy()
                    val tempPreViewport = tempViewport.copy()

                    // If in main activity, add an inset to have the entire labels for axis
                    if (dH.context::class == MainActivity::class) {
                        tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.05f)
                        dH.kChartView?.maximumViewport = tempViewport
                        dH.kChartView?.currentViewport = tempViewport
                    }

                    // If a preview view is available, displayPreviewChart(), i.e. we are not in the main activity view
                    if (dH.context::class != MainActivity::class) {
                        val dx = tempPreViewport.width() * 2f / 3f
                        tempPreViewport.offset(dx, 0f)
                        dH.displayPreviewGraph(kChart, tempPreViewport)
                    }
                }
            }
        }

    }
}