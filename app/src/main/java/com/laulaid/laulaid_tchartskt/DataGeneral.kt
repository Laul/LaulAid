package com.laulaid.laulaid_tchartskt

import java.text.SimpleDateFormat
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


        /**
         * Return date in specified format.
         * @param milliSeconds Date in milliseconds
         * @param dateFormat Date format
         * @return String representing date in specified format
         */
        fun getDate(milliSeconds: Long, dateFormat: String?): String? {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }


}