package com.example.thor.utils

import android.text.format.DateFormat
import java.util.*

object WeatherUtils {

    fun convertTimestampToDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd-MM-yyyy", calendar).toString()
        return "Updated at: $date"
    }


    fun getHourAndMinute(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return "${hour}:${minute}"
    }


    fun getTemp(temp: Double): String {
        return "$temp ℃"
    }


    fun getMinTemp(temp: Double): String {
        return "Min temp: $temp ℃"
    }


    fun getMaxTemp(temp: Double): String {
        return "Max temp: $temp ℃"
    }

    fun getWindSpeed(speed: String): String {
        return "$speed m/s"
    }

    fun getPressure(pressure: Int): String {
        return "$pressure hPa"
    }

    fun getHumidity(pressure: String): String {
        return "$pressure %"
    }
}