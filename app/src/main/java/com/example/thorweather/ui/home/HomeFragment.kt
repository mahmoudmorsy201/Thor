package com.example.thorweather.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.databinding.FragmentHomeBinding
import com.example.thorweather.util.Constants.PERMISSION_REQUEST_ACCESS_LOCATION
import com.example.thorweather.util.WeatherUtil.checkForInternetIsConnected


import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.thorweather.R
import com.example.thorweather.util.SharedPreferencesFactory

import com.example.thorweather.util.UnitsConverters


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    private val viewModel: HomeViewModel by viewModels()

    private val binding get() = _binding!!
    lateinit var weatherResponseDto: WeatherResponseDto
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var geocoder: Geocoder
    lateinit var address: List<Address>
    lateinit var currentCity: String
    lateinit var lang: String
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val shared = SharedPreferencesFactory(requireContext())
        lang = shared.getLanguage()!!
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        hourlyAdapter = HourlyAdapter(arrayListOf())
        dailyAdapter = DailyAdapter(arrayListOf())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        initRecyclersViews()

        if (checkForInternetIsConnected(requireContext())) {
            binding.animationView.visibility = View.VISIBLE
            lifecycle.coroutineScope.launch {
                getLastLocation()
            }

        } else {

            viewModel.getStoredWeatherResponse().asLiveData().observe(viewLifecycleOwner) {
                if(it != null)  {
                    binding.animationView.visibility = View.GONE
                    updateUI(it, it.timezone)
                }else {
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_LONG)
                        .show()
                }


            }

        }

        return root
    }

    private fun initRecyclersViews() {
        binding.hoursRecyclerView.apply {
            adapter = hourlyAdapter
        }
        binding.daysRecyclerView.apply {
            adapter = dailyAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun updateUI(weatherResponseDto: WeatherResponseDto, currentCity: String) {

        weatherResponseDto.let {
            weatherResponseDto.apply {
                val unitConverter = UnitsConverters(binding.cityNameTextView.context)
                binding.currentTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(
                        weatherResponseDto.current.temp.toInt().toString()
                    )
                binding.cityNameTextView.text = currentCity.split("Governorate")[0]
                binding.weatherDescriotionTextView.text =
                    weatherResponseDto.current.weather[0].description.uppercase()
                binding.weatherFullDescription.text = weatherResponseDto.current.weather[0].main
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

                unitConverter.returnWindSpeedUsingUserFormat((weatherResponseDto.current.wind_speed).toString())
                    .also {
                        binding.windSpeedValueTextView.text = it
                    }
                binding.feelsLikeTemperatureTextView.text =
                    unitConverter.returnTemperatureUsingUserFormat(
                        weatherResponseDto.current.feels_like.toInt().toString()
                    )
                binding.pressureValueTextView.text =
                    weatherResponseDto.current.pressure.toString() + " hPa"
                dailyAdapter.changeData(daily)
                hourlyAdapter.changeData(hourly)
            }


        }

    }

    suspend fun <T> Task<T>.awaitResult() = suspendCoroutine<T?> { continuation ->
        if (isComplete) {
            if (isSuccessful) continuation.resume(result)
            else continuation.resume(null)
            return@suspendCoroutine
        }
        addOnSuccessListener { continuation.resume(result) }
        addOnFailureListener { continuation.resume(null) }
        addOnCanceledListener { continuation.resume(null) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.awaitResult().let {
                    if (it == null) {
                        requestNewLocationData()
                    } else {
                        currentLat = it.latitude
                        currentLon = it.longitude
                        address = geocoder.getFromLocation(currentLat, currentLon, 1)
                        currentCity = address[0].subAdminArea
                        viewLifecycleOwner.lifecycleScope.launch {
                            weatherResponseDto =
                                viewModel.makeRequest(currentLat, currentLon, currentCity, lang)
                                    .body()!!
                            updateUI(weatherResponseDto, currentCity)
                            binding.animationView.visibility = View.GONE
                        }
                    }
                }
            } else {
                locationNotEnable()
            }
        } else {
            requestPermissions()
        }

    }


    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation


        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getActivity()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun locationNotEnable() {

        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        ActivityCompat.startActivityForResult(
            requireActivity(),
            intent,
            PERMISSION_REQUEST_ACCESS_LOCATION,
            Bundle()
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                lifecycle.coroutineScope.launch {
                    getLastLocation()
                }

            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}