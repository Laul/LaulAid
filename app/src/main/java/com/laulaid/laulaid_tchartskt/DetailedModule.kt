package com.laulaid.laulaid_tchartskt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


open class DetailedModule : AppCompatActivity() {
    var mType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailedmodule)
//        setSupportActionBar(findViewById(R.id.detailedmodule_toolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = mType


        var DataHealth = DataHealth(mType, this,R.id.detailedmodule_maingraph, R.id.detailedmodule_previewgraph, R.id.detailedmodule_value, R.id.detailedmodule_label,R.id.detailedmodule_date)
        DataHealth.connectGFit( DataHealth.context as Activity, false, 4)



    }


}