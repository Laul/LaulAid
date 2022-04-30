package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import co.csadev.kellocharts.view.LineChartView
import java.util.ArrayList
import com.laulaid.laulaid_tchartskt.DataHealth

class Module(dataHealth: DataHealth ) {
    val dataHealth = dataHealth
//    val title = dataHealth.gFitStreamName
//    val value = dataHealth.dataValue
//    val date = date
//    val label= label
//        val chart : LineChartView,
////        val icon: Int?


    companion object {

        /**
         * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
         */
        fun initModuleList(context: Context): List<Module> {
            val list = ArrayList<Module>()
            var DataHealth_BG = DataHealth("Blood Glucose", context, -1, -1, -1, -1, -1)
//            var DataHealth_Steps = DataHealth("Steps", context, -1, -1, -1, -1, -1)
            var DataHealth_HR = DataHealth("Heart Rate", context, -1, -1, -1, -1, -1)
//            var DataHealth_BG = DataHealth("Blood Pressure", context, -1, -1, -1, -1, -1)


            list.add(Module(DataHealth_BG))
//            list.add(Module(DataHealth_BG))
//            list.add(Module(DataHealth_BG))
//            list.add(Module(DataHealth_BG))
//            list.add(Module(DataHealth_Steps))
            list.add(Module(DataHealth_HR))

            return list
        }
    }
}