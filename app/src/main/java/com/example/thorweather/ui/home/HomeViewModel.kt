package com.example.thorweather.ui.home

import androidx.lifecycle.ViewModel
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {

     fun getStoredWeatherResponse() : Flow<WeatherResponseDto>{
       return repository.getStoredWeather()
    }

    suspend fun makeRequest(lat: Double, lon: Double, currentCity: String,lang:String) =
        repository.getCurrentWeatherAPI(lat, lon,currentCity,lang)


}

