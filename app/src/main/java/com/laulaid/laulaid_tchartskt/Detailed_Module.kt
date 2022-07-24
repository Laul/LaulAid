package com.laulaid.laulaid_tchartskt

import android.content.Context
import java.util.ArrayList

class Detailed_Module(dataHealth: DataHealth ) {
    val dataHealth = dataHealth

    companion object {

        /**
         * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
         */
        fun initModuleList(context: Context, mType: String): List<Detailed_Module> {
            val list = ArrayList<Detailed_Module>()

            var DataHealth_BG = DataHealth(mType,  context, -1, -1, -1,-1, -1, -1)

            list.add(Detailed_Module(DataHealth_BG))

            return list
        }
    }
}