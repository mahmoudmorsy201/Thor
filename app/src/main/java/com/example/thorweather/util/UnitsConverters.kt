package com.example.thorweather.util

import android.content.Context
import kotlin.math.roundToInt

class UnitsConverters(var context: Context) {


    fun convertToArabic(value: String): String? {
        return (value + "")
            .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
            .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
            .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
            .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
            .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
    }




    fun returnTemperatureUsingUserFormat(value: String): String? {
        var result = value
        val sharedPreferencesFactory = SharedPreferencesFactory(context)
        val units = sharedPreferencesFactory.getUnitsOfTemperature()
        val lang = sharedPreferencesFactory.getLanguage().toString()



        when {
            units.equals("K") -> {
                result = "$value \u212A"
            }
            units.equals("C") -> {
                val celsius: Float = value.toFloat() - 273.15f
                result = "${celsius.roundToInt()} \u2103"
            }
            units.equals("F") -> {
                val fah = 1.8 * (value.toFloat() - 273) + 32
                result = "${fah.roundToInt()} \u2109"
            }
        }
        if(lang == "ar") {
            return convertToArabic(result)
        }
        return result
    }

    fun returnWindSpeedUsingUserFormat(value: String): String {
        var result = value
        val sharedPreferencesFactory = SharedPreferencesFactory(context)
        val units = sharedPreferencesFactory.getUnitsOfWindSpeed()
        if (units.equals("meter/sec")) {
            result = "$value m/s"
        } else if (units.equals("miles/hour")) {
            val mph = value.toFloat().roundToInt() * 2.23694
            result = "${mph.roundToInt()} mi/h"
        }
        return result
    }

}