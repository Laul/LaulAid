package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Intent
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
            val module = moduleList[position].dataHealth

            // Set Title text and color
            holder.moduleTitle.text = module.mname
            holder.moduleTitle.setTextColor(module.mcolor_primary)

            // Set Unit text and color
            holder.moduleUnit.text = module.kYaxis.name
            holder.moduleUnit.setTextColor(module.mcolor_primary)

            // Set Button color
            holder.moduleBtn.setBackgroundColor(module.mcolor_secondary)
            holder.moduleBtn.setCompoundDrawablesWithIntrinsicBounds(module.micon, 0,0,0)

            module.bind(holder)
            module.connectGFit( module.context as Activity, false, 4)

        // Start BG activity
        holder.moduleBtn!!.setOnClickListener {
            val intent = Intent(holder.moduleBtn.context, Module_BG::class.java)
            holder.moduleBtn.context.startActivity(intent)
        }

        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

}