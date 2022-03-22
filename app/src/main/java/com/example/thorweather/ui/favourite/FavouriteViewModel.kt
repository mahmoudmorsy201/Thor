package com.example.thorweather.ui.favourite

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.thorweather.R
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {

    fun addFavouritePlace(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_navigation_favourite_to_navigation_map)

    }

    suspend fun makeRequest(lat: Double, lon: Double, currentCity: String,lang:String) =
        repository.updateFavouriteWeatherAPI(lat, lon, currentCity,lang)


    fun getFavouriteListFromRoom(isFavourite: Boolean) =
        repository.getFavouriteWeatherList(isFavourite)

    fun deleteWeatherObjectByTimeZone(timezone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteWeatherByTimeZone(timezone)
        }
    }

    val _detailsSelectedItem = MutableLiveData<WeatherResponseDto>()

    val detailsSelectedItem: LiveData<WeatherResponseDto> = _detailsSelectedItem

    fun setDetailsForItemSelected(weatherResponseDto: WeatherResponseDto) {
        _detailsSelectedItem.postValue(weatherResponseDto)
    }

}