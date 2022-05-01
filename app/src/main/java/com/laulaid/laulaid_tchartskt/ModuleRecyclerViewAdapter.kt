package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
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
            val module = moduleList[position].dataHealth

            // Set Title text and color
            holder.moduleTitle.text = module.gFitStreamName
            holder.moduleTitle.setTextColor(module.color_primary)

            // Set Unit text and color
            holder.moduleUnit.text = module.kYaxis.name
            holder.moduleUnit.setTextColor(module.color_primary)

            // Set Button color
            holder.moduleBtn.setBackgroundColor(module.color_secondary)
//            holder.moduleBtn.foregroundTintList

            holder.moduleBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icn_bg, 0,0,0)




                module.bind(holder)
            module.connectGFit( module.context as Activity, false, 4)


        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

}