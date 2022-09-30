package com.laulaid.laulaid_tchartskt

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.csadev.kellocharts.view.AbstractChartView

class Detailed_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var chart_week: AbstractChartView = itemView.findViewById(R.id.detailedmodule_Graph_Week)
    var chart_day: AbstractChartView = itemView.findViewById(R.id.detailedmodule_Graph_Day)
    var chart_previewweek: AbstractChartView = itemView.findViewById(R.id.detailedmodule_Graph_PreviewWeek)
    var moduleLabel: TextView = itemView.findViewById(R.id.detailedmodule_label)
    var moduleDate: TextView = itemView.findViewById(R.id.detailedmodule_date)
    var moduleValue: TextView = itemView.findViewById(R.id.detailedmodule_value)

}