package com.laulaid.laulaid_tchartskt

import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.view.LineChartView
import com.laulaid.laulaid_tchartskt.DataGeneral.Companion.getDate
import kotlin.collections.ArrayList

class DisplayData {

    companion object {


        /** Formatting for datapoint as lines - to be used with linechart.
         */
        fun formatAsLine(dH: DataHealth) {
            dH.kLine.clear()
            dH.kLineValues.clear()
            for (i in 0 until dH.dataPoint.size) {
                dH.kLineValues.add(PointValue(dH.dataPoint[i].dateMillis_point.toFloat(), dH.dataPoint[i].value[0], ""))

            }
            dH.kLine.add(Line(dH.kLineValues))
            displayCharts(dH, false)

        }

        /** Formatting for datapoint as columns - to display aggregate data per day for the week chart`
         */
        fun formatAsColumn(dH: DataHealth) {

            dH.kLine.clear()


            // Group data per Day

            var currentDayMilli = dH.dataPoint[0].dateMillis_bucket
            var currentDay = getDate(dH.dataPoint[0].dateMillis_bucket, "EEE")

            var tempVal = arrayListOf(dH.dataPoint[0].value)

            for (i in 1 until dH.dataPoint.size) {
                var tempDate = getDate(dH.dataPoint[i].dateMillis_bucket, "EEE")
                if (currentDay != tempDate) {

                    // if steps: we aggregate data for a day
                    if (dH.mname == "Steps") {
                        computeStepsData(dH, tempVal, currentDayMilli)
                    }

                    // else: calculate the mean, max, and min per day
                    else {
                        computeOtherData(dH, tempVal, currentDayMilli)
                    }
                    currentDay = tempDate
                    currentDayMilli = dH.dataPoint[i].dateMillis_bucket
                    tempVal = arrayListOf(dH.dataPoint[i].value)
                } else {
                    tempVal.add(dH.dataPoint[i].value)
                }
            }

            if (dH.mname == "Steps") {
                computeStepsData(dH, tempVal, currentDayMilli)
            } else {
                computeOtherData(dH, tempVal, currentDayMilli)
            }

            // Main Activity: display as week only
            // Detailed Activity: display as week for the top graph
            displayCharts(dH, true)
        }

        /** Create lines to display steps. Must be the total of steps per day
         */
        fun computeStepsData(dH: DataHealth, tempVal: ArrayList<ArrayList<Float>>, currentDayMilli: Long) {
            var tempValDay = 0f
            for (j in 0 until tempVal.size) {
                tempValDay += tempVal[j][0]
            }

            dH.kLine.add(
                Line(
                    arrayListOf(
                        PointValue(currentDayMilli.toFloat(), 0f, ""),
                        PointValue(currentDayMilli.toFloat(), tempValDay, tempValDay.toString()),
                    )
                )
            )
        }

        /** Create lines to display everything except steps.
         * 2 cases:
         *      - Blood Glucose + Heart Rate : min, max,mean - we display min and max
         *      - Blood Pressure : display mean of diastol + systol
         */
        fun computeOtherData(dH: DataHealth, tempVal: ArrayList<ArrayList<Float>>, currentDayMilli: Long) {
            var tempValMean = ArrayList<Float>()
            var tempValMin = ArrayList<Float>()
            var tempValMax = ArrayList<Float>()
            for (k in 0 until tempVal[0].size) {
                tempValMean.add(0f)
                tempValMin.add(10000f)
                tempValMax.add(0f)

                var sizeDay = 0

                for (j in 0 until tempVal.size) {
                    tempValMean[k] += tempVal[j][k]
                    if (tempValMin[k] > tempVal[j][k]) {
                        tempValMin[k] = tempVal[j][k]
                    }
                    if (tempValMax[k] < tempVal[j][k]) {
                        tempValMax[k] = tempVal[j][k]
                    }
                    sizeDay += 1
                }

                tempValMean[k] = tempValMean[k] / sizeDay
            }
            // Create a line for a given day
            if (dH.mname == "Blood Glucose" || dH.mname == "Heart Rate") {
                dH.kLine.add(
                    Line(
                        arrayListOf(
                            PointValue(currentDayMilli.toFloat(), tempValMin[0], tempValMin[0].toString()),
                            PointValue(currentDayMilli.toFloat(), tempValMax[0], tempValMax[0].toString()),
                        )
                    )
                )
            }
            if (dH.mname == "Blood Pressure") {
                dH.kLine.add(
                    Line(
                        arrayListOf(
                            PointValue(currentDayMilli.toFloat(), tempValMean[0], tempValMean[0].toString()),
                            PointValue(currentDayMilli.toFloat(), tempValMean[1], tempValMean[1].toString()),
                        )
                    )
                )
            }

        }


        /** Display Main graphs using KelloCharts Lib
         */
        fun displayCharts(dH: DataHealth, isWeek: Boolean) {
            var dataPointCopy = ArrayList<LDataPoint>(dH.dataPoint)

            dH.kXAxisValues.clear()

            // Display Graph
            if (dataPointCopy.size > 0 && dH.kChartView_Week != null) {


                // Last value to fill textfield + Label update depending on the last value
                dH.formatLabel()
                dH.kYaxis = Axis(hasLines = true, maxLabels = 4)
                dH.kDateEEE.clear()

                // Create distinct Xaxis value based on the date
                for (i in 0 until dataPointCopy.size) {
                    dH.kDateEEE.add(getDate(dataPointCopy[i].dateMillis_bucket.toLong(), "EEE"))
                }
                val kXAxisLabels = dH.kDateEEE.distinct()

                // Get index of each distinct value in the list of string dates
                var kXAxisIndex = ArrayList<Int>()
                if (kXAxisLabels.size > 1) {
                    kXAxisLabels.forEach {
                        kXAxisIndex.add(dH.kDateEEE.indexOf(it))
                    }
                }

//                // Create axis values
                    for (i in 0 until kXAxisIndex.size) {
                        dH.kXAxisValues.add(
                            AxisValue(
                                dataPointCopy[kXAxisIndex[i]].dateMillis_bucket.toFloat(),
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
                    it.hasPoints = true
                    it.strokeWidth = dH.kStrokeWidth
                    it.color = dH.mcolor_primary
                    it.pointRadius = 1
                    it.hasLabels = false
                }

                // Create a LineChartData using time and Line data
                var kChart_Data = LineChartData(ArrayList<Line>(dH.kLine))

                // Add axis values and push it in the chart
                kChart_Data.axisXBottom = dH.kXAxis
                kChart_Data.axisYRight = dH.kYaxis


                if (isWeek){
                    (dH.kChartView_Week as LineChartView).lineChartData = kChart_Data
                }
                else{
                    (dH.kChartView_Day as LineChartView).lineChartData = kChart_Data
                }
                val tempViewport = dH.kChartView_Week?.maximumViewport.copy()
                val tempPreViewport = tempViewport.copy()

                // If in main activity, add an inset to have the entire labels for axis
                    tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.05f)
                    dH.kChartView_Week?.maximumViewport = tempViewport
                    dH.kChartView_Week?.currentViewport = tempViewport

//                 If a preview view is available, displayPreviewChart(), i.e. we are not in the main activity view
                if (!isWeek){
                    val dx = tempPreViewport.width() * 2f / 3f
                    tempPreViewport.offset(dx, 0f)
                    dH.displayPreviewGraph(kChart_Data, tempPreViewport)
                }
            }
        }

    }
}