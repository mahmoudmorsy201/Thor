package com.example.thorweather.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thorweather.R
import com.example.thorweather.data.model.Hourly
import com.example.thorweather.databinding.TemperaturesHourItemViewBinding
import com.example.thorweather.util.Constants.IMG_URL
import com.example.thorweather.util.ImageHelper
import com.example.thorweather.util.SharedPreferencesFactory
import com.example.thorweather.util.UnitsConverters
import com.example.thorweather.util.WeatherUtil.getHourAndMinute
import kotlin.collections.ArrayList

class HourlyAdapter(var hourlyList: ArrayList<Hourly>) :
    RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    lateinit var sharedPreferencesFactory: SharedPreferencesFactory

    class HourlyViewHolder(var view: TemperaturesHourItemViewBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding = TemperaturesHourItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        sharedPreferencesFactory = SharedPreferencesFactory(parent.context)
        return HourlyViewHolder(binding)
    }

    fun changeData(newList: List<Hourly>) {
        hourlyList.clear()
        val list = newList.slice(0..23)
        hourlyList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = hourlyList.size

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = hourlyList[position]
        val unitConverter = UnitsConverters(context = holder.view.hourTemperatureTextView.context)
        if (position == 0) {
            holder.view.timeTextView.text =
                "  " + holder.view.hourTemperatureTextView.context.getString(R.string.current) + "  "
        } else {
            holder.view.timeTextView.text =
                getHourAndMinute(item.dt.toLong(),sharedPreferencesFactory.getLanguage().toString())
        }

        val imageHelper = ImageHelper()
        imageHelper.loadUrl(
            "${IMG_URL}${item.weather[0].icon}@2x.png",
            holder.view.weatherStatusImageView
        )
        holder.view.hourTemperatureTextView.text =
            unitConverter.returnTemperatureUsingUserFormat(item.temp.toInt().toString())


    }

}