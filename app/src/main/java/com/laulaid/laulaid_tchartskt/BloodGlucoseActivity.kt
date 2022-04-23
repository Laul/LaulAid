package com.laulaid.laulaid_tchartskt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button


class BloodGlucoseActivity : AppCompatActivity() {

    private var btnRequest: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bloodglucose)
        setSupportActionBar(findViewById(R.id.bgView_Toolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.bgView_MainGraph,  R.id.bgView_PreviewGraph , R.id.bgView_Value,R.id.bgView_Label, R.id.bg_date)
        DataHealth_BG.connectGFit( this, false, 15)


    }




}