package com.example.thorweather.ui.favourite

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.thorweather.R
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.databinding.FragmentFavouriteBinding
import com.example.thorweather.util.WeatherUtil
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FavouriteFragment : Fragment(R.layout.fragment_favourite) {

    private var _binding: FragmentFavouriteBinding? = null
    private val viewModel: FavouriteViewModel by viewModels()
    private lateinit var favoriteAdapter: FavouriteAdapter
    lateinit var geocoder: Geocoder
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        favoriteAdapter = FavouriteAdapter(arrayListOf(), viewModel)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        isConnectedToInternet()

        binding.addNewFavourite.setOnClickListener {
            viewModel.addFavouritePlace(it)
        }

        if (viewModel.getFavouriteListFromRoom(true) != emptyArray<WeatherResponseDto>()) {
            viewModel.getFavouriteListFromRoom(true).asLiveData().observe(viewLifecycleOwner) {
                favoriteAdapter.changeData(it)
            }
        }

        initUI()
        return root
    }

    private fun initUI() {
        binding.rvFavourites.apply {
            adapter = favoriteAdapter
        }
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                favoriteAdapter.removeFromAdapter(viewHolder)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavourites)


        favoriteAdapter.onItemClickListener = object : FavouriteAdapter.OnItemClickListener {
            override fun onClick(pos: Int, weatherResponseDto: WeatherResponseDto) {
                viewModel.setDetailsForItemSelected(weatherResponseDto)
              val  weatherString = Gson().toJson(weatherResponseDto)
                val bundle: Bundle = Bundle()
                bundle.putString("weatherString", weatherString)


                findNavController().navigate(R.id.action_navigation_favourite_to_navigation_FavouriteFragment,bundle)
                  }

        }

    }

    private fun isConnectedToInternet() {
        if (WeatherUtil.checkForInternetIsConnected(requireContext())) {
            binding.addNewFavourite.visibility = View.VISIBLE
            binding.noInternetView.visibility = View.GONE
        }else {
            binding.addNewFavourite.visibility = View.GONE
            binding.noInternetView.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}