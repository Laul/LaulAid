package com.laulaid.laulaid_tchartskt

import android.util.Log
import java.util.*

class DataGeneral {

    companion object {
        fun getTimes(duration: Int): List<Long>{
            val cal = GregorianCalendar()
            val TimeNowInMilli = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 0) //anything 0 - 23
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val TimeEndInMilli =  cal.timeInMillis
            val TimeStartInMilli = TimeEndInMilli - ((duration+1)*24*60*60*1000)
            return listOf(TimeNowInMilli, TimeStartInMilli, TimeEndInMilli)
        }


    }


}