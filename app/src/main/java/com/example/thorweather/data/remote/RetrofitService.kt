package com.example.thorweather.data.remote

import com.example.thorweather.data.model.WeatherResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "61bd1e865e5ded45f69f0e78e09c9f5d"

interface RetrofitService {
    @GET("/data/2.5/onecall?")
    suspend fun getAllStatusWeatherByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String = "standard",
        @Query("lang") lang:String="en",
        @Query("exclude") exclulde: String = "minutely",
        @Query("appid") appid: String = API_KEY
    ): Response<WeatherResponseDto>
}
