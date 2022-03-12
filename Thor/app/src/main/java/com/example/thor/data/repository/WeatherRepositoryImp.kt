package com.example.thor.data.repository

import com.example.thor.data.remote.OpenWeatherMapApi
import com.example.thor.data.remote.dto.OneCallWeatherDto
import com.example.thor.domain.repository.WeatherRepositoryInterface
import javax.inject.Inject

class WeatherRepositoryImp @Inject constructor(
    private val api: OpenWeatherMapApi
) : WeatherRepositoryInterface {
    override suspend fun getOneCallWeather(
        lat: String,
        lon: String,
        language: String,
        units: String,
        apiKey: String
    ): OneCallWeatherDto {

        return api.getOneCallWeather(
            lat, lon, language, units, apiKey
        )
    }


}