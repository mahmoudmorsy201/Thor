package com.example.thorweather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thorweather.data.model.WeatherResponseDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM ThorWeather WHERE isFavourite=:isFavourite")
    fun getCurrentWeatherResponse(isFavourite: Boolean = false): Flow<WeatherResponseDto>

    @Query("SELECT * FROM ThorWeather  WHERE isFavourite=:isFavourite")
    fun getWeatherResponseAlert(isFavourite: Boolean = false): WeatherResponseDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherDto: WeatherResponseDto)

    @Query("SELECT * FROM ThorWeather WHERE isFavourite=:isFavourite")
    fun getFavourite(isFavourite: Boolean = true): Flow<List<WeatherResponseDto>>

    @Query("DELETE FROM ThorWeather WHERE timezone = :timezone")
    suspend fun deleteWeatherByTimeZone(timezone: String)
}