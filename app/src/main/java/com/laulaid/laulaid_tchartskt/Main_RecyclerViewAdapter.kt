package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class Main_RecyclerViewAdapter(private val mainModuleList: List<Main_Module>) : RecyclerView.Adapter<Main_ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Main_ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.module_main, parent, false)
        return Main_ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: Main_ViewHolder, position: Int) {
        if (position < mainModuleList.size) {
            val module = mainModuleList[position].dataHealth

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
            module.connectGFit( module.context as Activity, false, 5)

            // Start BG activity
            if (module.mname == "Blood Glucose"){
                holder.moduleBtn!!.setOnClickListener {
                    val intent = Intent(holder.moduleBtn.context, Activity_BG::class.java)
                    holder.moduleBtn.context.startActivity(intent)
                }
            }
            if (module.mname == "Steps"){
                holder.moduleBtn!!.setOnClickListener {
                    val intent = Intent(holder.moduleBtn.context, Activity_Steps::class.java)
                    holder.moduleBtn.context.startActivity(intent)
                }
            }
            if (module.mname == "Heart Rate"){
                holder.moduleBtn!!.setOnClickListener {
                    val intent = Intent(holder.moduleBtn.context, Activity_HR::class.java)
                    holder.moduleBtn.context.startActivity(intent)
                }
            }

//
//            // Start HR activity
//            holder.moduleBtn!!.setOnClickListener {
//                val intent = Intent(holder.moduleBtn.context, Activity_HR::class.java)
//                holder.moduleBtn.context.startActivity(intent)
//            }

        }
    }

    override fun getItemCount(): Int {
        return mainModuleList.size
    }

}