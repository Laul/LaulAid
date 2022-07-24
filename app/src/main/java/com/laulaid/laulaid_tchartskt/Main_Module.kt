package com.laulaid.laulaid_tchartskt

import android.content.Context
import java.util.ArrayList

class Main_Module(dataHealth: DataHealth ) {
    val dataHealth = dataHealth

    companion object {

        /**
         * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
         */
        fun initModuleList(context: Context): List<Main_Module> {
            val list = ArrayList<Main_Module>()
            var DataHealth_BG = DataHealth("Blood Glucose", context, -1, -1, -1,-1, -1, -1)
            var DataHealth_Steps = DataHealth("Steps", context, -1, -1, -1,-1, -1, -1)
            var DataHealth_HR = DataHealth("Heart Rate", context, -1, -1, -1,-1, -1, -1)
            var DataHealth_BP = DataHealth("Blood Pressure", context, -1, -1,-1, -1, -1, -1)

//
            list.add(Main_Module(DataHealth_BG))
            list.add(Main_Module(DataHealth_Steps))
            list.add(Main_Module(DataHealth_HR))
            list.add(Main_Module(DataHealth_BP))

            return list
        }
    }
}