package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


/**
 * Adapter used to show a simple grid of products.
 */
class ModuleRecyclerViewAdapter(private val moduleList: List<Module>) : RecyclerView.Adapter<ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.main_module, parent, false)
        return ModuleViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        if (position < moduleList.size) {
            val module = moduleList[position]
            holder.moduleTitle.text = position.toString()
            module.dataHealth.bind(holder)
            module.dataHealth.connectGFit( module.dataHealth.context as Activity, false, 4)
        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }
}