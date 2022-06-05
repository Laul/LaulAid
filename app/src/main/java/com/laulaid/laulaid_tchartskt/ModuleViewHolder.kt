package com.laulaid.laulaid_tchartskt

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.csadev.kellocharts.view.AbstractChartView

class ModuleViewHolder(itemView: View) //TODO: Find and store views from itemView
    : RecyclerView.ViewHolder(itemView){
    var moduleBtn: Button = itemView.findViewById(R.id.module_btn)
    var moduleTitle: TextView = itemView.findViewById(R.id.module_title)
    var moduleValue: TextView = itemView.findViewById(R.id.module_value)
    var moduleUnit: TextView = itemView.findViewById(R.id.module_unit)
    var moduleDate: TextView = itemView.findViewById(R.id.module_date)
    var moduleLabel: TextView = itemView.findViewById(R.id.module_label)
    var moduleChart: AbstractChartView = itemView.findViewById(R.id.module_chart)

}