package com.example.thorweather.ui.favourite

import androidx.lifecycle.ViewModel
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsFragmentViewModel @Inject constructor(private val repo: RepositoryInterface) :
    ViewModel() {

    suspend fun insertFavLocation(lat: Double, lng: Double, currentCity: String,lang:String) =
        repo.insertFavouriteWeatherAPI(lat, lng, currentCity,lang)

}