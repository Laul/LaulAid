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




        //
        btnRequest = findViewById(R.id.bSearch)
        btnRequest!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Push data to GFit
        btnRequest = findViewById(R.id.bSync)
        btnRequest!!.setOnClickListener {
            var DataHealth_BG = DataHealth("Blood Glucose", this, R.id.graph_BG_MainView, 14 )
            DataHealth_BG.connectXDrip(this, true ,1000)
        }

    }




}