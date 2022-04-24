package com.laulaid.laulaid_tchartskt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.csadev.kellocharts.view.LineChartView

class SandboxActivity : AppCompatActivity() {

    private var btnRequest: Button? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
        setSupportActionBar(findViewById(R.id.bgView_Toolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.bgView_MainGraph,  R.id.bgView_PreviewGraph , R.id.bgView_Value,R.id.bgView_Label, R.id.bg_date)
        DataHealth_BG.connectGFit( this, false, 15)


    }


}