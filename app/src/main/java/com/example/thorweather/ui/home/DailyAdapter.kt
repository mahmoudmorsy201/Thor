package com.example.thorweather.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thorweather.data.model.Daily
import com.example.thorweather.databinding.WeekDaysItemViewBinding
import com.example.thorweather.util.*

class DailyAdapter(var dailyList: ArrayList<Daily>) :
    RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    lateinit var sharedPreferencesFactory: SharedPreferencesFactory

    class DailyViewHolder(var view: WeekDaysItemViewBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = WeekDaysItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        sharedPreferencesFactory = SharedPreferencesFactory(parent.context)
        return DailyViewHolder(binding)
    }

    fun changeData(newList: List<Daily>?) {
        dailyList.clear()
        if (newList != null) {
            dailyList.addAll(newList)
            dailyList.removeAt(0)
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dailyList.size

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = dailyList[position]
        val imgHelper = ImageHelper()
        holder.view.dateForWeekDaysTextView.text =
            WeatherUtil.convertTimestampToDate(item.dt.toLong(),sharedPreferencesFactory.getLanguage().toString())
        imgHelper.loadUrl(
            "${Constants.IMG_URL}${item.weather[0].icon}@2x.png",
            holder.view.iconForWeekDaysImageView
        )
        val unitConverter = UnitsConverters(context = holder.view.higherTemperatureTextView.context)

        holder.view.lowTemperatureTextView.text = unitConverter.returnTemperatureUsingUserFormat(item.temp.min.toInt().toString())
        holder.view.higherTemperatureTextView.text = unitConverter.returnTemperatureUsingUserFormat(item.temp.max.toInt().toString())

    }


}