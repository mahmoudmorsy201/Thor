package com.example.thor.domain.repository

import com.example.thor.data.remote.dto.OneCallWeatherDto
import retrofit2.http.Query

interface WeatherRepositoryInterface {

    suspend fun getOneCallWeather(
        lat: String,
        lon: String,
        language: String,
        units: String,
        apiKey: String
    ) : OneCallWeatherDto
}