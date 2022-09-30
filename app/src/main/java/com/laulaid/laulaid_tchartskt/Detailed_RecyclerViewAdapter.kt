package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class Detailed_RecyclerViewAdapter(private val moduleList: List<Detailed_Module>) : RecyclerView.Adapter<Detailed_ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Detailed_ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.module_detailed, parent, false)
        return Detailed_ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: Detailed_ViewHolder, position: Int) {
        if (position < moduleList.size) {
            val module = moduleList[position].dataHealth

            module.bind(holder)
            module.connectGFit( module.context as Activity, false, 5)

        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

}