package com.laulaid.laulaid_tchartskt

import android.content.res.Resources
import co.csadev.kellocharts.view.LineChartView
import java.util.ArrayList

class Module(title: String) {

    val title = title
//        val value: String,
//        val date: String,
//        val label: String,
//        val chart : LineChartView,
////        val icon: Int?


    companion object {
        /**
         * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
         */
        fun initModuleList(): List<Module> {
            val list = ArrayList<Module>()

            list.add(Module("Case 1 "))
            list.add(Module("Case 2 "))

            return list
        }
    }
}