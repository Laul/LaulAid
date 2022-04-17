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

        var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.bgView_MainGraph,  R.id.bgView_PreviewGraph , R.id.bgView_Value,R.id.bgView_Label)
        DataHealth_BG.connectGFit( this, false, 15)



        //
        btnRequest = findViewById(R.id.bgView_Back)
        btnRequest!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Push data to GFit
//        btnRequest = findViewById(R.id.bSync)
//        btnRequest!!.setOnClickListener {
//            var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.graph_BG_Main,  R.id.graph_BG_Preview , R.id.bg_value)
//            DataHealth_BG.connectXDrip(this, true ,1000)
//        }

    }




}