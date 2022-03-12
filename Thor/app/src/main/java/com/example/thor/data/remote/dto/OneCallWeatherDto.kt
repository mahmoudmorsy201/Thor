package com.example.thor.data.remote.dto

import com.example.thor.domain.model.OneCallWeather

data class OneCallWeatherDto(
    val alerts: List<Alert>,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)

fun OneCallWeatherDto.toOneCallWeather(): OneCallWeather {
    return OneCallWeather(
        current = current,
        daily = daily,
        hourly = hourly,
        lat = lat,
        lon = lon,
        timezone = timezone
    )
}