package com.example.thor.data.remote

import com.example.thor.data.remote.dto.OneCallWeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

    @GET("onecall?")
    suspend fun getOneCallWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("API key") apiKey: String
    ) : OneCallWeatherDto

}