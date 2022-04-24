package com.laulaid.laulaid_tchartskt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


/**
 * Adapter used to show a simple grid of products.
 */
class ModuleRecyclerViewAdapter(private val productList: List<Module>) : RecyclerView.Adapter<ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.main_module, parent, false)
        return ModuleViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        // TODO: Put ViewHolder binding code here in MDC-102
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}