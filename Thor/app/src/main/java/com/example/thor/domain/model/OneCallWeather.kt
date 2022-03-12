package com.example.thor.domain.model

import com.example.thor.data.remote.dto.*

data class OneCallWeather(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
)
