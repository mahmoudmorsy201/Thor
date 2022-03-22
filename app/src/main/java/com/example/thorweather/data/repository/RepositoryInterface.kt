package com.example.thorweather.data.repository

import com.example.thorweather.data.model.AlertModel
import com.example.thorweather.data.model.WeatherResponseDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface RepositoryInterface {

    suspend fun getCurrentWeatherAPI(
        lat: Double,
        lon: Double,
        currentCity: String,
        lang: String
    ): Response<WeatherResponseDto>

    fun getStoredWeather(): Flow<WeatherResponseDto>
    fun getWeatherForAlert(): WeatherResponseDto
    fun getFavouriteWeatherList(isFavourite: Boolean): Flow<List<WeatherResponseDto>>
    suspend fun insertWeather(weatherResponseDto: WeatherResponseDto)
    suspend fun deleteWeatherByTimeZone(timeZone: String)
    suspend fun insertFavouriteWeatherAPI(
        lat: Double,
        lon: Double,
        currentCity: String,
        lang: String
    )

    suspend fun getCurrentWeatherAPIForWidget(
        lat: Double,
        lon: Double,
        lang: String
    ): Response<WeatherResponseDto>


    suspend fun updateFavouriteWeatherAPI(
        lat: Double,
        lon: Double,
        currentCity: String,
        lang: String
    ): Response<WeatherResponseDto>

    suspend fun insertAlert(alert: AlertModel) : Long
    suspend fun deleteAlert(alert: AlertModel)
    fun getAlerts(): Flow<List<AlertModel>>
}