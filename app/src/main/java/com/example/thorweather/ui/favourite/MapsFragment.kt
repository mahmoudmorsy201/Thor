package com.example.thorweather.ui.favourite

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.thorweather.R
import com.example.thorweather.databinding.FragmentMapsBinding
import com.example.thorweather.util.SharedPreferencesFactory
import com.example.thorweather.util.WeatherUtil.getCurrentLanguage

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MapsFragment : Fragment(), GoogleMap.OnMapClickListener {


    private val viewModel: MapsFragmentViewModel by viewModels()

    private lateinit var map: GoogleMap
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var zoomLevel: Float = 10f
    private val autoCompleteRequestCode = 500
    private lateinit var geocoder: Geocoder
    lateinit var lang: String
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.setOnMapClickListener(this)
        map.setOnCameraChangeListener() { position ->
            run {
                zoomLevel = position.zoom
            }
        }
        val location = SharedPreferencesFactory(requireContext()).getLatLng()
        googleMap.addMarker(MarkerOptions().position(location))
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        lang = getCurrentLanguage(requireContext())

        geocoder = Geocoder(context, Locale.getDefault())

        binding.searchImageView.setOnClickListener() {
            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), getString(R.string.maps_api_key), Locale.US)
            }

            val fields = listOf(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())
            startActivityForResult(intent, autoCompleteRequestCode)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == autoCompleteRequestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        addMapMarker(place.latLng)
                        showDialogForLocation(LatLng(lat, lng))
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                }
                Activity.RESULT_CANCELED -> {
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun showDialogForLocation(latLng: LatLng) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.selected_location))
        alertDialogBuilder.setMessage(getString(R.string.to_select_location))
        alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            run {

                val address: List<Address> = geocoder.getFromLocation(lat, lng, 1)

                lifecycle.coroutineScope.launch {
                    viewModel.insertFavLocation(
                        latLng.latitude,
                        latLng.longitude,
                        currentCity = address[0].subAdminArea,
                        lang
                    )
                    findNavController().navigate(
                        R.id.action_navigation_map_to_navigation_favourite,

                        )
                }


            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            run {
                dialog.dismiss()
            }
        }
        alertDialogBuilder.show()
    }

    private fun addMapMarker(point: LatLng) {
        map.clear()
        map.addMarker(MarkerOptions().position(point))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10f))
        lat = point.latitude
        lng = point.longitude
    }

    override fun onMapClick(point: LatLng) {
        addMapMarker(point)
        showDialogForLocation(point)
    }

}