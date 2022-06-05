package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DetailedModule_Fragment(type: String, context: Context) : Fragment() {
    var mType = type
    var mcontext = context

    // inflate the layout
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.activity_detailedmodule_fragment, container, false)!!
    var DataHealth = DataHealth(mType, inflate as Context,R.id.detailedmodule_maingraph, R.id.detailedmodule_previewgraph, R.id.detailedmodule_value, R.id.detailedmodule_label,R.id.detailedmodule_date)
    DataHealth.connectGFit( DataHealth.context as Activity, false, 4)

return inflate
    }

}
