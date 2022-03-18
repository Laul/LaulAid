package com.laulaid.laulaid_tchartskt

// General
import android.app.Activity
import android.widget.Toast
import android.graphics.Color
import android.util.Log
import android.content.Context


// HTTP Request
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

// charting
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartItem

// General
import com.google.android.gms.tasks.Task



class HealthData {
    companion object {
        fun connectXDrip(url: String, context: Context){
            var mRequestQueue: RequestQueue? = null
            var mStringRequest: StringRequest? = null

            // Request XDrip connection and permissions
            //RequestQueue initialized
            mRequestQueue = Volley.newRequestQueue(context)


            //String Request initialized
            mStringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->run{
                    getGlucoData(response)
                    // pushGlucoToGFit(response)

                }
                }) { error ->
                    Log.i(TAG, "Unable to connect to XDrip")
            }
            mRequestQueue!!.add(mStringRequest)

        }


        /* Gluco data parsing + formatting to display graph
         @param response: XDrip json response
        */
        fun getGlucoData(jsonstring: String):ArrayList<ChartItem>{
            // set variables for display
            // val dataViewID = findViewById<TChart>(R.id.graph_steps)


            // parse gluco data
            val json = JSONArray(jsonstring)
            val data = ArrayList<ChartItem>()
            for (i in 0 until json.length()) {
                val measure = json.getJSONObject(i)
                val date = measure.getLong("date")
                val sgv = measure.getInt("sgv")
                data.add(ChartItem(date, arrayListOf(sgv)))
            }
            return data

            // displayGraph_advanced(data, dataViewID, dataTitle, keys, names, colors)
        }


    }
}