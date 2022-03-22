package com.example.thorweather.ui.favourite

import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thorweather.data.model.WeatherResponseDto
import com.example.thorweather.databinding.ItemFavouriteBinding
import com.example.thorweather.util.Constants
import com.example.thorweather.util.ImageHelper
import com.example.thorweather.util.UnitsConverters
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class
FavouriteAdapter(
    var favouriteList: ArrayList<WeatherResponseDto>,
    favouritesViewModel: FavouriteViewModel
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {
    class FavouriteViewHolder(var myView: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(myView.root)

    var imgHelpers = ImageHelper()

    private var removedPosition = 0
    lateinit var removedObject: WeatherResponseDto
    private var favouriteViewModel = favouritesViewModel
    lateinit var geocoder: Geocoder
    lateinit var address: List<Address>
    lateinit var countryName: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding =
            ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        geocoder = Geocoder(parent.context, Locale.getDefault())

        return FavouriteViewHolder(binding)
    }

    override fun getItemCount(): Int = favouriteList.size

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val unitsConverters = UnitsConverters(context = holder.myView.favCityTextivew.context)
        val item = favouriteList[position]
        address = geocoder.getFromLocation(item.lat, item.lon, 1)
        countryName = "${address[0].countryName} / ${address[0].subAdminArea}"
        holder.myView.favCityTextivew.text = item.timezone.substringAfterLast("/")
        holder.myView.favCountryTextview.text = countryName
        holder.myView.favDescriptionTextView.text = item.current.weather[0].description
        holder.myView.favTempTextView.text = unitsConverters.returnTemperatureUsingUserFormat(item.current.temp.toInt().toString())

        imgHelpers.loadUrl(
            "${Constants.IMG_URL + item.current.weather[0].icon}@2x.png",
            holder.myView.favWeatherImageView
        )
        holder.myView.itemFavouriteContainer.setOnClickListener {
            onItemClickListener?.onClick(position, item)
        }

    }

    fun changeData(newList: List<WeatherResponseDto>) {
        favouriteList.clear()
        favouriteList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeFromAdapter(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedObject = favouriteList[viewHolder.adapterPosition]
        favouriteList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        Snackbar.make(
            viewHolder.itemView,
            "${removedObject.timezone} removed",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Undo") {
                favouriteList.add(removedPosition, removedObject)
                notifyItemInserted(removedPosition)
            }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        favouriteViewModel.deleteWeatherObjectByTimeZone(removedObject.timezone)
                    }
                }
            })
        }.show()
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(pos: Int, weatherResponseDto: WeatherResponseDto)
    }


}