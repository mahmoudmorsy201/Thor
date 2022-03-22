package com.example.thorweather.ui.favourite

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import com.example.thorweather.R
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.databinding.FragmentHomeBinding
import com.example.thorweather.ui.home.DailyAdapter
import com.example.thorweather.ui.home.HourlyAdapter
import com.example.thorweather.util.ImageHelper
import com.example.thorweather.util.UnitsConverters
import com.example.thorweather.util.WeatherUtil
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class FavouriteDetailsFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: FavouriteViewModel by viewModels()
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    private var imageHelper = ImageHelper()
    lateinit var lang: String
    private val binding get() = _binding!!
    lateinit var weatherResponseDto: WeatherResponseDto
    lateinit var receivedWeatherDataDto: WeatherResponseDto
    lateinit var geocoder: Geocoder
    lateinit var address: List<Address>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        lang = WeatherUtil.getCurrentLanguage(requireContext())

        viewModel._detailsSelectedItem.observe(viewLifecycleOwner,
            object : Observer<WeatherResponseDto> {
                override fun onChanged(t: WeatherResponseDto?) {
                    Log.d("FragmentFavourite", "onChanged: ${t.toString()} ")
                }
            })
        geocoder = Geocoder(context, Locale.getDefault())
        hourlyAdapter = HourlyAdapter(arrayListOf())
        dailyAdapter = DailyAdapter(arrayListOf())
        initRecyclers()
        val str = arguments?.getString("weatherString")
        receivedWeatherDataDto = Gson().fromJson(str, WeatherResponseDto::class.java)
        if (WeatherUtil.checkForInternetIsConnected(requireContext())) {
            lifecycle.coroutineScope.launch {
                weatherResponseDto =
                    viewModel.makeRequest(
                        receivedWeatherDataDto.lat,
                        receivedWeatherDataDto.lon,
                        receivedWeatherDataDto.timezone,
                        lang
                    ).body()!!

                updateUI(weatherResponseDto)
                binding.animationView.visibility = View.GONE
            }
        } else
            updateUI(receivedWeatherDataDto)

        viewModel.detailsSelectedItem.observe(viewLifecycleOwner) {
            Log.d("FragmentFavourite", "onChanged: ${it.toString()} ")
        }


        return root
    }

    private fun initRecyclers() {
        binding.hoursRecyclerView.apply {
            adapter = hourlyAdapter
        }
        binding.daysRecyclerView.apply {
            adapter = dailyAdapter
        }
    }

    private fun updateUI(weatherResponseDto: WeatherResponseDto) {

        weatherResponseDto.let {
            weatherResponseDto.apply {
                val unitConverter = UnitsConverters(binding.cityNameTextView.context)
                address = geocoder.getFromLocation(lat, lon, 1)
                binding.currentTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(
                        weatherResponseDto.current.temp.toInt().toString()
                    )
                binding.cityNameTextView.text =
                    "${address[0].countryName} / ${address[0].subAdminArea}"

                binding.currentTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(
                        weatherResponseDto.current.temp.toInt().toString()
                    )
                binding.weatherDescriotionTextView.text =
                    weatherResponseDto.current.weather[0].description
                binding.feelsLikeTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(
                        weatherResponseDto.current.feels_like.toInt().toString()
                    )
                binding.lowestAndHighestTemp.text =
                    "H:${
                        unitConverter.returnTemperatureUsingUserFormat(
                            weatherResponseDto.daily[0].temp.max.toInt().toString()
                        )
                    } L:${
                        unitConverter.returnTemperatureUsingUserFormat(
                            weatherResponseDto.daily[0].temp.min.toInt().toString()
                        )
                    }"
                "${weatherResponseDto.current.humidity} %".also {
                    binding.humidityTextView.text = it
                }
                ((weatherResponseDto.current.visibility / 1000).toString() + " KM").also {
                    binding.visibilityValueTextView.text = it
                }

                unitConverter.returnWindSpeedUsingUserFormat((weatherResponseDto.current.wind_speed / 1000).toString())
                    .also {
                        binding.windSpeedValueTextView.text = it
                    }
                binding.feelsLikeTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(weatherResponseDto.current.feels_like.toInt().toString())
                binding.pressureValueTextView.text = weatherResponseDto.current.pressure.toString()
                dailyAdapter.changeData(daily)
                hourlyAdapter.changeData(hourly)


            }
            binding.animationView.visibility = View.GONE
        }
    }
}
